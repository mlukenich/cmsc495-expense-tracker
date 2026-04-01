package com.expensetracker.ui;

import com.expensetracker.MainApp;
import com.expensetracker.ui.util.SceneNavigator;
import com.expensetracker.ui.view.LoginView;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.api.FxAssert;
import org.testfx.matcher.control.LabeledMatchers;

import static org.testfx.api.FxAssert.verifyThat;

@SpringBootTest
@ActiveProfiles("test")
@org.junit.jupiter.api.Disabled("UI tests require a graphical environment or compatible headless setup (Monocle is incompatible with Java 21+)")
public class LoginUITest extends ApplicationTest {

	@Autowired
	private ConfigurableApplicationContext springContext;

	@Autowired
	private com.expensetracker.repository.DatabaseManager databaseManager;

	@org.junit.jupiter.api.BeforeEach
	void setUp() {
		databaseManager.initializeDatabase();
	}

	@BeforeAll
	public static void setupHeadless() {
		// Set to true for headless environments (like CI)
		if (Boolean.getBoolean("headless")) {
			System.setProperty("testfx.robot", "glass");
			System.setProperty("testfx.headless", "true");
			System.setProperty("prism.order", "sw");
			System.setProperty("prism.text", "t2k");
			System.setProperty("java.awt.headless", "true");
		}
	}

	@Override
	public void start(Stage stage) {
		SceneNavigator.initialize(stage, springContext);
		LoginView loginView = new LoginView(springContext);
		stage.setScene(new Scene(loginView.createView(), 1100, 750));
		stage.show();
	}

	@Test
	void testLoginFailure() {
		clickOn("#emailField").write("wrong@test.com");
		clickOn("#passwordField").write("wrongpassword");
		clickOn("#loginButton");

		verifyThat("#statusLabel", LabeledMatchers.hasText("Invalid email or password."));
	}

	@Test
	void testRegisterAndLoginSuccess() {
		String testEmail = "newuser@test.com";
		String testPass = "password123";

		clickOn("#emailField").write(testEmail);
		clickOn("#passwordField").write(testPass);
		clickOn("#registerButton");

		// Should now be on Dashboard
		verifyThat("#welcomeLabel", LabeledMatchers.hasText("Logged in as: " + testEmail));

		// Test Logout
		clickOn("#logoutButton");

		// Should be back on Login
		verifyThat("#loginTitle", LabeledMatchers.hasText("Personal Expense Tracker"));
	}
}
