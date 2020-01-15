package goalKeepin.constants;

public enum CashReportStatus {
	CT01("Waiting"), CT02("Completed");
	
	private String statusName;
	
	private CashReportStatus(String statusName) {
		this.statusName = statusName;
	}
	
	public String getStatusName() {
		return statusName;
	}
}
