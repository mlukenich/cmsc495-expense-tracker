package com.expensetracker.service;

import com.expensetracker.model.Expense;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Provides services for managing expenses, including adding, updating, deleting, and retrieving expenses for users.
 */
@Service
public class ExpenseService {
	private final JdbcTemplate jdbcTemplate;

	/**
	 * Constructs a new ExpenseService with the specified JdbcTemplate.
	 * @param jdbcTemplate the JdbcTemplate to use for database operations
	 */
	public ExpenseService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * Adds a new expense for the specified user with the given details.
	 * @param userId the ID of the user for whom to add the expense
	 * @param categoryId the ID of the category for the expense
	 * @param amount the amount of the expense
	 * @param description a description of the expense
	 * @param transactionDate the date of the transaction in YYYY-MM-DD format
	 */
	public void addExpense(int userId, int categoryId, double amount, String description, String transactionDate) {
		String sql = "INSERT INTO expense (user_id, category_id, amount, description, transaction_date) VALUES (?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql, userId, categoryId, amount, description, transactionDate);
	}

	/**
	 * Updates an existing expense with the specified details.
	 * @param expenseId the ID of the expense to update
	 * @param categoryId the new category ID for the expense
	 * @param amount the new amount for the expense
	 * @param description the new description for the expense
	 * @param transactionDate the new transaction date in YYYY-MM-DD format
	 */
	public void updateExpense(int expenseId, int categoryId, double amount, String description, String transactionDate) {
		String sql = "UPDATE expense SET category_id = ?, amount = ?, description = ?, transaction_date = ? WHERE id = ?";
		jdbcTemplate.update(sql, categoryId, amount, description, transactionDate, expenseId);
	}

	/**
	 * Deletes the expense with the specified ID.
	 * @param expenseId the ID of the expense to delete
	 */
	public void deleteExpense(int expenseId) {
		String sql = "DELETE FROM expense WHERE id = ?";
		jdbcTemplate.update(sql, expenseId);
	}

	/**
	 * Retrieves a list of expenses for the specified user, optionally filtered by a search text that matches the category name, description, or transaction date.
	 * @param userId the ID of the user for whom to retrieve expenses
	 * @param searchText an optional search text to filter expenses by category name, description, or transaction date
	 * @return a list of Expense objects matching the criteria
	 */
	public List<Expense> getExpensesForUser(int userId, String searchText) {
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

		return jdbcTemplate.query(sql, (rs, rowNum) -> new Expense(
			rs.getInt("id"),
			rs.getInt("user_id"),
			rs.getInt("category_id"),
			rs.getString("category_name"),
			rs.getDouble("amount"),
			rs.getString("description"),
			rs.getString("transaction_date")
		), userId, searchText, searchText == null ? "" : searchText, searchPattern, searchPattern, searchPattern);
	}

	/**
	 * Retrieves a list of category totals for the specified user, which includes the total amount spent in each category.
	 * @param userId the ID of the user for whom to retrieve category totals
	 * @return a list of CategoryTotal objects representing the total amount spent in each category
	 */
	public List<CategoryTotal> getCategoryTotalsForUser(int userId) {
		String sql = """
			SELECT c.name AS category_name, SUM(e.amount) AS total_amount
			FROM expense e
			JOIN category c ON e.category_id = c.id
			WHERE e.user_id = ?
			GROUP BY c.name
			ORDER BY total_amount DESC
		""";

		return jdbcTemplate.query(sql, (rs, rowNum) -> new CategoryTotal(
			rs.getString("category_name"),
			rs.getDouble("total_amount")
		), userId);
	}

	/**
	 * Retrieves the total amount of expenses for the specified user.
	 * @param userId the ID of the user for whom to retrieve the total expense amount
	 * @return the total amount of expenses for the user
	 */
	public double getTotalExpenseAmountForUser(int userId) {
		String sql = "SELECT COALESCE(SUM(amount), 0) AS total_amount FROM expense WHERE user_id = ?";
		Double total = jdbcTemplate.queryForObject(sql, Double.class, userId);
		return total != null ? total : 0.0;
	}

	/**
	 * Represents the total amount spent in a specific category.
	 */
	public static class CategoryTotal {
		private final String categoryName;
		private final double totalAmount;

		/**
		 * Constructs a new CategoryTotal with the specified category name and total amount.
		 * @param categoryName the name of the category
		 * @param totalAmount the total amount spent in the category
		 */
		public CategoryTotal(String categoryName, double totalAmount) {
			this.categoryName = categoryName;
			this.totalAmount = totalAmount;
		}

		/**
		 * Returns the name of the category.
		 * @return categoryName the name of the category
		 */
		public String getCategoryName() {
			return categoryName;
		}

		/**
		 * Returns the total amount spent in the category.
		 * @return totalAmount the total amount spent in the category
		 */
		public double getTotalAmount() {
			return totalAmount;
		}
	}
}