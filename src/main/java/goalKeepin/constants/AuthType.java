package goalKeepin.constants;

public enum AuthType {

	AU01("Photo"), AU02("Voice");
	
	private String authTypeName;
	
	private AuthType(String authTypeName) {
		this.authTypeName = authTypeName;
	}
	
	public String getAuthTypeName() {
		return authTypeName;
	}
}
