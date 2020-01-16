package goalKeepin.data;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.model.CashReport;

@Mapper
public interface CashReportMapper {

	int getTotalCashReportCount(Map<String, Object> paramMap);

	List<CashReport> selectCashReportList(Map<String, Object> paramMap);

	void updateCashReportStatus(List<String> cashReportIdList);

	List<Map<String, String>> selectCashReportForDownload(String nationalityCd);

}
