package goalKeepin.data;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.model.Faq;

@Mapper
public interface FaqMapper {

	List<Faq> selectFaqList(Map<String, Object> paramMap);

	int getTotalFaqCount();

	Faq selectFaqDetail(Long faqNo);

	void deleteFaq(Long faqNo);

	void insertOrUpdateFaqQuestion(Faq faq);

	void insertOrUpdateFaqAnswer(Faq faq);

	void insertOrUpdateFaq(Faq faq);

	void insertOrUpdateFaqKeyword(Faq faq);

}
