package goalKeepin.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import goalKeepin.config.GoalKeepinProps;
import goalKeepin.data.ReportMapper;
import goalKeepin.model.Notice;
import goalKeepin.model.Page;
import goalKeepin.model.Report;
import goalKeepin.service.PageService;
import goalKeepin.web.dto.ReportDetailResponseDto;
import goalKeepin.web.dto.ReportingUser;

@Controller
@RequestMapping("/report")
public class ReportController {

	@Autowired
	private PageService pageService;
	
	@Autowired
	private GoalKeepinProps props;
	
	@Autowired
	private ReportMapper reportMapper;
	
	@GetMapping("/showReportList/{pageNum}")
	public String showReportList(@PathVariable("pageNum") Integer pageNum, Model model, @RequestParam(value="sort", required=false) String sort) {
		int pageSize = props.getPageSize();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("startIndex", (pageNum - 1) * pageSize);
		paramMap.put("pageSize", pageSize);
		
		if (sort != null) {
			String[] sortElements = sort.split(",");
			String sortField = sortElements[0];
			String sortOrder = sortElements[1];
			model.addAttribute("sortField", sortField);
			model.addAttribute("sortOrder", sortOrder);
			paramMap.put("sortField", sortField);
			paramMap.put("sortOrder", sortOrder);
		}
		
		List<Report> pageData = reportMapper.selectReportList(paramMap);
		int totalRecordNum = reportMapper.getTotalReportCount();
		
		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		
		model.addAttribute("page", page);
		
		return "report/reportList";
	}
	
	@GetMapping("/showReportDetail")
	public String showReportDetail(@RequestParam("reportNo") Long reportNo, Model model) {
		
		ReportDetailResponseDto reportDetail = reportMapper.selectReportDetail(reportNo);
		
		List<ReportingUser> reportingUserList = reportMapper.selectOtherRepotingUserList(reportNo);
		reportDetail.setReportingUserList(reportingUserList);
		
		int reportCount = reportMapper.selectReportCount(reportNo);
		reportDetail.setReportCount(reportCount);
		
		model.addAttribute("reportDetail", reportDetail);
		
		return "report/reportDetailForm";
	}
}
