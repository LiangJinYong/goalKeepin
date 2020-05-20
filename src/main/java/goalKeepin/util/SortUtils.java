package goalKeepin.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.ui.Model;

public class SortUtils {

	private SortUtils() {
	}

	public static Map<String, Object> getParamMap(int pageNum, int pageSize, Model model, String sort) {
		
		Map<String, Object> paramMap = new HashMap<>();
		
		if (sort != null && !"".equals(sort)) {
			String[] sortElements = sort.split(",");
			String sortField = sortElements[0];
			String sortOrder = sortElements[1];
			model.addAttribute("sortField", sortField);
			model.addAttribute("sortOrder", sortOrder);
			paramMap.put("sortField", sortField);
			paramMap.put("sortOrder", sortOrder);
		}
		
		paramMap.put("startIndex", (pageNum - 1) * pageSize);
		paramMap.put("pageSize", pageSize);
		
		return paramMap;
	}
}
