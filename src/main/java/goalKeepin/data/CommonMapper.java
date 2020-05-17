package goalKeepin.data;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommonMapper {

	String getContentForPushMessage(Map<String, String> param);

	String getLastLanguageForUser(Integer userNo);

	String getChallengeNameByLanguage(Map<String, String> param);
}
