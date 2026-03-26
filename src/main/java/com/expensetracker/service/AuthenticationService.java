package com.expensetracker.service;

import com.expensetracker.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class AuthenticationService {
	private final JdbcTemplate jdbcTemplate;

	public AuthenticationService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public User registerUser(String email, String password) {
		String insertUserSql = "INSERT INTO user (email, password_hash) VALUES (?, ?)";
		try {
			jdbcTemplate.update(insertUserSql, email.trim().toLowerCase(), hashPassword(password));
			return authenticateUser(email, password);
		} catch (Exception e) {
			return null;
		}
	}

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