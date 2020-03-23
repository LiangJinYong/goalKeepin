package goalKeepin.web.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ReportDetailResponseDto {

	private int reportCount;
	private String challengeName;
	private String challengeAuthType;
	private String challengeAuthUrl;
	private Date authRegDate;
	private String reportedUserId;
	private String reportedUserEmail;
	private String reportContent;
	private String reportingUserId;
	private String reportingUserEmail;
	private Date reportRegDate;
	
	List<ReportingUser> reportingUserList = new ArrayList<>();
}
