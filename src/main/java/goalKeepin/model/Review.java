package goalKeepin.model;

import lombok.Data;
import java.util.Date;

@Data
public class Review {

	private Long reviewNo;
	private String reviewContent;
	private Date reviewRegDate;
	private User user;
	private OperatedChallenge operatedChallenge;
}
