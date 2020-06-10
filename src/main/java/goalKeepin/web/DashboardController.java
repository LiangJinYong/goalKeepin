package goalKeepin.web;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import goalKeepin.data.DashboardMapper;
import goalKeepin.web.dto.DashboardResponseDto;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

	@Autowired
	private DashboardMapper dashboardMapper;
	
	@GetMapping
	public String getDashboardData(Model model) {
		
		LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd.yyyy HH:mm:ss");
        String currentDatetime = now.format(formatter);
        model.addAttribute("currentDatetime", currentDatetime);
		
		Integer totalUserCount = dashboardMapper.selectTotalUserCount();
		Integer totalParicipantCount=dashboardMapper.selectTotalParicipantCount();
		Double totalFeeAmount=dashboardMapper.selectTotalFeeAmount();
		Double totalRewardAmount=dashboardMapper.selectTotalRewardAmount();
		Double totalPaymentAmount=dashboardMapper.selectTotalPaymentAmount();
		Double totalCommissionAmount=dashboardMapper.selectTotalCommissionAmount();
		
		List<Map<String, Object>> recentOngoingProjectList = dashboardMapper.selectRecentOngongProjectList();
		List<Map<String, Object>> todayApprovalList = dashboardMapper.selectTodayApprovalList();
		List<Map<String, Object>> unpaidRewardList = dashboardMapper.selectUnpaidRewardList();
		
		DashboardResponseDto dashboard = new DashboardResponseDto(totalUserCount, totalParicipantCount, totalFeeAmount, totalRewardAmount, totalPaymentAmount, totalCommissionAmount);
		dashboard.setRecentOngoingProjectList(recentOngoingProjectList);
		dashboard.setTodayApprovalList(todayApprovalList);
		dashboard.setUnpaidRewardList(unpaidRewardList);
		
		model.addAttribute("dashboard", dashboard);
		return "dashboard/dashboard";
	}
}
