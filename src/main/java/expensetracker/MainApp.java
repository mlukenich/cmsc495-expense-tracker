package expensetracker;

import expensetracker.ui.LoginView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MainApp extends Application {
	private ConfigurableApplicationContext springContext;

	@Override
	public void init() throws Exception {
		springContext = SpringApplication.run(MainApp.class);
	}

	@Override
	public void start(Stage primaryStage) {
		// Initialize the initial database tables if not done via Spring boot script (optional now, but keeping for compatibility)
		springContext.getBean(DatabaseManager.class).initializeDatabase();

		SceneNavigator.initialize(primaryStage, springContext);

		// LoginView will now need the spring context to retrieve services
		LoginView loginView = new LoginView(springContext);
		Scene scene = new Scene(loginView.createView(), 1100, 750);

		primaryStage.setTitle("Personal Expense Tracker");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void stop() throws Exception {
		springContext.close();
		Platform.exit();
	}

	public static void main(String[] args) {
		launch(MainApp.class, args);
	}
}