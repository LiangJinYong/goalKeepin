package goalKeepin.data;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.model.Approval;
import goalKeepin.web.dto.ApprovalDetailResponseDto;

@Mapper
public interface ApprovalMapper {

	List<Approval> selectApprovalList(Map<String, Object> paramMap);

	int getTotalApprovalCount(Map<String, Object> paramMap);

	ApprovalDetailResponseDto selectApprovalDetail(Long authNo);

	Long getNextWatingAuthNo(Long authNo);

	void updateAuthAprovalStatus(Map<String, Object> param);

	int getChallengeAuthNum(Long authNo);

	int getChallengeMaxResult(Long authNo);

	void updateChallengeResult(Map<String, Object> param);

}
