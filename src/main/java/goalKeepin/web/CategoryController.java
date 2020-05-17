package goalKeepin.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import goalKeepin.config.GoalKeepinProps;
import goalKeepin.constants.BaseHabitType;
import goalKeepin.data.CategoryMapper;
import goalKeepin.model.Category;
import goalKeepin.model.Page;
import goalKeepin.service.PageService;

@Controller
@RequestMapping("/category")
public class CategoryController {
	
	@Autowired
	private PageService pageService;
	
	@Autowired
	private GoalKeepinProps props;

	@Autowired
	private CategoryMapper categoryMapper;
	
	@GetMapping("/showCategoryList/{pageNum}")
	public String showCategoryList(@PathVariable("pageNum") Integer pageNum, Model model, @RequestParam(value="sort", required=false) String sort) {
		
		int pageSize = props.getPageSize();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("startIndex", (pageNum - 1) * pageSize);
		paramMap.put("pageSize", pageSize);
		
		if (sort != null) {
			String[] sortElements = sort.split(",");
			String sortField = sortElements[0];
			String sortOrder = sortElements[1];
			model.addAttribute("sortField", sortField);
			model.addAttribute("sortOrder", sortOrder);
			paramMap.put("sortField", sortField);
			paramMap.put("sortOrder", sortOrder);
		}
		
		List<Category> pageData = categoryMapper.selectCategoryList(paramMap);
		int totalRecordNum = categoryMapper.getTotalCategoryCount();
		
		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		
		model.addAttribute("page", page);
		
		BaseHabitType[] values = BaseHabitType.values();
		List<String> habitTypeList = new ArrayList<String>();
		
		for(BaseHabitType type: values) {
			habitTypeList.add(type.getBaseHabitTypeName().toUpperCase());
		}
		
		model.addAttribute("habitTypeList", habitTypeList);

		return "category/categoryList";
	}
	
	@GetMapping("/createNewCategory")
	public String createNewCategory(Model model) {
		model.addAttribute("category", new Category());
		return "category/createCategoryForm";
	}
	
	@PostMapping("/processCagegoryCreation")
	public String processCagegoryCreation(Category category) {
		categoryMapper.insertCategoryNmTrans(category);
		categoryMapper.insertCategoryDescriptionTrans(category);
		categoryMapper.insertNewCategory(category);
		return "redirect:/category/showCategoryList/1";
	}
	
	@PostMapping("/toggleCategoryIsMain")
	@ResponseBody
	public String toggleCategoryIsMain(@RequestParam("categoryNo") Long categoryNo) {
		try {
			categoryMapper.toggleCategoryIsMain(categoryNo);
			return "200";
		} catch (Exception e) {
			e.printStackTrace();
			return "500";
		}
	}
	
	@PostMapping("/saveCategoryOrders")
	@ResponseBody
	public String saveCategoryOrders(@RequestBody List<Map<String, Object>> categoryOrders) {
		try {
			for(Map<String, Object> categoryOrder : categoryOrders) {
				categoryMapper.updateCategoryOrders(categoryOrder);
			}
			return "200";
		} catch (Exception e) {
			e.printStackTrace();
			return "500";
		}
	}
}
