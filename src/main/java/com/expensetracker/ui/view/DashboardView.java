package com.expensetracker.ui.view;

import com.expensetracker.ui.util.SceneNavigator;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.service.CategoryService;
import com.expensetracker.service.ExpenseService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * A view for displaying the user's dashboard, including expense entries, charts, and summary information.
 */
public class DashboardView {
	private final User currentUser;
	private final ConfigurableApplicationContext springContext;
	private final ExpenseService expenseService;
	private final CategoryService categoryService;

	private final TableView<Expense> expenseTableView = new TableView<>();
	private final ComboBox<Category> categoryComboBox = new ComboBox<>();
	private final TextField amountTextField = new TextField();
	private final TextField descriptionTextField = new TextField();
	private final DatePicker transactionDatePicker = new DatePicker(LocalDate.now());
	private final TextField searchTextField = new TextField();
	private final Label totalLabel = new Label();
	private final PieChart categoryPieChart = new PieChart();
	private final BarChart<String, Number> categoryBarChart = new BarChart<>(new CategoryAxis(), new NumberAxis());

	/**
	 * Constructs a new DashboardView for the specified user and Spring application context. The constructor initializes the necessary services and UI components for displaying the dashboard.
	 * @param currentUser the currently logged-in user for whom to display the dashboard
	 * @param springContext the Spring application context to access services and manage dependencies
	 */
	public DashboardView(User currentUser, ConfigurableApplicationContext springContext) {
		this.currentUser = currentUser;
		this.springContext = springContext;
		this.expenseService = springContext.getBean(ExpenseService.class);
		this.categoryService = springContext.getBean(CategoryService.class);

		// Initialize IDs
		expenseTableView.setId("expenseTable");
		categoryComboBox.setId("categoryCombo");
		amountTextField.setId("amountField");
		descriptionTextField.setId("descriptionField");
		transactionDatePicker.setId("datePicker");
		searchTextField.setId("searchField");
		totalLabel.setId("totalLabel");
	}

	/**
	 * Creates and returns the main view for the dashboard, including the top bar with user information and logout button, the expense entry form, the expense table, and the charts. This method sets up all UI components and their event handlers to provide a functional dashboard experience for the user.
	 * @return the root Parent node containing the entire dashboard view
	 */
	public Parent createView() {
		Label welcomeLabel = new Label("Logged in as: " + currentUser.getEmail());
		welcomeLabel.setId("welcomeLabel");
		welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		Button logoutButton = new Button("Logout");
		logoutButton.setId("logoutButton");
		logoutButton.setOnAction(event -> SceneNavigator.switchScene(new LoginView(springContext).createView(), "Personal Expense Tracker"));

		HBox topBar = new HBox(12, welcomeLabel, logoutButton);
		topBar.setAlignment(Pos.CENTER_LEFT);
		topBar.setPadding(new Insets(12));

		configureExpenseTable();
		configureCharts();
		refreshCategories();
		refreshExpenseData();

		VBox formSection = buildExpenseFormSection();
		VBox tableSection = buildExpenseTableSection();
		VBox chartSection = buildChartSection();

		SplitPane splitPane = new SplitPane();
		splitPane.getItems().addAll(formSection, tableSection, chartSection);
		splitPane.setDividerPositions(0.26, 0.67);

		BorderPane rootContainer = new BorderPane();
		rootContainer.setTop(topBar);
		rootContainer.setCenter(splitPane);

		return rootContainer;
	}

	/*
	 * Builds the section for the expense entry form, including input fields and action buttons.
	 * @return the VBox containing the expense form section
	 */
	private VBox buildExpenseFormSection() {
		Label formTitleLabel = new Label("Expense Entry");
		formTitleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

		amountTextField.setPromptText("Amount");
		descriptionTextField.setPromptText("Description");
		searchTextField.setPromptText("Search by category, description, or date");

		Button addExpenseButton = new Button("Add Expense");
		addExpenseButton.setId("addExpenseButton");
		addExpenseButton.setMaxWidth(Double.MAX_VALUE);
		addExpenseButton.setOnAction(event -> addExpense());

		Button updateSelectedExpenseButton = new Button("Update Selected");
		updateSelectedExpenseButton.setId("updateButton");
		updateSelectedExpenseButton.setMaxWidth(Double.MAX_VALUE);
		updateSelectedExpenseButton.setOnAction(event -> updateSelectedExpense());

		Button deleteSelectedExpenseButton = new Button("Delete Selected");
		deleteSelectedExpenseButton.setId("deleteButton");
		deleteSelectedExpenseButton.setMaxWidth(Double.MAX_VALUE);
		deleteSelectedExpenseButton.setOnAction(event -> deleteSelectedExpense());

		Button addCategoryButton = new Button("Add Custom Category");
		addCategoryButton.setId("addCategoryButton");
		addCategoryButton.setMaxWidth(Double.MAX_VALUE);
		addCategoryButton.setOnAction(event -> addCustomCategory());

		Button searchButton = new Button("Search");
		searchButton.setId("searchButton");
		searchButton.setMaxWidth(Double.MAX_VALUE);
		searchButton.setOnAction(event -> refreshExpenseData());

		GridPane formGridPane = new GridPane();
		formGridPane.setHgap(8);
		formGridPane.setVgap(8);

		formGridPane.add(new Label("Category"), 0, 0);
		formGridPane.add(categoryComboBox, 1, 0);
		formGridPane.add(new Label("Amount"), 0, 1);
		formGridPane.add(amountTextField, 1, 1);
		formGridPane.add(new Label("Description"), 0, 2);
		formGridPane.add(descriptionTextField, 1, 2);
		formGridPane.add(new Label("Date"), 0, 3);
		formGridPane.add(transactionDatePicker, 1, 3);

		VBox formSection = new VBox(
			10,
			formTitleLabel,
			formGridPane,
			addExpenseButton,
			updateSelectedExpenseButton,
			deleteSelectedExpenseButton,
			new Separator(),
			addCategoryButton,
			new Separator(),
			new Label("Search"),
			searchTextField,
			searchButton
		);
		formSection.setPadding(new Insets(16));

		return formSection;
	}

