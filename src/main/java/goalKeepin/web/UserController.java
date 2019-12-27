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

import goalKeepin.data.UserMapper;
import goalKeepin.model.Paging;
import goalKeepin.model.User;
import goalKeepin.util.PagingUtils;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserMapper userMapper;
	
	@GetMapping("/showUserList/{country}/{pageNum}")
	public String showUserList(@PathVariable("country") String country, @PathVariable("pageNum") Integer pageNum, Model model) {
		
		String nationalityCd = "NA02";
		
		if ("hk".equals(country)) {
			nationalityCd = "NA01";
		}
		
		int totalUserCount = userMapper.getTotalUserCount(nationalityCd);
		Paging paging = PagingUtils.getPaging(pageNum, totalUserCount);
		
		int startIndex = (pageNum - 1) * 10;
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("startIndex", startIndex);
		paramMap.put("nationalityCd", nationalityCd);
		
		List<User> userList = userMapper.selectUserList(paramMap);
		model.addAttribute("userList", userList);
		model.addAttribute("paging", paging);
		model.addAttribute("country", country);
		
		return "user/userList";
	}
}
