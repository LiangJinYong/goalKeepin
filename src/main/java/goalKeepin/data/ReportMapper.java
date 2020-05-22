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

	int getTotalReportCount(Map<String, Object> paramMap);

	ReportDetailResponseDto selectReportDetail(Long reportNo);

	List<ReportingUser> selectOtherRepotingUserList(Long reportNo);

	int selectReportCount(Long reportNo);

	void processReport(Long reportNo);

	void updateYellowCardForReport(Long reportNo);

	void updateRedCardForReport(Long reportNo);

	long selectAuthNoByReportNo(Long reportNo);

	long selectUserNoByReportNo(Long reportNo);

	String selectChallengeStatus(Long reportNo);

}
