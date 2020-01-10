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
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import goalKeepin.data.PopupMapper;
import goalKeepin.model.Paging;
import goalKeepin.model.Popup;
import goalKeepin.util.PagingUtils;

@Controller
@RequestMapping("/popup")
public class PopupController {

	@Autowired
	private PopupMapper popupMapper;
	
	@GetMapping("/showPopupList/{pageNum}")
	public String showPopupList(@PathVariable("pageNum") Integer pageNum, Model model) {
		int totalPopupCount = popupMapper.getTotalPopupCount();
		Paging paging = PagingUtils.getPaging(pageNum, totalPopupCount);
		
		int startIndex = (pageNum - 1) * 10;
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("startIndex", startIndex);
		
		List<Popup> popupList =  popupMapper.selectPopupList(paramMap);
		
		model.addAttribute("popupList", popupList);
		model.addAttribute("paging", paging);
		
		return "popup/popupList";
	}
	
	@GetMapping("/createNewPopup")
	public String createNewPopup(Model model) {
		model.addAttribute("popup", new Popup());
		return "popup/createPopupForm";
	}

	@GetMapping("/getNoticeList/{pageNum}")
	@ResponseBody
	public String getNoticeList(@PathVariable("pageNum") Integer pageNum) {
		Gson gson = new Gson();
		Map<String, Object> result = new HashMap<>();
		
		int totalNoticeCount = popupMapper.getTotalNoticeCount();
		Paging paging = PagingUtils.getPaging(pageNum, totalNoticeCount);
		paging.setPageSize(5);
		int startIndex = (pageNum - 1) * 5;
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("startIndex", startIndex);
		
		List<Map<String, String>> noticeList = popupMapper.selectNoticeList(paramMap);
		result.put("contentList", noticeList);
		result.put("paging", paging);
		return gson.toJson(result);
	}
	
	@GetMapping("/getChallengeList/{pageNum}")
	@ResponseBody
	public String getChallengeList(@PathVariable("pageNum") Integer pageNum) {
		Gson gson = new Gson();
		Map<String, Object> result = new HashMap<>();
		
		int totalChallengeCount = popupMapper.getTotalChallengeCount();
		Paging paging = PagingUtils.getPaging(pageNum, totalChallengeCount);
		paging.setPageSize(5);
		
		int startIndex = (pageNum - 1) * 5;
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("startIndex", startIndex);
		
		List<Map<String, String>> challengeList = popupMapper.selectChallengeList(paramMap);
		result.put("contentList", challengeList);
		result.put("paging", paging);
		return gson.toJson(result);
	}
	
}
