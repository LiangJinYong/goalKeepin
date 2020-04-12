package goalKeepin.model;

import lombok.Data;
import java.util.Date;

@Data
public class CashReport {
	private Long cashReportNo;
	private String reportTypeCd;
	private String reportStatusCd;
	private String reportCashAmount;
	private String reportBank;
	private String reportAccountHolderFirstName;
	private String reportAccountHolderLastName;
	private String reportAccount;
	private Date reportRegDate;
	private User user;
}
