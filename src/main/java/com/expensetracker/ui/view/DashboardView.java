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
	
	private final VBox toastContainer = new VBox(10);

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
		
		amountTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue && !amountTextField.getText().isBlank()) {
				try {
					double amount = Double.parseDouble(amountTextField.getText().replace("$", "").replace(",", "").trim());
					amountTextField.setText(String.format("$%.2f", amount));
				} catch (NumberFormatException ignored) {
					// Ignore invalid numbers during focus loss, wait for them to hit Add Expense to show an error
				}
			}
		});

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
		welcomeLabel.getStyleClass().add("subtitle-label");

		Button logoutButton = new Button("Logout");
		logoutButton.setId("logoutButton");
		logoutButton.getStyleClass().addAll("button", "secondary-button");
		logoutButton.setOnAction(event -> SceneNavigator.switchScene(new LoginView(springContext).createView(), "Personal Expense Tracker"));

		HBox topBar = new HBox(12, welcomeLabel, logoutButton);
		topBar.setAlignment(Pos.CENTER_LEFT);
		topBar.setPadding(new Insets(16, 24, 16, 24));
		topBar.getStyleClass().add("top-bar");
		HBox.setHgrow(welcomeLabel, Priority.ALWAYS);

		configureExpenseTable();
		configureCharts();
		refreshCategories();
		refreshExpenseData();

		VBox formSection = buildExpenseFormSection();
		VBox tableSection = buildExpenseTableSection();
		VBox chartSection = buildChartSection();

		VBox mainContent = new VBox(24, chartSection, tableSection);
		mainContent.setPadding(new Insets(24));
		ScrollPane scrollPane = new ScrollPane(mainContent);
		scrollPane.setFitToWidth(true);
		scrollPane.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent;");

		BorderPane rootContainer = new BorderPane();
		rootContainer.setTop(topBar);
		rootContainer.setLeft(formSection);
		rootContainer.setCenter(scrollPane);
		rootContainer.getStyleClass().add("root");

		toastContainer.setAlignment(Pos.BOTTOM_RIGHT);
		toastContainer.setPadding(new Insets(24));
		toastContainer.setPickOnBounds(false);

		javafx.scene.layout.StackPane stackRoot = new javafx.scene.layout.StackPane(rootContainer, toastContainer);
		return stackRoot;
	}

	/*
	 * Builds the section for the expense entry form, including input fields and action buttons.
	 * @return the VBox containing the expense form section
	 */
	private VBox buildExpenseFormSection() {
		Label formTitleLabel = new Label("Expense Entry");
		formTitleLabel.getStyleClass().add("subtitle-label");

		amountTextField.setPromptText("Amount");
		descriptionTextField.setPromptText("Description");
		searchTextField.setPromptText("Search by category, description, or date");

		Button addExpenseButton = new Button("Add Expense");
		addExpenseButton.setId("addExpenseButton");
		addExpenseButton.getStyleClass().addAll("button", "primary-button");
		addExpenseButton.setMaxWidth(Double.MAX_VALUE);
		addExpenseButton.setOnAction(event -> addExpense());

		Button updateSelectedExpenseButton = new Button("Update Expense");
		updateSelectedExpenseButton.setId("updateButton");
		updateSelectedExpenseButton.getStyleClass().addAll("button", "primary-button");
		updateSelectedExpenseButton.setMaxWidth(Double.MAX_VALUE);
		updateSelectedExpenseButton.setOnAction(event -> updateSelectedExpense());

		Button cancelEditButton = new Button("Cancel Edit");
		cancelEditButton.getStyleClass().addAll("button", "secondary-button");
		cancelEditButton.setMaxWidth(Double.MAX_VALUE);
		cancelEditButton.setOnAction(event -> clearForm());

		// Toggle visibility based on selection
		updateSelectedExpenseButton.visibleProperty().bind(expenseTableView.getSelectionModel().selectedItemProperty().isNotNull());
		updateSelectedExpenseButton.managedProperty().bind(updateSelectedExpenseButton.visibleProperty());
		cancelEditButton.visibleProperty().bind(updateSelectedExpenseButton.visibleProperty());
		cancelEditButton.managedProperty().bind(cancelEditButton.visibleProperty());

		addExpenseButton.visibleProperty().bind(expenseTableView.getSelectionModel().selectedItemProperty().isNull());
		addExpenseButton.managedProperty().bind(addExpenseButton.visibleProperty());

		Button addCategoryButton = new Button("+ Custom Category");
		addCategoryButton.setId("addCategoryButton");
		addCategoryButton.getStyleClass().addAll("button", "secondary-button");
		addCategoryButton.setMaxWidth(Double.MAX_VALUE);
		addCategoryButton.setOnAction(event -> addCustomCategory());

		Button searchButton = new Button("Search");
		searchButton.setId("searchButton");
		searchButton.getStyleClass().addAll("button", "primary-button");
		searchButton.setMaxWidth(Double.MAX_VALUE);
		searchButton.setOnAction(event -> refreshExpenseData());

		GridPane formGridPane = new GridPane();
		formGridPane.setHgap(8);
		formGridPane.setVgap(12);

		formGridPane.add(new Label("Category"), 0, 0);
		formGridPane.add(categoryComboBox, 1, 0);
		formGridPane.add(new Label("Amount"), 0, 1);
		formGridPane.add(amountTextField, 1, 1);
		formGridPane.add(new Label("Description"), 0, 2);
		formGridPane.add(descriptionTextField, 1, 2);
		formGridPane.add(new Label("Date"), 0, 3);
		formGridPane.add(transactionDatePicker, 1, 3);

		VBox formSection = new VBox(
			16,
			formTitleLabel,
			formGridPane,
			addExpenseButton,
			updateSelectedExpenseButton,
			cancelEditButton,
			new Separator(),
			addCategoryButton,
			new Separator(),
			new Label("Search"),
			searchTextField,
			searchButton
		);
		formSection.setPadding(new Insets(24));
		formSection.setPrefWidth(320);
		formSection.getStyleClass().add("sidebar");

		return formSection;
	}

	/*
	 * Builds the section for displaying the expense table, including the total amount label and the table view itself.
	 * @return the VBox containing the expense table section
	 */
	private VBox buildExpenseTableSection() {
		Label tableTitleLabel = new Label("Expenses");
		tableTitleLabel.getStyleClass().add("subtitle-label");

		totalLabel.getStyleClass().add("subtitle-label");

		expenseTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedExpense) -> {
			if (selectedExpense != null) {
				amountTextField.setText(String.format("%.2f", selectedExpense.getAmount()));
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

		VBox tableSection = new VBox(16, tableTitleLabel, totalLabel, expenseTableView);
		tableSection.getStyleClass().add("card");
		VBox.setVgrow(expenseTableView, Priority.ALWAYS);
		return tableSection;
	}

	/*
	 * Builds the section for displaying the charts, including a pie chart for expense breakdown and a bar chart for spending by category.
	 * @return the VBox containing the charts section
	 */
	private VBox buildChartSection() {
		Label chartTitleLabel = new Label("Reports");
		chartTitleLabel.getStyleClass().add("subtitle-label");

		categoryPieChart.setLegendVisible(true);
		categoryBarChart.setLegendVisible(false);
		categoryBarChart.setTitle("Spending by Category");

		HBox charts = new HBox(24, categoryPieChart, categoryBarChart);
		HBox.setHgrow(categoryPieChart, Priority.ALWAYS);
		HBox.setHgrow(categoryBarChart, Priority.ALWAYS);

		VBox chartSection = new VBox(16, chartTitleLabel, charts);
		chartSection.getStyleClass().add("card");
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
		amountColumn.setCellFactory(column -> new TableCell<Expense, Double>() {
			@Override
			protected void updateItem(Double amount, boolean empty) {
				super.updateItem(amount, empty);
				if (empty || amount == null) {
					setText(null);
				} else {
					setText(String.format("$%.2f", amount));
				}
			}
		});

		TableColumn<Expense, String> descriptionColumn = new TableColumn<>("Description");
		descriptionColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDescription()));

		TableColumn<Expense, Void> actionsColumn = new TableColumn<>("Actions");
		actionsColumn.setCellFactory(param -> new TableCell<>() {
			private final Button editBtn = new Button("✎ Edit");
			private final Button deleteBtn = new Button("✕ Delete");
			private final HBox pane = new HBox(8, editBtn, deleteBtn);

			{
				editBtn.getStyleClass().add("action-button");
				deleteBtn.getStyleClass().addAll("action-button", "danger-button");

				editBtn.setOnAction(event -> {
					Expense expense = getTableView().getItems().get(getIndex());
					expenseTableView.getSelectionModel().select(expense);
				});

				deleteBtn.setOnAction(event -> {
					Expense expense = getTableView().getItems().get(getIndex());
					expenseTableView.getSelectionModel().select(expense);
					deleteSelectedExpense();
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					setGraphic(pane);
				}
			}
		});

		expenseTableView.getColumns().addAll(idColumn, dateColumn, categoryColumn, amountColumn, descriptionColumn, actionsColumn);
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
			double amount = Double.parseDouble(amountTextField.getText().replace("$", "").replace(",", "").trim());
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
			showToast("Expense added successfully!", false);
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
			double amount = Double.parseDouble(amountTextField.getText().replace("$", "").replace(",", "").trim());
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
			showToast("Expense updated successfully!", false);
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
			showToast("Expense deleted.", false);
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
		showToast(message, true);
	}

	private void showToast(String message, boolean isError) {
		javafx.application.Platform.runLater(() -> {
			Label toast = new Label(message);
			toast.getStyleClass().add(isError ? "toast-error" : "toast-success");
			toastContainer.getChildren().add(toast);

			javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(4));
			delay.setOnFinished(e -> toastContainer.getChildren().remove(toast));
			delay.play();
		});
	}
}