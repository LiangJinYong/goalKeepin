package goalKeepin.web.dto;

import java.util.Date;

import lombok.Data;

@Data
public class PushHistoryResponseDto {

	private Long pushNo;
	private String pushReceiveUserId;
	private String pushTitle;
	private String pushMsg;
	private String pushTypeName;
	private Date pushRegDate;
}
