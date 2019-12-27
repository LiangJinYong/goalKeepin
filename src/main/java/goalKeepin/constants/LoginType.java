package goalKeepin.constants;

public enum LoginType {
	LT01("Email"), LT02("Google"), LT03("Facebook"), LT04("Wechat"), LT05("Whatsapp");
	
	private String loginTypeName;
	
	private LoginType(String loginTypeName) {
		this.loginTypeName = loginTypeName;
	}
	
	public String getLoginTypeName() {
		return loginTypeName;
	}
}
