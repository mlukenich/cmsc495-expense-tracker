package expensetracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
	private static final String DATABASE_URL = "jdbc:sqlite:expense_tracker.db";

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(DATABASE_URL);
	}

	public static void initializeDatabase() {
		try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
			statement.execute("""
				CREATE TABLE IF NOT EXISTS user (
					id INTEGER PRIMARY KEY AUTOINCREMENT,
					email TEXT NOT NULL UNIQUE,
					password_hash TEXT NOT NULL,
					created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
				)
			""");

			statement.execute("""
				CREATE TABLE IF NOT EXISTS category (
					id INTEGER PRIMARY KEY AUTOINCREMENT,
					user_id INTEGER,
					name TEXT NOT NULL,
					is_default INTEGER NOT NULL DEFAULT 0,
					FOREIGN KEY (user_id) REFERENCES user(id)
				)
			""");

			statement.execute("""
				CREATE TABLE IF NOT EXISTS expense (
					id INTEGER PRIMARY KEY AUTOINCREMENT,
					user_id INTEGER NOT NULL,
					category_id INTEGER NOT NULL,
					amount REAL NOT NULL,
					description TEXT,
					transaction_date TEXT NOT NULL,
					created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
					FOREIGN KEY (user_id) REFERENCES user(id),
					FOREIGN KEY (category_id) REFERENCES category(id)
				)
			""");

			seedDefaultCategories(statement);
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
	}

	private static void seedDefaultCategories(Statement statement) throws SQLException {
		String[] defaultCategories = {
			"Food",
			"Transportation",
			"Housing",
			"Entertainment",
			"Healthcare",
			"Other"
		};

		for (String categoryName : defaultCategories) {
			statement.executeUpdate(
				"INSERT INTO category (user_id, name, is_default) " +
				"SELECT NULL, '" + categoryName + "', 1 " +
				"WHERE NOT EXISTS (SELECT 1 FROM category WHERE user_id IS NULL AND name = '" + categoryName + "')"
			);
		}
	}
}