package com.expensetracker.ui.util;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;

public class SceneNavigator {
	private static Stage primaryStage;
	private static ConfigurableApplicationContext springContext;

	public static void initialize(Stage stage, ConfigurableApplicationContext context) {
		primaryStage = stage;
		springContext = context;
	}

	public static ConfigurableApplicationContext getSpringContext() {
		return springContext;
	}

	public static void switchScene(Parent root, String title) {
		primaryStage.setTitle(title);
		primaryStage.setScene(new Scene(root, 1100, 750));
		primaryStage.show();
	}
}