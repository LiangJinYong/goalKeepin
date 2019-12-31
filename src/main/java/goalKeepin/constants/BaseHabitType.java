package goalKeepin.constants;

public enum BaseHabitType {
	HT01("Health"), HT02("Lifestyle"), HT03("Relationships"), HT04("Ability"), 
	HT05("Hobby"), HT06("Asset"), HT07("Event"), HT08("Premium");

	private String baseHabitTypeName;

	private BaseHabitType(String baseHabitTypeName) {
		this.baseHabitTypeName = baseHabitTypeName;
	}

	public String getBaseHabitTypeName() {
		return baseHabitTypeName;
	}
}
