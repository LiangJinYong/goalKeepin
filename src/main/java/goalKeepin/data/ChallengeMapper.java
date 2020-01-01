package goalKeepin.data;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.model.BaseChallenge;
import goalKeepin.model.OperatedChallenge;
import goalKeepin.model.ParticipantEntry;
import goalKeepin.model.Review;

@Mapper
public interface ChallengeMapper {

	List<Map<String, Object>> selectBaseChallengeList(int startIndex);

	int getTotalBaseChallengeNum();

	void insertOrUpdateBaseChallenge(BaseChallenge baseChallenge);

	void insertOrUpdateBaseNmTrans(BaseChallenge baseChallenge);

	void insertOrUpdateBaseAuthDescTrans(BaseChallenge baseChallenge);

	void insertOrUpdateBaseDetailTrans(BaseChallenge baseChallenge);

	BaseChallenge selectBaseChallengeByNo(Long baseNo);

	List<Map<Integer, String>> selectCategoryList();

	void insertOperatedChallengeInfo(OperatedChallenge challengeDetail);

	List<OperatedChallenge> selectOperatedChallengeListByBaseNo(Map<String, Object> paramMap);

	String selectBaseChallengeNmEn(Long baseNo);

	int getOperatedChallengeCountByBase(Long baseNo);

	int selectModifiable(Long baseNo);

	OperatedChallenge selectOperatedChallengeByNo(Long operatedNo);
	
	int getChallengeProofCount(Long operatedNo);

	int getPaticipantCountByChallenge(Long operatedNo);

	List<ParticipantEntry> selectParticipantEntryList(int startIndex);

	List<ParticipantEntry> selectParticipantEntryList(Map<String, Object> paramMap);

	ParticipantEntry selectEntryInfoByParticipant(Long entryNo);

	OperatedChallenge selectOperatedChallengeInfo(Long operatedNo);

	int getReviewCountByChallenge(Long operatedNo);

	List<Review> selectReviewListByChallenge(Map<String, Object> paramMap);

	int getAllOperatedChallengeCount(Map<String, Object> paramMap);

	List<OperatedChallenge> selectAllOperatedChallengeList(Map<String, Object> paramMap);

}
