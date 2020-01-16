package goalKeepin.data;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.model.Category;

@Mapper
public interface CategoryMapper {

	int getTotalCategoryCount();

	List<Category> selectCategoryList(Map<String, Object> startIndex);

	void insertCategoryNmTrans(Category category);

	void insertCategoryDescriptionTrans(Category category);

	void insertNewCategory(Category category);
}
