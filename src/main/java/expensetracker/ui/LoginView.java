package expensetracker.ui;

import expensetracker.SceneNavigator;
import expensetracker.model.User;
import expensetracker.service.AuthenticationService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.springframework.context.ConfigurableApplicationContext;

public class LoginView {
	private final ConfigurableApplicationContext springContext;
	private final AuthenticationService authenticationService;

	public LoginView(ConfigurableApplicationContext springContext) {
		this.springContext = springContext;
		this.authenticationService = springContext.getBean(AuthenticationService.class);
	}

	public Parent createView() {
		Label titleLabel = new Label("Personal Expense Tracker");
		titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

		TextField emailTextField = new TextField();
		emailTextField.setPromptText("Email");
		emailTextField.setMaxWidth(320);

		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Password");
		passwordField.setMaxWidth(320);

		Label statusLabel = new Label();

		Button loginButton = new Button("Login");
		loginButton.setDefaultButton(true);
		loginButton.setOnAction(event -> {
			User authenticatedUser = authenticationService.authenticateUser(
				emailTextField.getText(),
				passwordField.getText()
			);

			if (authenticatedUser != null) {
				DashboardView dashboardView = new DashboardView(authenticatedUser, springContext);
				SceneNavigator.switchScene(dashboardView.createView(), "Dashboard - Personal Expense Tracker");
			} else {
				statusLabel.setText("Invalid email or password.");
			}
		});

		Button registerButton = new Button("Register");
		registerButton.setOnAction(event -> {
			User registeredUser = authenticationService.registerUser(
				emailTextField.getText(),
				passwordField.getText()
			);

			if (registeredUser != null) {
				DashboardView dashboardView = new DashboardView(registeredUser, springContext);
				SceneNavigator.switchScene(dashboardView.createView(), "Dashboard - Personal Expense Tracker");
			} else {
				statusLabel.setText("Registration failed. Email may already exist.");
			}
		});

		VBox rootContainer = new VBox(14, titleLabel, emailTextField, passwordField, loginButton, registerButton, statusLabel);
		rootContainer.setPadding(new Insets(40));
		rootContainer.setAlignment(Pos.CENTER);

		return rootContainer;
	}
}