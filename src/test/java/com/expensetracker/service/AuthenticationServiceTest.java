package com.expensetracker.service;

import com.expensetracker.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

	@Mock
	private JdbcTemplate jdbcTemplate;

	@InjectMocks
	private AuthenticationService authenticationService;

	private final String testEmail = "test@example.com";
	private final String testPassword = "password";
	// SHA-256 for "password"
	private final String testPasswordHash = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";

	@Test
	void registerUser_Successful() {
		when(jdbcTemplate.update(anyString(), anyString(), anyString())).thenReturn(1);
		
		User mockUser = new User(1, testEmail);
		when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString()))
				.thenReturn(Collections.singletonList(mockUser));

		User result = authenticationService.registerUser(testEmail, testPassword);

		assertThat(result).isNotNull();
		assertThat(result.getEmail()).isEqualTo(testEmail);
		verify(jdbcTemplate).update(contains("INSERT INTO user"), eq(testEmail), eq(testPasswordHash));
	}

	@Test
	void registerUser_Failure_EmailExists() {
		when(jdbcTemplate.update(anyString(), anyString(), anyString())).thenThrow(new RuntimeException("Duplicate email"));

		User result = authenticationService.registerUser(testEmail, testPassword);

		assertThat(result).isNull();
	}

	@Test
	void authenticateUser_Successful() {
		User mockUser = new User(1, testEmail);
		when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString()))
				.thenReturn(Collections.singletonList(mockUser));

		User result = authenticationService.authenticateUser(testEmail, testPassword);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(1);
		assertThat(result.getEmail()).isEqualTo(testEmail);
		verify(jdbcTemplate).query(contains("SELECT id, email, password_hash FROM user"), any(RowMapper.class), eq(testEmail));
	}

	@Test
	void authenticateUser_Failure_WrongPassword() {
		when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString()))
				.thenReturn(Collections.emptyList());

		User result = authenticationService.authenticateUser(testEmail, "wrongpassword");

		assertThat(result).isNull();
	}
}
