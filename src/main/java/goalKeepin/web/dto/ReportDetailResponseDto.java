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
	private Long reportNo;
	private String reportedUserId;
	private String reportedUserEmail;
	private String reportContent;
	private String reportingUserId;
	private String reportingUserEmail;
	private boolean processStatus;
	private Date reportRegDate;
	private boolean receivedYellowCard;
	private boolean receivedRedCard;
	
	List<ReportingUser> reportingUserList = new ArrayList<>();
}
