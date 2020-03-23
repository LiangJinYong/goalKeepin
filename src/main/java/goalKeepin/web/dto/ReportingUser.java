package goalKeepin.web.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ReportingUser {

	private String reportContent;
	private String reportingUserId;
	private String reportingUserEmail;
	private Date reportRegDate;
}
