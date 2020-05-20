package goalKeepin.constants;

public enum CashReportType {
	CA01("Cash Charge"), CA02("Reward"), CA03("Withdraw"), CA04("Participation Fee"), CA05("Gift Badge"), CA06(
			"Paticipation Cancellation Refund Fee"), CA07("Cash Refund"), CA08("Participation Fee Refund"), CA09("Cash Refund By Red Card");

	private String cashReportTypeName;

	private CashReportType(String cashReportTypeName) {
		this.cashReportTypeName = cashReportTypeName;
	}

	public String getCashReportTypeName() {
		return cashReportTypeName;
	}
}
