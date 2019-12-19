package goalKeepin.model;

import lombok.Data;
import java.util.Date;

@Data
public class ParticipantEntry {

	private Long entryNo;
	private User participant;
	private OperatedChallenge operatedChallenge;
	private String entryFee;
	private String entryResult;
	private Date entryRegDate;
	private String entryYear;
	private Long entryQuarter;
}
