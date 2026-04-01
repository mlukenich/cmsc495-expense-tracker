package com.expensetracker.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ExpenseTest {
	@Test
	void expenseCreation_Successful() {
		Expense expense = new Expense(
			1, 10, 5, "Food", 25.50, "Lunch", "2026-03-25"
		);

		assertThat(expense.getId()).isEqualTo(1);
		assertThat(expense.getUserId()).isEqualTo(10);
		assertThat(expense.getCategoryId()).isEqualTo(5);
		assertThat(expense.getCategoryName()).isEqualTo("Food");
		assertThat(expense.getAmount()).isEqualTo(25.50);
		assertThat(expense.getDescription()).isEqualTo("Lunch");
		assertThat(expense.getTransactionDate()).isEqualTo("2026-03-25");
	}
}
