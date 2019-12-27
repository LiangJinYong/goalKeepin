package goalKeepin.model;

import lombok.Data;
import java.util.Date;

@Data
public class CashReport {
	private Long cashReportNo;
	private String reportTypeCd;
	private String reportStatusCd;
	private String reportCashAmount;
	private Date reportRegDate;
	private User user;
}
