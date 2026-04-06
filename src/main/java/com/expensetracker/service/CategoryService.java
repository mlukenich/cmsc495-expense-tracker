package com.expensetracker.service;

import com.expensetracker.model.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
	private final JdbcTemplate jdbcTemplate;

	public CategoryService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

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

	public void addCustomCategory(int userId, String categoryName) {
		String sql = "INSERT INTO category (user_id, name, is_default) VALUES (?, ?, 0)";
		jdbcTemplate.update(sql, userId, categoryName.trim());
	}
}