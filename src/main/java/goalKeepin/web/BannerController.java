package goalKeepin.web;

import java.util.List;
import java.util.Map;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import goalKeepin.data.BannerMapper;
import goalKeepin.model.Banner;
import goalKeepin.model.Page;
import goalKeepin.model.Popup;
import goalKeepin.service.PageService;
import goalKeepin.util.FileUploadUtils;
import goalKeepin.util.SortUtils;

@Controller
@RequestMapping("/banner")
public class BannerController {

	@Autowired
	private PageService pageService;

	@Autowired
	private BannerMapper bannerMapper;

	@Value("${goalkeepin.upload.banner}")
	private String uploadPath;

	@GetMapping("/showBannerList/{pageNum}")
	public String showPopupList(@PathVariable("pageNum") Integer pageNum, Model model,
			@RequestParam(value = "sort", required = false) String sort) {

		int pageSize = 5;
		Map<String, Object> paramMap = SortUtils.getParamMap(pageNum, pageSize, model, sort);

		List<Popup> pageData = bannerMapper.selectBannerList(paramMap);
		int totalRecordNum = bannerMapper.getTotalBannerCount();

		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		model.addAttribute("page", page);

		return "banner/bannerList";
	}

	@GetMapping("/createNewBanner")
	public String createNewPopup(Model model) {
		model.addAttribute("banner", new Banner());
		return "banner/bannerDetailForm";
	}

	@GetMapping("/getMainBannerCount")
	@ResponseBody
	public Integer getMainBannerCount() {

		Integer mainBannerCount = bannerMapper.selectMainBannerCount();
		return mainBannerCount;
	}

	@PostMapping("/processBannerCreation")
	public String processBannerCreation(@RequestParam("bannerTypeCd") String bannerTypeCd,
			@RequestParam("bannerTarget") String bannerTarget,
			@RequestParam(name = "bannerIsMain", required = false) boolean bannerIsMain,
			@RequestParam("bannerImage") MultipartFile file) {

		String filePath = FileUploadUtils.processFileUpload(file, uploadPath, null);

		Banner banner = new Banner();
		banner.setBannerTypeCd(bannerTypeCd);
		banner.setBannerTarget(bannerTarget);
		banner.setBannerImgUrl("/app/goalkeepinImage/bannerImage/" + filePath);
		banner.setBannerIsMain(bannerIsMain);
		bannerMapper.insertNewBanner(banner);
		return "redirect:/banner/showBannerList/1";
	}

	@PostMapping("/saveBannerActivation")
	@ResponseBody
	public String saveBannerActivation(@RequestParam("bannerList") String bannerList) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Object>> bannerListObj = mapper.readValue(bannerList, List.class);

		for (Map<String, Object> banner : bannerListObj) {
			bannerMapper.updateBannerActiveStatus(banner);
		}

		return "200";
	}

	@GetMapping("/showBannerDetail")
	public String showBannerDetail(@RequestParam("bannerNo") Long bannerNo, Model model) {
		Banner banner = bannerMapper.selectBannerDetail(bannerNo);
		model.addAttribute("banner", banner);
		return "banner/bannerDetailForm";
	}

	@PostMapping("/processBannerModification")
	public String proceessBannerCreation(@RequestParam("bannerNo") Long bannerNo,
			@RequestParam("bannerTypeCd") String bannerTypeCd, @RequestParam("bannerTarget") String bannerTarget,
			@RequestParam(name = "bannerIsMain", required = false) boolean bannerIsMain,
			@RequestParam(name = "bannerImage", required = false) MultipartFile file) {

		Banner banner = new Banner();

		long imageSize = file.getSize();

		if (imageSize > 0) {
			String filePath = FileUploadUtils.processFileUpload(file, uploadPath, null);
			banner.setBannerImgUrl("/app/goalkeepinImage/bannerImage/" + filePath);
		}

		banner.setBannerNo(bannerNo);
		banner.setBannerTypeCd(bannerTypeCd);
		banner.setBannerTarget(bannerTarget);
		banner.setBannerIsMain(bannerIsMain);
		bannerMapper.updateBanner(banner);
		return "redirect:/banner/showBannerList/1";
	}

	@PostMapping("/deleteBanner")
	@ResponseBody
	public String deleteBanner(@RequestParam("bannerNo") Long bannerNo) {

		try {
			bannerMapper.deleteBanner(bannerNo);
		} catch (Exception e) {
			e.printStackTrace();
			return "500";
		}
		return "200";
	}
}
