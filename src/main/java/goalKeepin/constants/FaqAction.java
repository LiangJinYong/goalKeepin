package goalKeepin.constants;

public enum FaqAction {

	WD("Withdraw");
	
	final private String action;
	
	private FaqAction(String action) {
		this.action = action;
	}
	
	public String getAction() {
		return action;
	}
}
