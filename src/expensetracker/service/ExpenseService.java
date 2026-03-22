package expensetracker.service;

import expensetracker.DatabaseManager;
import expensetracker.model.Expense;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExpenseService {
	public void addExpense(int userId, int categoryId, double amount, String description, String transactionDate) {
		String sql = "INSERT INTO expense (user_id, category_id, amount, description, transaction_date) VALUES (?, ?, ?, ?, ?)";

		try (Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

				preparedStatement.setInt(1, userId);
				preparedStatement.setInt(2, categoryId);
				preparedStatement.setDouble(3, amount);
				preparedStatement.setString(4, description);
				preparedStatement.setString(5, transactionDate);
				preparedStatement.executeUpdate();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
	}

	public void updateExpense(int expenseId, int categoryId, double amount, String description, String transactionDate) {
		String sql = "UPDATE expense SET category_id = ?, amount = ?, description = ?, transaction_date = ? WHERE id = ?";

		try (Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

				preparedStatement.setInt(1, categoryId);
				preparedStatement.setDouble(2, amount);
				preparedStatement.setString(3, description);
				preparedStatement.setString(4, transactionDate);
				preparedStatement.setInt(5, expenseId);
				preparedStatement.executeUpdate();
		} catch (SQLException exception) {
				exception.printStackTrace();
		}
	}

	public void deleteExpense(int expenseId) {
		String sql = "DELETE FROM expense WHERE id = ?";

		try (Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

				preparedStatement.setInt(1, expenseId);
				preparedStatement.executeUpdate();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
	}

	public List<Expense> getExpensesForUser(int userId, String searchText) {
		List<Expense> expenses = new ArrayList<>();
		String sql = """
			SELECT e.id, e.user_id, e.category_id, c.name AS category_name, e.amount, e.description, e.transaction_date
			FROM expense e
			JOIN category c ON e.category_id = c.id
			WHERE e.user_id = ?
				AND (
					? IS NULL OR ? = '' OR
					LOWER(c.name) LIKE LOWER(?) OR
					LOWER(COALESCE(e.description, '')) LIKE LOWER(?) OR
					e.transaction_date LIKE ?
				)
			ORDER BY e.transaction_date DESC, e.id DESC
		""";

		String searchPattern = "%" + (searchText == null ? "" : searchText.trim()) + "%";

		try (Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

				preparedStatement.setInt(1, userId);
				preparedStatement.setString(2, searchText);
				preparedStatement.setString(3, searchText == null ? "" : searchText);
				preparedStatement.setString(4, searchPattern);
				preparedStatement.setString(5, searchPattern);
				preparedStatement.setString(6, searchPattern);

				ResultSet resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					expenses.add(new Expense(
						resultSet.getInt("id"),
						resultSet.getInt("user_id"),
						resultSet.getInt("category_id"),
						resultSet.getString("category_name"),
						resultSet.getDouble("amount"),
						resultSet.getString("description"),
						resultSet.getString("transaction_date")
					));
				}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}

		return expenses;
	}

	public List<CategoryTotal> getCategoryTotalsForUser(int userId) {
		List<CategoryTotal> categoryTotals = new ArrayList<>();
		String sql = """
			SELECT c.name AS category_name, SUM(e.amount) AS total_amount
			FROM expense e
			JOIN category c ON e.category_id = c.id
			WHERE e.user_id = ?
			GROUP BY c.name
			ORDER BY total_amount DESC
		""";

		try (Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

				preparedStatement.setInt(1, userId);
				ResultSet resultSet = preparedStatement.executeQuery();

				while (resultSet.next()) {
					categoryTotals.add(new CategoryTotal(
						resultSet.getString("category_name"),
						resultSet.getDouble("total_amount")
					));
				}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}

		return categoryTotals;
	}

	public double getTotalExpenseAmountForUser(int userId) {
		String sql = "SELECT COALESCE(SUM(amount), 0) AS total_amount FROM expense WHERE user_id = ?";

		try (Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

				preparedStatement.setInt(1, userId);
				ResultSet resultSet = preparedStatement.executeQuery();

				if (resultSet.next()) {
					return resultSet.getDouble("total_amount");
				}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}

		return 0.0;
	}

	public static class CategoryTotal {
		private final String categoryName;
		private final double totalAmount;

		public CategoryTotal(String categoryName, double totalAmount) {
			this.categoryName = categoryName;
			this.totalAmount = totalAmount;
		}

		public String getCategoryName() {
			return categoryName;
		}

		public double getTotalAmount() {
			return totalAmount;
		}
	}
}