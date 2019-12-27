package goalKeepin.constants;

public enum CashReportStatus {
	CT01("대기중"), CT02("완료");
	
	private String statusName;
	
	private CashReportStatus(String statusName) {
		this.statusName = statusName;
	}
	
	public String getStatusName() {
		return statusName;
	}
}
