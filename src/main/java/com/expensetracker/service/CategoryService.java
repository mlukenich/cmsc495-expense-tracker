package com.expensetracker.service;

import com.expensetracker.model.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Provides services for managing expense categories.
 */
@Service
public class CategoryService {
	private final JdbcTemplate jdbcTemplate;

	/**
	 * Constructs a new CategoryService with the specified JdbcTemplate.
	 * @param jdbcTemplate the JdbcTemplate to use for database operations
	 */
	public CategoryService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * Retrieves a list of categories for the specified user. This includes both default categories (where user_id is null) and custom categories created by the user.
	 * @param userId the ID of the user for whom to retrieve categories
	 * @return a list of Category objects associated with the user
	 */
	public List<Category> getCategoriesForUser(int userId) {
		String sql = """
			SELECT id, user_id, name, is_default
			FROM category
			WHERE user_id = ? OR user_id IS NULL
			ORDER BY is_default DESC, name ASC
		""";

		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			Integer categoryUserId = rs.getObject("user_id") == null ? null : rs.getInt("user_id");
			return new Category(
				rs.getInt("id"),
				categoryUserId,
				rs.getString("name"),
				rs.getInt("is_default") == 1
			);
		}, userId);
	}

	/**
	 * Adds a new custom category for the specified user with the given category name. The new category will have is_default set to false.
	 * @param userId the ID of the user for whom to add the category
	 * @param categoryName the name of the new category to add
	 */
	public void addCustomCategory(int userId, String categoryName) {
		String sql = "INSERT INTO category (user_id, name, is_default) VALUES (?, ?, 0)";
		jdbcTemplate.update(sql, userId, categoryName.trim());
	}
}