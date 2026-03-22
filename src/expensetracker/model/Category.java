package expensetracker.model;

public class Category {
	private final int id;
	private final Integer userId;
	private final String name;
	private final boolean isDefault;

	public Category(int id, Integer userId, String name, boolean isDefault) {
		this.id = id;
		this.userId = userId;
		this.name = name;
		this.isDefault = isDefault;
	}

	public int getId() {
		return id;
	}

	public Integer getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public boolean isDefault() {
		return isDefault;
	}

	@Override
	public String toString() {
		return name;
	}
}