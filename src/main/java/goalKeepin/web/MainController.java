package goalKeepin.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import goalKeepin.data.ChallengeMapper;

@Controller
public class MainController {

	@Autowired
	private ChallengeMapper challengerMapper;
	
	@GetMapping("/")
	public String home(Model model) {
		Map<String, Object> homepageHeaderInfo =  challengerMapper.selectHomepageHeaderInfo();
		model.addAttribute("homepageHeaderInfo", homepageHeaderInfo);
		
		int unprocessedInquiryCount = challengerMapper.selectUnprocessedInquiryCount();
		model.addAttribute("unprocessedInquiryCount", unprocessedInquiryCount);
		
		int unprocessedReportCount = challengerMapper.selectUnprocessedReportCount();
		model.addAttribute("unprocessedReportCount", unprocessedReportCount);
		
		int unprocessedApprovalCount = challengerMapper.selectUnprocessedApprovalCount();
		model.addAttribute("unprocessedApprovalCount", unprocessedApprovalCount);
		
		return "home";
	}
	
	@GetMapping("/getUnprocessedCounts")
	@ResponseBody
	public Map<String, Object> getUnprocessedCounts() {
		Map<String, Object> result = new HashMap<>();
		
		int unprocessedInquiryCount = challengerMapper.selectUnprocessedInquiryCount();
		int unprocessedReportCount = challengerMapper.selectUnprocessedReportCount();
		int unprocessedApprovalCount = challengerMapper.selectUnprocessedApprovalCount();
		
		result.put("unprocessedInquiryCount", unprocessedInquiryCount);
		result.put("unprocessedReportCount", unprocessedReportCount);
		result.put("unprocessedApprovalCount", unprocessedApprovalCount);
		
		return result;
	}
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/loginError")
	public String loginError(Model model) {
		model.addAttribute("loginError", true);
		return "login";
	}
	
	@PostMapping("/logout")
	public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null){    
	        new SecurityContextLogoutHandler().logout(request, response, auth);
	    }
	    return "redirect:/login?logout";
	}
	
}
