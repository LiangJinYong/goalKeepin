package goalKeepin.data;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.model.Approval;

@Mapper
public interface ApprovalMapper {

	List<Approval> selectApprovalList(Map<String, Object> paramMap);

	int getTotalApprovalCount(Map<String, Object> paramMap);

}
