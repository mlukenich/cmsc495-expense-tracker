package com.expensetracker.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UserTest {
	@Test
	void userCreation_Successful() {
		User user = new User(1, "test@example.com");
		assertThat(user.getId()).isEqualTo(1);
		assertThat(user.getEmail()).isEqualTo("test@example.com");
		assertThat(user.toString()).isEqualTo("test@example.com");
	}
}
