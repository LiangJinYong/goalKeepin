package goalKeepin.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import goalKeepin.config.GoalKeepinProps;
import goalKeepin.data.ApprovalMapper;
import goalKeepin.model.Approval;
import goalKeepin.model.Page;
import goalKeepin.service.PageService;

@Controller
@RequestMapping("/approval")
public class ApprovalController {
	
	@Autowired
	private ApprovalMapper approvalMapper;
	
	@Autowired
	private PageService pageService;

	@Autowired
	private GoalKeepinProps props;

	@GetMapping("/showApprovalList/{pageNum}")
	public String showApprovalList(@PathVariable("pageNum") Integer pageNum, Model model,
			HttpServletRequest request, @RequestParam(value = "sort", required = false) String sort) {

		Enumeration<String> parameterNames = request.getParameterNames();
		while(parameterNames.hasMoreElements()) {
			String param = parameterNames.nextElement();
			String value = request.getParameter(param);
			System.out.println("==>" + param + ":" + value);
			model.addAttribute(param, value);
		}
		
		int pageSize = props.getPageSize();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("startIndex", (pageNum - 1) * pageSize);
		paramMap.put("pageSize", pageSize);
		
		Map<String, String[]> approvalFormParam = request.getParameterMap();
		paramMap.putAll(convertParamMap(approvalFormParam));

		if (sort != null) {
			String[] sortElements = sort.split(",");
			String sortField = sortElements[0];
			String sortOrder = sortElements[1];
			model.addAttribute("sortField", sortField);
			model.addAttribute("sortOrder", sortOrder);
			paramMap.put("sortField", sortField);
			paramMap.put("sortOrder", sortOrder);
		}

		List<Approval> pageData = approvalMapper.selectApprovalList(paramMap);
		int totalRecordNum = approvalMapper.getTotalApprovalCount(paramMap);

		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);

		model.addAttribute("page", page);

		return "approval/approvalList";
	}
	
	private Map<String, String> convertParamMap(Map<String, String[]> param) {
		Map<String, String> result = new HashMap<>();
		Set<Entry<String,String[]>> entrySet = param.entrySet();
		
		for(Entry<String, String[]> entry: entrySet) {
			result.put(entry.getKey(), entry.getValue()[0]);
		}
		
		return result;
	}
}
