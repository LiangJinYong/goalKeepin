package goalKeepin.constants;

public enum Country {
	NA01("Hong Kong"), NA02("Singapore");
	
	private String countryName;
	
	private Country(String countryName) {
		this.countryName = countryName;
	}
	
	public String getCountryName() {
		return countryName;
	}
}
