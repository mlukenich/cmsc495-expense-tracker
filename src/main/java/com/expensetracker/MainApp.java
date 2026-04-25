package com.expensetracker;

import com.expensetracker.repository.DatabaseManager;
import com.expensetracker.ui.util.SceneNavigator;
import com.expensetracker.ui.view.LoginView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * The main application class for the Personal Expense Tracker.
 */
@SpringBootApplication
public class MainApp extends Application {
	private ConfigurableApplicationContext springContext;

	/**
	 * Initializes the JavaFX application by starting the Spring application context. This method is called before the JavaFX application starts and is responsible for setting up the Spring environment.
	 * @throws Exception if an error occurs during initialization
	 */
	@Override
	public void init() throws Exception {
		springContext = SpringApplication.run(MainApp.class);
	}

	/**
	 * Starts the JavaFX application by initializing the database, setting up the initial scene with the LoginView, and displaying the primary stage. This method is called after the JavaFX application has been initialized and is responsible for setting up the initial user interface.
	 * @param primaryStage the primary stage for this application
	 */
	@Override
	public void start(Stage primaryStage) {
		// Initialize the initial database tables if not done via Spring boot script (optional now, but keeping for compatibility)
		springContext.getBean(DatabaseManager.class).initializeDatabase();

		SceneNavigator.initialize(primaryStage, springContext);

		// LoginView will now need the spring context to retrieve services
		LoginView loginView = new LoginView(springContext);
		SceneNavigator.switchScene(loginView.createView(), "Personal Expense Tracker");
	}

	/**
	 * Stops the JavaFX application by closing the Spring application context and exiting the platform. This method is called when the application is about to exit and is responsible for performing any necessary cleanup operations.
	 * @throws Exception if an error occurs during shutdown
	 */
	@Override
	public void stop() throws Exception {
		springContext.close();
		Platform.exit();
	}

	/**
	 * The main entry point for the application. This method launches the JavaFX application.
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(MainApp.class, args);
	}
}