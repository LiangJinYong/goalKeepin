package goalKeepin.data;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.model.PushRecord;

@Mapper
public interface CommonMapper {

	String getContentForPushMessage(Map<String, String> param);

	String getLastLanguageForUser(Integer userNo);

	String getChallengeNameByLanguage(Map<String, String> param);
	
	String getPushTokenByUserNo(long userNo);

	boolean selectReceivingChallengePushStatus(Integer userNo);
	
	boolean selectReceivingRelationPushStatus(Integer userNo);
	
	boolean selectReceivingAdPushStatus(Integer userNo);

	void insertSendPushRecord(PushRecord pushRecord);

	List<Integer> selectAllUserNos();
}
