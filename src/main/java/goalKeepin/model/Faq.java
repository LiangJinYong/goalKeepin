package goalKeepin.model;

import java.util.Date;

import lombok.Data;

@Data
public class Faq {

	private Long faqNo;
	
	private Long faqQuestionTransNo;
	private String faqQuestionEn;
	private String faqQuestionTc;
	private String faqQuestionSc;
	
	private Long faqAnswerTransNo;
	private String faqAnswerEn;
	private String faqAnswerTc;
	private String faqAnswerSc;
	
	private Long faqKeywordTransNo;
	private String faqKeywordEn;
	private String faqKeywordTc;
	private String faqKeywordSc;
	
	private String faqActionCode;
	private Date faqRegDate;
}
