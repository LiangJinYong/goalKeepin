package goalKeepin.data;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.model.Report;
import goalKeepin.web.dto.ReportDetailResponseDto;
import goalKeepin.web.dto.ReportingUser;

@Mapper
public interface ReportMapper {

	List<Report> selectReportList(Map<String, Object> paramMap);

	int getTotalReportCount();

	ReportDetailResponseDto selectReportDetail(Long reportNo);

	List<ReportingUser> selectOtherRepotingUserList(Long reportNo);

	int selectReportCount(Long reportNo);

	void processReport(Long reportNo);

}
