package com.expensetracker.service;

import com.expensetracker.model.Expense;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

	@Mock
	private JdbcTemplate jdbcTemplate;

	@InjectMocks
	private ExpenseService expenseService;

	@Test
	void addExpense_Successful() {
		when(jdbcTemplate.update(anyString(), anyInt(), anyInt(), anyDouble(), anyString(), anyString())).thenReturn(1);

		expenseService.addExpense(1, 1, 50.0, "Lunch", "2026-03-25");

		verify(jdbcTemplate).update(contains("INSERT INTO expense"), eq(1), eq(1), eq(50.0), eq("Lunch"), eq("2026-03-25"));
	}

	@Test
	void updateExpense_Successful() {
		when(jdbcTemplate.update(anyString(), anyInt(), anyDouble(), anyString(), anyString(), anyInt())).thenReturn(1);

		expenseService.updateExpense(10, 2, 75.0, "Updated Lunch", "2026-03-26");

		verify(jdbcTemplate).update(contains("UPDATE expense"), eq(2), eq(75.0), eq("Updated Lunch"), eq("2026-03-26"), eq(10));
	}

	@Test
	void deleteExpense_Successful() {
		when(jdbcTemplate.update(anyString(), anyInt())).thenReturn(1);

		expenseService.deleteExpense(10);

		verify(jdbcTemplate).update(contains("DELETE FROM expense"), eq(10));
	}

	@Test
	void getExpensesForUser_ReturnsExpenses() {
		List<Expense> mockExpenses = Collections.singletonList(
			new Expense(1, 1, 1, "Food", 50.0, "Lunch", "2026-03-25")
		);

		when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt(), any(), any(), any(), any(), any()))
				.thenReturn(mockExpenses);

		List<Expense> result = expenseService.getExpensesForUser(1, "Food");

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getAmount()).isEqualTo(50.0);
		verify(jdbcTemplate).query(contains("SELECT e.id, e.user_id"), any(RowMapper.class), eq(1), eq("Food"), eq("Food"), anyString(), anyString(), anyString());
	}

	@Test
	void getCategoryTotalsForUser_ReturnsTotals() {
		List<ExpenseService.CategoryTotal> mockTotals = Arrays.asList(
			new ExpenseService.CategoryTotal("Food", 100.0),
			new ExpenseService.CategoryTotal("Transportation", 50.0)
		);

		when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt()))
				.thenReturn(mockTotals);

		List<ExpenseService.CategoryTotal> result = expenseService.getCategoryTotalsForUser(1);

		assertThat(result).hasSize(2);
		assertThat(result.get(0).getCategoryName()).isEqualTo("Food");
		assertThat(result.get(0).getTotalAmount()).isEqualTo(100.0);
	}

	@Test
	void getTotalExpenseAmountForUser_ReturnsSum() {
		when(jdbcTemplate.queryForObject(anyString(), eq(Double.class), anyInt())).thenReturn(150.0);

		double result = expenseService.getTotalExpenseAmountForUser(1);

		assertThat(result).isEqualTo(150.0);
	}
}
