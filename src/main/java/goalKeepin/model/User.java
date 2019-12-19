package goalKeepin.model;

import lombok.Data;
import java.util.List;

@Data
public class User {

	private long userNo;
	private String userId;
	private String loginTypeCd;
	private String nickName;
	private List<OperatedChallenge> challenges;
}
