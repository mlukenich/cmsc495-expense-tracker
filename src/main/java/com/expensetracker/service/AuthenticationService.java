package com.expensetracker.service;

import com.expensetracker.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Provides authentication services for user registration and login.
 */
@Service
public class AuthenticationService {
	private final JdbcTemplate jdbcTemplate;

	/**
	 * Constructs a new AuthenticationService with the specified JdbcTemplate.
	 * @param jdbcTemplate the JdbcTemplate to use for database operations
	 */
	public AuthenticationService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * Registers a new user with the given email and password. Returns the created User object if registration is successful, or null if the email is already in use.
	 * @param email the email address of the new user
	 * @param password the password for the new user
	 * @return the created User object or null if registration fails
	 */
	public User registerUser(String email, String password) {
		if (email == null || email.trim().isEmpty()) {
			throw new IllegalArgumentException("Email cannot be blank.");
		}
		if (password == null || password.trim().isEmpty()) {
			throw new IllegalArgumentException("Password cannot be blank.");
		}

		String insertUserSql = "INSERT INTO user (email, password_hash) VALUES (?, ?)";
		try {
			jdbcTemplate.update(insertUserSql, email.trim().toLowerCase(), hashPassword(password));
			return authenticateUser(email, password);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Authenticates a user with the given email and password. Returns the User object if authentication is successful, or null if authentication fails.
	 * @param email the email address of the user
	 * @param password the password of the user
	 * @return the authenticated User object or null if authentication fails
	 */
	public User authenticateUser(String email, String password) {
		String selectUserSql = "SELECT id, email, password_hash FROM user WHERE email = ?";
		
		List<User> users = jdbcTemplate.query(selectUserSql, (rs, rowNum) -> {
			String storedPasswordHash = rs.getString("password_hash");
			String providedPasswordHash = hashPassword(password);
			if (storedPasswordHash.equals(providedPasswordHash)) {
				return new User(rs.getInt("id"), rs.getString("email"));
			}
			return null;
		}, email.trim().toLowerCase());

		for (User user : users) {
			if (user != null) return user;
		}
		return null;
	}

	/*
	 * Hashes the given password using SHA-256 and returns the hexadecimal representation of the hash.
	 * @param password the password to hash
	 * @return the hexadecimal hash of the password
	 */
	private String hashPassword(String password) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] digestBytes = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
			StringBuilder hexadecimalHashBuilder = new StringBuilder();

			for (byte currentByte : digestBytes) {
				hexadecimalHashBuilder.append(String.format("%02x", currentByte));
			}

			return hexadecimalHashBuilder.toString();
		} catch (NoSuchAlgorithmException exception) {
			throw new RuntimeException("SHA-256 not available", exception);
		}
	}
}