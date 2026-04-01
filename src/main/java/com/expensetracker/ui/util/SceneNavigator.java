package com.expensetracker.ui.util;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Utility class for managing scene navigation in the JavaFX application. It holds a reference to the primary stage and the Spring application context, allowing for easy scene switching and access to Spring-managed beans.
 */
public class SceneNavigator {
	private static Stage primaryStage;
	private static ConfigurableApplicationContext springContext;

	/**
	 * Initializes the SceneNavigator with the primary stage and Spring application context. This method should be called once during application startup to set up the necessary references for scene navigation.
	 * @param stage
	 * @param context
	 */
	public static void initialize(Stage stage, ConfigurableApplicationContext context) {
		primaryStage = stage;
		springContext = context;
	}

	/**
	 * Returns the Spring application context, allowing access to Spring-managed beans from anywhere in the application.
	 * @return the Spring application context
	 */
	public static ConfigurableApplicationContext getSpringContext() {
		return springContext;
	}

	/**
	 * Switches the current scene to the specified root node and updates the window title. This method can be called from any controller or part of the application to change the displayed scene.
	 * @param root the root node of the new scene to display
	 * @param title the title to set for the window when switching to the new scene
	 */
	public static void switchScene(Parent root, String title) {
		primaryStage.setTitle(title);
		primaryStage.setScene(new Scene(root, 1100, 750));
		primaryStage.show();
	}
}