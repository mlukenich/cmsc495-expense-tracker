package com.expensetracker.integration;

import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.service.AuthenticationService;
import com.expensetracker.service.CategoryService;
import com.expensetracker.service.ExpenseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UserWorkflowIntegrationTest {

	@Autowired
	private AuthenticationService authService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ExpenseService expenseService;

	@Test
	void testFullUserWorkflow() {
		// 1. Register a new user
		String email = "integration@test.com";
		String password = "securepassword";
		User user = authService.registerUser(email, password);
		
		assertThat(user).isNotNull();
		assertThat(user.getEmail()).isEqualTo(email);
		int userId = user.getId();

		// 2. Add a custom category
		String categoryName = "Gadgets";
		categoryService.addCustomCategory(userId, categoryName);
		
		List<Category> categories = categoryService.getCategoriesForUser(userId);
		assertThat(categories).anyMatch(c -> c.getName().equals(categoryName));
		
		Category gadgetsCategory = categories.stream()
				.filter(c -> c.getName().equals(categoryName))
				.findFirst()
				.orElseThrow();

		// 3. Add an expense
		expenseService.addExpense(userId, gadgetsCategory.getId(), 999.99, "New Smartphone", "2026-03-27");

		// 4. Verify expense was saved and totals are correct
		List<Expense> expenses = expenseService.getExpensesForUser(userId, null);
		assertThat(expenses).hasSize(1);
		assertThat(expenses.get(0).getAmount()).isEqualTo(999.99);
		assertThat(expenses.get(0).getDescription()).isEqualTo("New Smartphone");

		double total = expenseService.getTotalExpenseAmountForUser(userId);
		assertThat(total).isEqualTo(999.99);

		List<ExpenseService.CategoryTotal> totals = expenseService.getCategoryTotalsForUser(userId);
		assertThat(totals).hasSize(1);
		assertThat(totals.get(0).getCategoryName()).isEqualTo("Gadgets");
		assertThat(totals.get(0).getTotalAmount()).isEqualTo(999.99);

		// 5. Delete expense and verify
		expenseService.deleteExpense(expenses.get(0).getId());
		assertThat(expenseService.getExpensesForUser(userId, null)).isEmpty();
		assertThat(expenseService.getTotalExpenseAmountForUser(userId)).isEqualTo(0.0);
	}
}
