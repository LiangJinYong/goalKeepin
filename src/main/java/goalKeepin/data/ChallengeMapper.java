package goalKeepin.data;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.model.BaseChallenge;

@Mapper
public interface ChallengeMapper {

	List<Map<String, Object>> selectBaseChallengeList(int startIndex);

	int getTotalRecordSize();

	void insertBaseChallenge(BaseChallenge baseChallenge);

	void insertBaseNmTrans(BaseChallenge baseChallenge);

	void insertBaseAuthDescTrans(BaseChallenge baseChallenge);

	void insertBaseDetailTrans(BaseChallenge baseChallenge);
}
