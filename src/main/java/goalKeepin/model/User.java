package goalKeepin.model;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class User {

	private Long userNo;
	private String userId;
	private String loginTypeCd;
	private String nickName;
	private String nationalityCd;
	private String userAccount;
	private Date userRegDate;
	private List<OperatedChallenge> challenges;
}
