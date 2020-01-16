package goalKeepin.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import goalKeepin.config.GoalKeepinProps;
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
	public String showCategoryList(@PathVariable("pageNum") Integer pageNum, Model model) {
		
		int pageSize = props.getPageSize();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("startIndex", (pageNum - 1) * pageSize);
		paramMap.put("pageSize", pageSize);
		
		List<Category> pageData = categoryMapper.selectCategoryList(paramMap);
		int totalRecordNum = categoryMapper.getTotalCategoryCount();
		
		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		
		model.addAttribute("page", page);

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
}
