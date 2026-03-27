package com.expensetracker.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {
	@Test
	void categoryCreation_Successful() {
		Category category = new Category(1, 10, "Food", true);
		assertThat(category.getId()).isEqualTo(1);
		assertThat(category.getUserId()).isEqualTo(10);
		assertThat(category.getName()).isEqualTo("Food");
		assertThat(category.isDefault()).isTrue();
		assertThat(category.toString()).isEqualTo("Food");
	}

	@Test
	void defaultCategoryCreation_Successful() {
		Category category = new Category(1, null, "Other", true);
		assertThat(category.getUserId()).isNull();
		assertThat(category.isDefault()).isTrue();
	}
}
