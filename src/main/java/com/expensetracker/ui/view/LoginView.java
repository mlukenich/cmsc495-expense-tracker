package com.expensetracker.ui.view;

import com.expensetracker.ui.util.SceneNavigator;
import com.expensetracker.model.User;
import com.expensetracker.service.AuthenticationService;
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
		titleLabel.setId("loginTitle");
		titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

		TextField emailTextField = new TextField();
		emailTextField.setId("emailField");
		emailTextField.setPromptText("Email");
		emailTextField.setMaxWidth(320);

		PasswordField passwordField = new PasswordField();
		passwordField.setId("passwordField");
		passwordField.setPromptText("Password");
		passwordField.setMaxWidth(320);

		Label statusLabel = new Label();
		statusLabel.setId("statusLabel");

		Button loginButton = new Button("Login");
		loginButton.setId("loginButton");
		loginButton.setDefaultButton(true);
		loginButton.setOnAction(event -> {
			try {
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
			} catch (Throwable throwable) {
				throwable.printStackTrace();
				String details = throwable.getMessage() == null ? "" : " - " + throwable.getMessage();
				statusLabel.setText("Login failed: " + throwable.getClass().getSimpleName() + details);
			}
		});

		Button registerButton = new Button("Register");
		registerButton.setId("registerButton");
		registerButton.setOnAction(event -> {
			try {
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
			} catch (Throwable throwable) {
				throwable.printStackTrace();
				String details = throwable.getMessage() == null ? "" : " - " + throwable.getMessage();
				statusLabel.setText("Registration failed: " + throwable.getClass().getSimpleName() + details);
			}
		});

		VBox rootContainer = new VBox(14, titleLabel, emailTextField, passwordField, loginButton, registerButton, statusLabel);
		rootContainer.setPadding(new Insets(40));
		rootContainer.setAlignment(Pos.CENTER);

		return rootContainer;
	}
}
