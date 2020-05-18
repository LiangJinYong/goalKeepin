package goalKeepin.web.dto;

import java.util.Date;

import lombok.Data;

@Data
public class UserDetailDto {

	private Long userNo;
	private String userId;
	private String userGrade;
	private String username;
	private String nickname;
	private String phoneNumber;
	private String email;
	private String userCash;
	private String userImageUrl;
	private Integer yellowCardNumber;
	private Integer redCardNumber;
	private String redCardExpiredDate;
}
