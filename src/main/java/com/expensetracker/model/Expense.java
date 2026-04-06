package com.expensetracker.model;

public class Expense {
	private final int id;
	private final int userId;
	private final int categoryId;
	private final String categoryName;
	private final double amount;
	private final String description;
	private final String transactionDate;

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

	public int getId() {
		return id;
	}

	public int getUserId() {
		return userId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public double getAmount() {
		return amount;
	}

	public String getDescription() {
		return description;
	}

	public String getTransactionDate() {
		return transactionDate;
	}
}