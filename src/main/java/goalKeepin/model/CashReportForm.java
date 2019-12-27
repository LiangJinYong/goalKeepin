package goalKeepin.model;

import lombok.Data;

@Data
public class CashReportForm {
	
	private String bank;
	private String reportStatusCd;
	private String reportRegDate;
	private String userId;
}
