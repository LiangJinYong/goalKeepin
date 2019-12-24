package goalKeepin.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import goalKeepin.data.CategoryMapper;
import goalKeepin.model.Category;
import goalKeepin.model.Paging;
import goalKeepin.util.PagingUtils;

@Controller
@RequestMapping("/category")
public class CategoryController {

	@Autowired
	private CategoryMapper categoryMapper;
	
	@GetMapping("/showCategoryList/{pageNum}")
	public String showCategoryList(@PathVariable("pageNum") Integer pageNum, Model model) {
		
		int totalCategoryCount = categoryMapper.getTotalCategoryCount();
		Paging paging = PagingUtils.getPaging(pageNum, totalCategoryCount);
		
		int startIndex = (pageNum - 1) * 10;

		List<Category> categoryList = categoryMapper.selectCategoryList(startIndex);
		
		model.addAttribute("categoryList", categoryList);
		model.addAttribute("paging", paging);
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
