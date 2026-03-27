package com.expensetracker.service;

import com.expensetracker.model.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

	@Mock
	private JdbcTemplate jdbcTemplate;

	@InjectMocks
	private CategoryService categoryService;

	@Test
	void getCategoriesForUser_ReturnsCategories() {
		List<Category> mockCategories = Arrays.asList(
			new Category(1, null, "Food", true),
			new Category(2, 1, "Custom", false)
		);

		when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt()))
				.thenReturn(mockCategories);

		List<Category> result = categoryService.getCategoriesForUser(1);

		assertThat(result).hasSize(2);
		assertThat(result.get(0).getName()).isEqualTo("Food");
		assertThat(result.get(1).getName()).isEqualTo("Custom");
		verify(jdbcTemplate).query(contains("SELECT id, user_id, name, is_default"), any(RowMapper.class), eq(1));
	}

	@Test
	void addCustomCategory_Successful() {
		when(jdbcTemplate.update(anyString(), anyInt(), anyString())).thenReturn(1);

		categoryService.addCustomCategory(1, "New Category");

		verify(jdbcTemplate).update(contains("INSERT INTO category"), eq(1), eq("New Category"));
	}
}
