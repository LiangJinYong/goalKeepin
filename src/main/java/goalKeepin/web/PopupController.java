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
}
