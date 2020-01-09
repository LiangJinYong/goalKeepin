package goalKeepin.web;

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

import goalKeepin.data.ChallengeMapper;

@Controller
public class MainController {

	@Autowired
	private ChallengeMapper challengerMapper;
	
	@GetMapping("/")
	public String home(Model model) {
		Map<String, Object> homepageHeaderInfo =  challengerMapper.selectHomepageHeaderInfo();
		model.addAttribute("homepageHeaderInfo", homepageHeaderInfo);
		return "home";
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
	    return "redirect:/login?logout";//You can redirect wherever you want, but generally it's a good practice to show login screen again.
	}
	
}
