package goalKeepin.model;

import lombok.Data;

@Data
public class Category {

	private Long categoryNo;
	
	private Long categoryNmTransNo;
	private String categoryNmEn;
	private String categoryNmTc;
	private String categoryNmSc;
	
	private Long categoryDescriptionTransNo;
	private String categoryDescriptionEn;
	private String categoryDescriptionTc;
	private String categoryDescriptionSc;
	
	private int challengeNum;
}
