package com.expensetracker.model;

/**
 * Represents a user in the expense tracker. Each user has an ID and an email address.
 */
public class User {
	private final int id;
	private final String email;

	/**
	 * Constructs a new User with the specified ID and email address.
	 * @param id the ID of the user
	 * @param email the email address of the user
	 */
	public User(int id, String email) {
		this.id = id;
		this.email = email;
	}

	/**
	 * Returns the ID of this user.
	 * @return the ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the email address of this user.
	 * @return the email address
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Returns a string representation of this user, which is its email address.
	 * @return the email address of the user
	 */
	@Override
	public String toString() {
		return email;
	}
}