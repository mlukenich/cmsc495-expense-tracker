package com.expensetracker.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;

@Repository
public class DatabaseManager {

	private final JdbcTemplate jdbcTemplate;

	public DatabaseManager(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@PostConstruct
	public void initializeDatabase() {
		jdbcTemplate.execute("""
			CREATE TABLE IF NOT EXISTS user (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				email TEXT NOT NULL UNIQUE,
				password_hash TEXT NOT NULL,
				created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
			)
		""");

		jdbcTemplate.execute("""
			CREATE TABLE IF NOT EXISTS category (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				user_id INTEGER,
				name TEXT NOT NULL,
				is_default INTEGER NOT NULL DEFAULT 0,
				FOREIGN KEY (user_id) REFERENCES user(id)
			)
		""");

		jdbcTemplate.execute("""
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

		seedDefaultCategories();
	}

	private void seedDefaultCategories() {
		String[] defaultCategories = {
			"Food",
			"Transportation",
			"Housing",
			"Entertainment",
			"Healthcare",
			"Other"
		};

		for (String categoryName : defaultCategories) {
			jdbcTemplate.update(
				"INSERT INTO category (user_id, name, is_default) " +
				"SELECT NULL, ?, 1 " +
				"WHERE NOT EXISTS (SELECT 1 FROM category WHERE user_id IS NULL AND name = ?)",
				categoryName, categoryName
			);
		}
	}
}