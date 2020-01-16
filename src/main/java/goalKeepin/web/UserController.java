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

import goalKeepin.config.GoalKeepinProps;
import goalKeepin.data.UserMapper;
import goalKeepin.model.Page;
import goalKeepin.model.User;
import goalKeepin.service.PageService;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private PageService pageService;
	
	@Autowired
	private GoalKeepinProps props;
	
	@Autowired
	private UserMapper userMapper;
	
	@GetMapping("/showUserList/{country}/{pageNum}")
	public String showUserList(@PathVariable("country") String country, @PathVariable("pageNum") Integer pageNum, Model model) {
		
		String nationalityCd = "NA02";
		
		if ("hk".equals(country)) {
			nationalityCd = "NA01";
		}
		
		int pageSize = props.getPageSize();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("startIndex", (pageNum - 1) * pageSize);
		paramMap.put("pageSize", pageSize);
		paramMap.put("nationalityCd", nationalityCd);
		
		List<User> pageData = userMapper.selectUserList(paramMap);
		int totalRecordNum = userMapper.getTotalUserCount(nationalityCd);
		
		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		
		model.addAttribute("page", page);
		model.addAttribute("country", country);
		
		return "user/userList";
	}
}
