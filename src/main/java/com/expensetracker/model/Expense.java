package com.expensetracker.model;

/**
 * Represents an expense in the expense tracker.
 */
public class Expense {
	private final int id;
	private final int userId;
	private final int categoryId;
	private final String categoryName;
	private final double amount;
	private final String description;
	private final String transactionDate;

	/**
	 * Constructs a new Expense with the specified parameters.
	 * @param id the ID of the expense
	 * @param userId the ID of the user who made the expense
	 * @param categoryId the ID of the category of the expense
	 * @param categoryName the name of the category of the expense
	 * @param amount the amount of the expense
	 * @param description a description of the expense
	 * @param transactionDate the date of the transaction in ISO format (YYYY-MM-DD)
	 */
	public Expense(
		int id,
		int userId,
		int categoryId,
		String categoryName,
		double amount,
		String description,
		String transactionDate
	) {
		this.id = id;
		this.userId = userId;
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.amount = amount;
		this.description = description;
		this.transactionDate = transactionDate;
	}

	/**
	 * Returns the ID of this expense.
	 * @return the ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the ID of the user who made this expense.
	 * @return the user ID
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * Returns the ID of the category of this expense.
	 * @return the category ID
	 */
	public int getCategoryId() {
		return categoryId;
	}

	/**
	 * Returns the name of the category of this expense.
	 * @return the category name
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * Returns the amount of this expense.
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * Returns the description of this expense.
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the date of the transaction in ISO format (YYYY-MM-DD).
	 * @return the transaction date
	 */
	public String getTransactionDate() {
		return transactionDate;
	}
}