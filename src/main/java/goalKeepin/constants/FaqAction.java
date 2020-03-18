package goalKeepin.constants;

public enum FaqAction {

	WD("Withdraw"), LG("Login");
	
	final private String action;
	
	private FaqAction(String action) {
		this.action = action;
	}
	
	public String getAction() {
		return action;
	}
}
