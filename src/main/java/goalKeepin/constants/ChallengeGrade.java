package goalKeepin.constants;

public enum ChallengeGrade {
	LV00("No Limit"), LV02("Bronze"), LV03("Silver"), LV04("Gold"), 
	LV05("Platinum"), LV06("Diamond"), LV07("Master"), LV08("Goalkeeper");

	private String gradeName;

	private ChallengeGrade(String gradeName) {
		this.gradeName = gradeName;
	}

	public String getGradeName() {
		return gradeName;
	}
}
