package com.expensetracker.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseManagerTest {

	@Mock
	private JdbcTemplate jdbcTemplate;

	@InjectMocks
	private DatabaseManager databaseManager;

	@Test
	void initializeDatabase_ExecutesCreateTables() {
		databaseManager.initializeDatabase();

		// Verify create table calls
		verify(jdbcTemplate, atLeastOnce()).execute(contains("CREATE TABLE IF NOT EXISTS user"));
		verify(jdbcTemplate, atLeastOnce()).execute(contains("CREATE TABLE IF NOT EXISTS category"));
		verify(jdbcTemplate, atLeastOnce()).execute(contains("CREATE TABLE IF NOT EXISTS expense"));

		// Verify seeding (at least for one category)
		verify(jdbcTemplate, atLeastOnce()).update(contains("INSERT INTO category"), anyString(), anyString());
	}
}
