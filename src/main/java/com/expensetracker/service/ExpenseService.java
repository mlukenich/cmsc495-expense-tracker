package com.expensetracker.service;

import com.expensetracker.model.Expense;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {
	private final JdbcTemplate jdbcTemplate;

	public ExpenseService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void addExpense(int userId, int categoryId, double amount, String description, String transactionDate) {
		String sql = "INSERT INTO expense (user_id, category_id, amount, description, transaction_date) VALUES (?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql, userId, categoryId, amount, description, transactionDate);
	}

	public void updateExpense(int expenseId, int categoryId, double amount, String description, String transactionDate) {
		String sql = "UPDATE expense SET category_id = ?, amount = ?, description = ?, transaction_date = ? WHERE id = ?";
		jdbcTemplate.update(sql, categoryId, amount, description, transactionDate, expenseId);
	}

	public void deleteExpense(int expenseId) {
		String sql = "DELETE FROM expense WHERE id = ?";
		jdbcTemplate.update(sql, expenseId);
	}

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

	public double getTotalExpenseAmountForUser(int userId) {
		String sql = "SELECT COALESCE(SUM(amount), 0) AS total_amount FROM expense WHERE user_id = ?";
		Double total = jdbcTemplate.queryForObject(sql, Double.class, userId);
		return total != null ? total : 0.0;
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