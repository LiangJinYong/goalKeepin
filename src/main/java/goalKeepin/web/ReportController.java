package goalKeepin.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import goalKeepin.config.GoalKeepinProps;
import goalKeepin.data.ApprovalMapper;
import goalKeepin.data.ReportMapper;
import goalKeepin.data.UserMapper;
import goalKeepin.model.Page;
import goalKeepin.model.Report;
import goalKeepin.service.PageService;
import goalKeepin.util.SortUtils;
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
	
	@Autowired
	private ApprovalMapper approvalMapper;
	
	@Autowired
	private UserMapper userMapper;

	@GetMapping("/showReportList/{pageNum}")
	public String showReportList(@RequestParam(name = "reportStatus", required = false) String reportStatus,
			@PathVariable("pageNum") Integer pageNum, Model model,
			@RequestParam(value = "sort", required = false) String sort) {
		
		int pageSize = props.getPageSize();
		Map<String, Object> paramMap = SortUtils.getParamMap(pageNum, pageSize, model, sort);
		
		if (reportStatus == null) {
			reportStatus = "";
		}
		paramMap.put("reportStatus", reportStatus);

		List<Report> pageData = reportMapper.selectReportList(paramMap);
		int totalRecordNum = reportMapper.getTotalReportCount(paramMap);

		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);

		model.addAttribute("page", page);
		model.addAttribute("reportStatus", reportStatus);

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

	@PostMapping("/processReport")
	@ResponseBody
	public String processReport(@RequestParam("reportNo") Long reportNo, @RequestParam("processAction") String processAction) {
		
		Map<String, Object> param = new HashMap<>();
		long userNo = reportMapper.selectUserNoByReportNo(reportNo);
		
		if ("giveYellowCard".equals(processAction)) {
			reportMapper.updateYellowCardForReport(reportNo);
			
			int yellowCardNumber = userMapper.selectYellowCardNumber(userNo);
			
			if (yellowCardNumber == 0) {
				userMapper.increaseYellowCardNumber(userNo);
			} else if (yellowCardNumber == 1) {
				userMapper.increaseRedCardNumber(userNo);
				
				int redCardNumber = userMapper.selectRedCardNumber(userNo);
				
				param.put("userNo", userNo);
				if (redCardNumber == 1) {
					param.put("expiredMonthNumber", 1);
					userMapper.updateRedCardExpiredDate(param);
				} else if (redCardNumber == 2) {
					param.put("expiredMonthNumber", 3);
					userMapper.updateRedCardExpiredDate(param);
					userMapper.clearToken(userNo);
					
					Double totalReward = userMapper.selectTotalReward(userNo);
					Double currentUserCash = userMapper.selectCurrentUserCash(userNo);
					
					Double reportCashAmount = totalReward;
					
					if (totalReward > currentUserCash) {
						reportCashAmount = currentUserCash;
					}
					
					param.put("reportCashAmount", reportCashAmount);
					userMapper.insertUserReportRecord(param);
					userMapper.subtractReward(userNo);
				}
			}
		} else if ("giveRedCard".equals(processAction)) {
			reportMapper.updateRedCardForReport(reportNo);
			
			int redCardNumber = userMapper.selectRedCardNumber(userNo);
			
			String challengeStatus = reportMapper.selectChallengeStatus(reportNo);
			
			// 진행중인 챈린지일 경우 달성률 마이너서
			if ("CH02".equals(challengeStatus)) {
				
				long authNo = reportMapper.selectAuthNoByReportNo(reportNo);
				
				int authNums = approvalMapper.getChallengeAuthNum(authNo);
				int maxResult = approvalMapper.getChallengeMaxResult(authNo);
				
				double authNumsDouble = (double) authNums;
				double maxResultDouble = (double) maxResult;
				
				int currentResult = (int) (authNumsDouble / maxResultDouble * 100);
				
				param.put("authNo", authNo);
				
				String resultGrade = "-1";
				param.put("result", currentResult);
				
				if (currentResult == 100) {
					resultGrade = "0.1";
				} else if (currentResult >= 85) {
					resultGrade = "0";
				}
				
				param.put("resultGrade", resultGrade);
				approvalMapper.updateChallengeResult(param);
			}
			
			param.put("userNo", userNo);
			if (redCardNumber == 0) {
				param.put("expiredMonthNumber", 1);
				userMapper.increaseRedCardNumber(userNo);
				userMapper.updateRedCardExpiredDate(param);
			} else if (redCardNumber == 1) {
				param.put("expiredMonthNumber", 3);
				userMapper.increaseRedCardNumber(userNo);
				userMapper.updateRedCardExpiredDate(param);
				userMapper.clearToken(userNo);
			}
			
			Double totalReward = userMapper.selectTotalReward(userNo);
			Double currentUserCash = userMapper.selectCurrentUserCash(userNo);
			
			Double reportCashAmount = totalReward;
			
			if (totalReward > currentUserCash) {
				reportCashAmount = currentUserCash;
			}
			
			param.put("reportCashAmount", reportCashAmount);
			userMapper.insertUserReportRecord(param);
			userMapper.subtractReward(userNo);
		}
		
		reportMapper.processReport(reportNo);
		
		return "200";
	}
}
