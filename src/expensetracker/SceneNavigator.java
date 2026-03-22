package expensetracker;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneNavigator {
	private static Stage primaryStage;

	public static void initialize(Stage stage) {
		primaryStage = stage;
	}

	public static void switchScene(Parent root, String title) {
		primaryStage.setTitle(title);
		primaryStage.setScene(new Scene(root, 1100, 750));
		primaryStage.show();
	}
}