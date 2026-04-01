package com.expensetracker.model;
/**
 * Represents a category for expenses. Each category has an ID, an optional user ID (null for default categories), a name, and a flag indicating whether it is a default category.
 */
public class Category {
	private final int id;
	private final Integer userId;
	private final String name;
	private final boolean isDefault;

	/**
	 * Constructs a new Category with the specified parameters.
	 * @param id
	 * @param userId
	 * @param name
	 * @param isDefault
	 */
	public Category(int id, Integer userId, String name, boolean isDefault) {
		this.id = id;
		this.userId = userId;
		this.name = name;
		this.isDefault = isDefault;
	}

	/**
	 * Returns the ID of this category.
	 * @return the ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the user ID associated with this category, or null if it is a default category.
	 * @return the user ID or null
	 */		
	public Integer getUserId() {
		return userId;
	}

	/**
	 * Returns the name of this category.
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns whether this category is a default category.
	 * @return true if it is a default category, false otherwise
	 */
	public boolean isDefault() {
		return isDefault;
	}

	/**
	 * Returns a string representation of this category, which is its name.
	 * @return the name of the category
	 */
	@Override
	public String toString() {
		return name;
	}
}