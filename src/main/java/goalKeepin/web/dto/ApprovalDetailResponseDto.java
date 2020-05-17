package goalKeepin.web.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ApprovalDetailResponseDto {

	private Long authNo;
	private String challengeAuthType;
	private String challengeAuthUrl;
	private Date authRegDate;
	private String challengeName;
	private String startDate;
	private String endDate;
	private String authUserId;
	private String authUserEmail;
	private String approvalStatusCd;
	private String challengeStatusCd;
}
