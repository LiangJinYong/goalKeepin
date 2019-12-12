package goalKeepin.model;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class Translation {

	private Long transNo;
	
	@NotEmpty(message="Required")
	private String transEn;
	
	@NotEmpty(message="Required")
	private String transTc;
	
	@NotEmpty(message="Required")
	private String transSc;
}
