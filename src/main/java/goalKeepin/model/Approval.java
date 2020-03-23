package goalKeepin.model;

import java.util.Date;

import lombok.Data;

@Data
public class Approval {

	private Long authNo;
	private String authTypeCd;
	private String baseNmEn;
	private Date authRegDate;
}
