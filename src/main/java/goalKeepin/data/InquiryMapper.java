package goalKeepin.data;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.model.Inquiry;

@Mapper
public interface InquiryMapper {

	int getTotalInquiryCount(Map<String, Object> paramMap);

	List<Inquiry> selectInquiryList(Map<String, Object> paramMap);

	Inquiry selectInquiryDetail(Long inquiryNo);
	
	void updateInquiryStatus(Inquiry inquiry);

	String getPushTokenByUserNo(long userNo);
}
