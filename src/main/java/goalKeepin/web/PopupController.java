package goalKeepin.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import goalKeepin.data.PopupMapper;
import goalKeepin.model.Page;
import goalKeepin.model.Popup;
import goalKeepin.service.PageService;
import goalKeepin.util.FileUploadUtils;

@Controller
@RequestMapping("/popup")
public class PopupController {

	@Autowired
	private PageService pageService;

	@Autowired
	private PopupMapper popupMapper;

	@Value("${goalkeepin.upload.popup-setting}")
	private String uploadPath;

	@GetMapping("/showPopupList/{pageNum}")
	public String showPopupList(@PathVariable("pageNum") Integer pageNum, Model model,
			@RequestParam(value = "sort", required = false) String sort) {

		int pageSize = 5;
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

		List<Popup> pageData = popupMapper.selectPopupList(paramMap);
		int totalRecordNum = popupMapper.getTotalPopupCount();

		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		model.addAttribute("page", page);

		return "popup/popupList";
	}

	@GetMapping("/createNewPopup")
	public String createNewPopup(Model model) {
		model.addAttribute("popup", new Popup());
		return "popup/popupDetailForm";
	}

	@GetMapping("/getNoticeList/{pageNum}")
	@ResponseBody
	public String getNoticeList(@PathVariable("pageNum") Integer pageNum,
			@RequestParam(value = "sort", required = false) String sort) {
		Gson gson = new Gson();
		Map<String, Object> result = new HashMap<>();

		int pageSize = 5;
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("startIndex", (pageNum - 1) * pageSize);
		paramMap.put("pageSize", pageSize);

		if (sort != null && !"".equals(sort)) {
			String[] sortElements = sort.split(",");
			String sortField = sortElements[0];
			String sortOrder = sortElements[1];
			result.put("sortField", sortField);
			result.put("sortOrder", sortOrder);
			paramMap.put("sortField", sortField);
			paramMap.put("sortOrder", sortOrder);
		}

		List<Map<String, String>> pageData = popupMapper.selectNoticeList(paramMap);
		int totalRecordNum = popupMapper.getTotalNoticeCount();

		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		result.put("page", page);

		return gson.toJson(result);
	}

	@GetMapping("/getChallengeList/{pageNum}")
	@ResponseBody
	public String getChallengeList(@PathVariable("pageNum") Integer pageNum,
			@RequestParam(value = "sort", required = false) String sort) {
		Gson gson = new Gson();
		Map<String, Object> result = new HashMap<>();

		int pageSize = 5;
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("startIndex", (pageNum - 1) * pageSize);
		paramMap.put("pageSize", pageSize);

		if (sort != null && !"".equals(sort)) {
			String[] sortElements = sort.split(",");
			String sortField = sortElements[0];
			String sortOrder = sortElements[1];
			result.put("sortField", sortField);
			result.put("sortOrder", sortOrder);
			paramMap.put("sortField", sortField);
			paramMap.put("sortOrder", sortOrder);
		}

		List<Map<String, String>> pageData = popupMapper.selectChallengeList(paramMap);
		int totalRecordNum = popupMapper.getTotalChallengeCount();

		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		result.put("page", page);

		return gson.toJson(result);
	}

	@PostMapping("/processPopupCreation")
	public String proceessPopupCreation(@RequestParam("popupTypeCd") String popupTypeCd,
			@RequestParam("popupTargetNo") Long popupTargetNo, @RequestParam("popupImage") MultipartFile file) {

		String filePath = FileUploadUtils.processFileUpload(file, uploadPath, null);

		Popup popup = new Popup();
		popup.setPopupTypeCd(popupTypeCd);
		popup.setPopupTargetNo(popupTargetNo);
		popup.setPopupImgUrl("/app/goalkeepinImage/popupImage/" + filePath);
		popupMapper.insertNewPopup(popup);
		return "redirect:/popup/showPopupList/1";
	}

	@GetMapping("/deactivatePopups")
	@ResponseBody
	public String deactivatePopups(HttpServletRequest request) {

		String deactivedPopups = request.getParameter("deactivedPopups");
		String[] popupArr = deactivedPopups.split("-");
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("popupArr", popupArr);

		try {
			popupMapper.deactivatePopups(paramMap);
		} catch (Exception e) {
			e.printStackTrace();
			return "500";
		}

		return "200";
	}

	@GetMapping("/activatePopup")
	@ResponseBody
	public String activatePopup(HttpServletRequest request) {

		String activePopup = request.getParameter("activePopup");

		try {
			popupMapper.deactivateOtherPopups(activePopup);
			popupMapper.activatePopup(activePopup);
		} catch (Exception e) {
			e.printStackTrace();
			return "500";
		}

		return "200";
	}

	@GetMapping("/showPopupDetail")
	public String showPopupDetail(@RequestParam("popupNo") Long popupNo, Model model) {
		Popup popup = popupMapper.selectPopupDetail(popupNo);
		model.addAttribute("popup", popup);
		return "popup/popupDetailForm";
	}

	@PostMapping("/processPopupModification")
	public String proceessPopupCreation(@RequestParam("popupNo") Long popupNo,
			@RequestParam("popupTypeCd") String popupTypeCd, @RequestParam("popupTargetNo") Long popupTargetNo,
			@RequestParam(name = "popupImage", required = false) MultipartFile file) {

		Popup popup = new Popup();

		long imageSize = file.getSize();

		if (imageSize > 0) {
			String filePath = FileUploadUtils.processFileUpload(file, uploadPath, null);
			popup.setPopupImgUrl("/app/goalkeepinImage/popupImage/" + filePath);
		}

		popup.setPopupNo(popupNo);
		popup.setPopupTypeCd(popupTypeCd);
		popup.setPopupTargetNo(popupTargetNo);
		popupMapper.updatePopup(popup);
		return "redirect:/popup/showPopupList/1";
	}

	@PostMapping("/deletePopup")
	@ResponseBody
	public String deletePopup(@RequestParam("popupNo") Long popupNo) {
		
		try {
			popupMapper.deletePopup(popupNo);
		} catch (Exception e) {
			e.printStackTrace();
			return "500";
		}
		return "200";
	}
}
