package goalKeepin.data;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.model.User;

@Mapper
public interface UserMapper {

	int getTotalUserCount(String nationalityCd);

	List<User> selectUserList(Map<String, Object> paramMap);

	List<User> selectUserPageList(Map<String, Object> paramMap);

	int selectUserTotalCount();

}
