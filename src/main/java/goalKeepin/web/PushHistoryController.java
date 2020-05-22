package goalKeepin.web;

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
import goalKeepin.data.PushHistoryMapper;
import goalKeepin.model.Page;
import goalKeepin.service.PageService;
import goalKeepin.util.SortUtils;
import goalKeepin.web.dto.PushHistoryResponseDto;

@Controller
@RequestMapping("/pushHistory")
public class PushHistoryController {

	@Autowired
	private PushHistoryMapper pushHistoryMapper;
	
	@Autowired
	private PageService pageService;

	@Autowired
	private GoalKeepinProps props;
	
	@GetMapping("/showPushHistoryList/{pageNum}")
	public String showPushHistoryList(@PathVariable("pageNum") Integer pageNum, Model model,
			@RequestParam(value = "sort", required = false) String sort) {

		int pageSize = props.getPageSize();
		Map<String, Object> paramMap = SortUtils.getParamMap(pageNum, pageSize, model, sort);
		
		List<PushHistoryResponseDto> pageData = pushHistoryMapper.selectPushHistoryList(paramMap);
		int totalRecordNum = pushHistoryMapper.getTotalPushHistoryCount();
		
		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		
		model.addAttribute("page", page);
		
		return "pushHistory/pushHistory";
	}
}