	/*
	 * Builds the section for displaying the expense table, including the total amount label and the table view itself.
	 * @return the VBox containing the expense table section
	 */
	private VBox buildExpenseTableSection() {
		Label tableTitleLabel = new Label("Expenses");
		tableTitleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

		totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		expenseTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedExpense) -> {
			if (selectedExpense != null) {
				amountTextField.setText(String.valueOf(selectedExpense.getAmount()));
				descriptionTextField.setText(selectedExpense.getDescription());
				transactionDatePicker.setValue(LocalDate.parse(selectedExpense.getTransactionDate()));

				for (Category category : categoryComboBox.getItems()) {
					if (category.getId() == selectedExpense.getCategoryId()) {
						categoryComboBox.setValue(category);
						break;
					}
				}
			}
		});

		VBox tableSection = new VBox(10, tableTitleLabel, totalLabel, expenseTableView);
		tableSection.setPadding(new Insets(16));
		VBox.setVgrow(expenseTableView, Priority.ALWAYS);
		return tableSection;
	}

	/*
	 * Builds the section for displaying the charts, including a pie chart for expense breakdown and a bar chart for spending by category.
	 * @return the VBox containing the charts section
	 */
	private VBox buildChartSection() {
		Label chartTitleLabel = new Label("Reports");
		chartTitleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

		categoryPieChart.setLegendVisible(true);
		categoryBarChart.setLegendVisible(false);
		categoryBarChart.setTitle("Spending by Category");

		VBox chartSection = new VBox(10, chartTitleLabel, categoryPieChart, categoryBarChart);
		chartSection.setPadding(new Insets(16));
		VBox.setVgrow(categoryPieChart, Priority.ALWAYS);
		VBox.setVgrow(categoryBarChart, Priority.ALWAYS);
		return chartSection;
	}

	/*
	 * Configures the expense table by setting up its columns and data binding.
	 */
	private void configureExpenseTable() {
		TableColumn<Expense, Integer> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getId()));

		TableColumn<Expense, String> dateColumn = new TableColumn<>("Date");
		dateColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getTransactionDate()));

		TableColumn<Expense, String> categoryColumn = new TableColumn<>("Category");
		categoryColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCategoryName()));

		TableColumn<Expense, Double> amountColumn = new TableColumn<>("Amount");
		amountColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getAmount()));

		TableColumn<Expense, String> descriptionColumn = new TableColumn<>("Description");
		descriptionColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDescription()));

		expenseTableView.getColumns().addAll(idColumn, dateColumn, categoryColumn, amountColumn, descriptionColumn);
		expenseTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
	}

	/*
	 * Configures the charts by setting titles and other properties.
	 */
	private void configureCharts() {
		categoryPieChart.setTitle("Expense Breakdown");
	}

	/*
	 * Refreshes the list of categories in the category combo box by fetching the latest categories for the user from the CategoryService.
	 */
	private void refreshCategories() {
		List<Category> categories = categoryService.getCategoriesForUser(currentUser.getId());
		categoryComboBox.setItems(FXCollections.observableArrayList(categories));
		if (!categories.isEmpty()) {
			categoryComboBox.setValue(categories.get(0));
		}
	}

	/*
	 * Refreshes the expense data displayed in the table and updates the total amount label and charts based on the latest data for the user.
	 */
	private void refreshExpenseData() {
		List<Expense> expenses = expenseService.getExpensesForUser(currentUser.getId(), searchTextField.getText());
		expenseTableView.setItems(FXCollections.observableArrayList(expenses));

		double totalExpenseAmount = expenseService.getTotalExpenseAmountForUser(currentUser.getId());
		totalLabel.setText(String.format("Total Spent: $%.2f", totalExpenseAmount));

		refreshCharts();
	}

	/*
	 * Refreshes the data displayed in the charts by fetching the latest category totals for the user and updating the pie chart and bar chart accordingly.
	 */
	private void refreshCharts() {
		List<ExpenseService.CategoryTotal> categoryTotals = expenseService.getCategoryTotalsForUser(currentUser.getId());

		categoryPieChart.getData().clear();
		XYChart.Series<String, Number> barChartSeries = new XYChart.Series<>();

		for (ExpenseService.CategoryTotal categoryTotal : categoryTotals) {
			categoryPieChart.getData().add(new PieChart.Data(categoryTotal.getCategoryName(), categoryTotal.getTotalAmount()));
			barChartSeries.getData().add(new XYChart.Data<>(categoryTotal.getCategoryName(), categoryTotal.getTotalAmount()));
		}

		categoryBarChart.getData().clear();
		categoryBarChart.getData().add(barChartSeries);
	}

	/*
	 * Handles the action of adding a new expense based on the input fields in the form. Validates the input, calls the ExpenseService to add the expense, and refreshes the displayed data.
	 */
	private void addExpense() {
		Category selectedCategory = categoryComboBox.getValue();
		if (selectedCategory == null || amountTextField.getText().isBlank() || transactionDatePicker.getValue() == null) {
			showAlert("Please fill in category, amount, and date.");
			return;
		}

		try {
			double amount = Double.parseDouble(amountTextField.getText().trim());
			if (amount < 0) {
				showAlert("Amount must be zero or greater.");
				return;
			}

			expenseService.addExpense(
				currentUser.getId(),
				selectedCategory.getId(),
				amount,
				descriptionTextField.getText().trim(),
				transactionDatePicker.getValue().toString()
			);
			clearForm();
			refreshExpenseData();
		} catch (NumberFormatException exception) {
			showAlert("Amount must be a valid number.");
		}
	}

	/*
	 * Handles the action of updating the selected expense based on the input fields in the form. Validates the input, calls the ExpenseService to update the expense, and refreshes the displayed data.
	 */
	private void updateSelectedExpense() {
		Expense selectedExpense = expenseTableView.getSelectionModel().getSelectedItem();
		Category selectedCategory = categoryComboBox.getValue();

		if (selectedExpense == null || selectedCategory == null || transactionDatePicker.getValue() == null) {
			showAlert("Please select an expense to update.");
			return;
		}

		try {
			double amount = Double.parseDouble(amountTextField.getText().trim());
			if (amount < 0) {
				showAlert("Amount must be zero or greater.");
				return;
			}

			expenseService.updateExpense(
				selectedExpense.getId(),
				selectedCategory.getId(),
				amount,
				descriptionTextField.getText().trim(),
				transactionDatePicker.getValue().toString()
			);
			clearForm();
			refreshExpenseData();
		} catch (NumberFormatException exception) {
			showAlert("Amount must be a valid number.");
		}
	}

	/*
	 * Handles the action of deleting the selected expense. Prompts the user for confirmation before calling the ExpenseService to delete the expense and refreshing the displayed data.
	 */
	private void deleteSelectedExpense() {
		Expense selectedExpense = expenseTableView.getSelectionModel().getSelectedItem();
		if (selectedExpense == null) {
			showAlert("Please select an expense to delete.");
			return;
		}

		Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmationAlert.setTitle("Delete Expense");
		confirmationAlert.setHeaderText("Delete selected expense?");
		confirmationAlert.setContentText("This action cannot be undone.");

		Optional<ButtonType> buttonResult = confirmationAlert.showAndWait();
		if (buttonResult.isPresent() && buttonResult.get() == ButtonType.OK) {
			expenseService.deleteExpense(selectedExpense.getId());
			clearForm();
			refreshExpenseData();
		}
	}

	/*
	 * Handles the action of adding a new custom category by prompting the user for the category name, validating the input, calling the CategoryService to add the category, and refreshing the category list.
	 */
	private void addCustomCategory() {
		TextInputDialog categoryNameDialog = new TextInputDialog();
		categoryNameDialog.setTitle("Add Category");
		categoryNameDialog.setHeaderText("Create a custom category");
		categoryNameDialog.setContentText("Category name:");

		Optional<String> categoryNameResult = categoryNameDialog.showAndWait();
		categoryNameResult.ifPresent(categoryName -> {
			if (!categoryName.isBlank()) {
				categoryService.addCustomCategory(currentUser.getId(), categoryName.trim());
				refreshCategories();
			} else {
				showAlert("Category name cannot be blank.");
			}
		});
	}

	/*
	 * Clears the input fields in the expense form and deselects any selected expense in the table.
	 */
	private void clearForm() {
		amountTextField.clear();
		descriptionTextField.clear();
		transactionDatePicker.setValue(LocalDate.now());
		expenseTableView.getSelectionModel().clearSelection();
	}

	/*
	 * Displays an informational alert with the specified message. This method is used to show validation errors and other messages to the user.
	 * @param message the message to display in the alert
	 */
	private void showAlert(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Expense Tracker");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}