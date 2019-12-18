package goalKeepin.data;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.model.BaseChallenge;
import goalKeepin.model.ChallengeDetail;

@Mapper
public interface ChallengeMapper {

	List<Map<String, Object>> selectBaseChallengeList(int startIndex);

	int getTotalBaseChallengeNum();

	void insertBaseChallenge(BaseChallenge baseChallenge);

	void insertBaseNmTrans(BaseChallenge baseChallenge);

	void insertBaseAuthDescTrans(BaseChallenge baseChallenge);

	void insertBaseDetailTrans(BaseChallenge baseChallenge);

	BaseChallenge selectBaseChallengeByNo(Long baseNo);

	List<Map<Integer, String>> selectCategoryList();

	void insertChallengeDetailInfo(ChallengeDetail challengeDetail);

	List<ChallengeDetail> selectChallengeListByBaseNo(Map<String, Object> paramMap);

	String selectBaseChallengeNmEn(Long baseNo);

	int getTotalDetailChallengeNumByBase(Long baseNo);

	int selectModifiable(Long baseNo);
	
	
}
