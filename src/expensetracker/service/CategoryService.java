package expensetracker.service;

import expensetracker.DatabaseManager;
import expensetracker.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryService {
    public List<Category> getCategoriesForUser(int userId) {
			List<Category> categories = new ArrayList<>();
			String sql = """
				SELECT id, user_id, name, is_default
				FROM category
				WHERE user_id = ? OR user_id IS NULL
				ORDER BY is_default DESC, name ASC
			""";

			try (Connection connection = DatabaseManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

					preparedStatement.setInt(1, userId);
					ResultSet resultSet = preparedStatement.executeQuery();

					while (resultSet.next()) {
						Integer categoryUserId = resultSet.getObject("user_id") == null ? null	: resultSet.getInt("user_id");

						categories.add(new Category(
							resultSet.getInt("id"),
							categoryUserId,
							resultSet.getString("name"),
							resultSet.getInt("is_default") == 1
						));
					}
			} catch (SQLException exception) {
					exception.printStackTrace();
			}

			return categories;
    }

    public void addCustomCategory(int userId, String categoryName) {
			String sql = "INSERT INTO category (user_id, name, is_default) VALUES (?, ?, 0)";

			try (Connection connection = DatabaseManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

					preparedStatement.setInt(1, userId);
					preparedStatement.setString(2, categoryName.trim());
					preparedStatement.executeUpdate();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
	}
}