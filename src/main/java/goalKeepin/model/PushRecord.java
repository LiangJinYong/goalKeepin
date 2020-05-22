package goalKeepin.model;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PushRecord {

	private Long pushNo;
	private Integer pushReceiveUserNo;
	private String pushTitle;
	private String pushMsg;
	private String pushType;
	private Integer pushTarget;
	private Date pushRegDate;
	
	public PushRecord(Integer pushReceiveUserNo,String pushTitle, String pushMsg, String pushType, Integer pushTarget) {
		this.pushReceiveUserNo = pushReceiveUserNo;
		this.pushTitle = pushTitle;
		this.pushMsg = pushMsg;
		this.pushType = pushType;
		this.pushTarget = pushTarget;
	}
}
