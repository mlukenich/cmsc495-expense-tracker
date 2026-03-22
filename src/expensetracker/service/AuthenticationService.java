package expensetracker.service;

import expensetracker.DatabaseManager;
import expensetracker.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationService {
	public User registerUser(String email, String password) {
		String insertUserSql = "INSERT INTO user (email, password_hash) VALUES (?, ?)";

		try (Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(insertUserSql)) {

				preparedStatement.setString(1, email.trim().toLowerCase());
				preparedStatement.setString(2, hashPassword(password));
				preparedStatement.executeUpdate();

				return authenticateUser(email, password);
		} catch (SQLException exception) {
			return null;
		}
	}

	public User authenticateUser(String email, String password) {
		String selectUserSql = "SELECT id, email, password_hash FROM user WHERE email = ?";

		try (Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(selectUserSql)) {

				preparedStatement.setString(1, email.trim().toLowerCase());
				ResultSet resultSet = preparedStatement.executeQuery();

				if (resultSet.next()) {
					String storedPasswordHash = resultSet.getString("password_hash");
					String providedPasswordHash = hashPassword(password);

					if (storedPasswordHash.equals(providedPasswordHash)) {
						return new User(resultSet.getInt("id"), resultSet.getString("email"));
					}
				}
		} catch (SQLException exception) {
			exception.printStackTrace();
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