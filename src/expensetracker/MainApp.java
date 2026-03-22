package expensetracker;

import expensetracker.ui.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
	@Override
	public void start(Stage primaryStage) {
		DatabaseManager.initializeDatabase();
		SceneNavigator.initialize(primaryStage);

		LoginView loginView = new LoginView();
		Scene scene = new Scene(loginView.createView(), 1100, 750);

		primaryStage.setTitle("Personal Expense Tracker");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}