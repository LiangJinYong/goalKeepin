package goalKeepin.model;

import java.util.Date;

import lombok.Data;

@Data
public class Report {

	private Long reportNo;
	private String reporterUserId;
	private String authType;
	private Date reportRegDate;
	private boolean processStatus;
}
