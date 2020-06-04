package goalKeepin.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import goalKeepin.config.GoalKeepinProps;
import goalKeepin.data.CouponMapper;
import goalKeepin.model.Coupon;
import goalKeepin.model.Page;
import goalKeepin.service.PageService;
import goalKeepin.util.SortUtils;

@Controller
@RequestMapping("/coupon")
public class CouponController {
	@Autowired
	private PageService pageService;

	@Autowired
	private GoalKeepinProps props;

	@Autowired
	private CouponMapper couponMapper;

	@GetMapping("/showCouponList/{pageNum}")
	public String showCouponList(@PathVariable("pageNum") Integer pageNum, Model model,
			@RequestParam(value = "sort", required = false) String sort) {

		int pageSize = props.getPageSize();
		Map<String, Object> paramMap = SortUtils.getParamMap(pageNum, pageSize, model, sort);

		List<Map<String, Object>> pageData = couponMapper.selectCouponList(paramMap);
		int totalRecordNum = couponMapper.getTotalCouponNum();

		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		model.addAttribute("page", page);

		return "coupon/couponList";
	}

	@GetMapping("/createNewCoupon")
	public String createNewCoupon(Model model) {
		model.addAttribute("coupon", new Coupon());
		return "coupon/couponDetailForm";
	}

	@PostMapping("/processCouponCreation")
	public String processCouponCreation(@RequestParam("couponType") String couponType,
			@RequestParam("couponName") String couponName, @RequestParam("couponGiftAmount") Integer couponGiftAmount,
			@RequestParam("couponUseNumber") Integer couponUseNumber,
			@RequestParam("couponMaxUse") Integer couponMaxUse,
			@RequestParam("couponExpiredDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date couponExpiredDate) {

		Coupon coupon = new Coupon();
		coupon.setCouponType(couponType);
		coupon.setCouponName(couponName);
		coupon.setCouponGiftAmount(couponGiftAmount);
		coupon.setCouponUseNumber(couponUseNumber);
		coupon.setCouponMaxUse(couponMaxUse);
		coupon.setCouponExpiredDate(couponExpiredDate);

		List<String> couponCodeList = couponMapper.selectCurrentCouponCodeList();

		String couponCode = generateCouponCode(6);

		while (couponCodeList.contains(coupon)) {
			couponCode = generateCouponCode(6);
		}

		coupon.setCouponCode(couponCode);

		couponMapper.insertNewCoupon(coupon);
		return "redirect:/coupon/showCouponList/1";
	}

	private String generateCouponCode(int count) {

		StringBuilder builder = new StringBuilder();
		String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

		while (count-- != 0) {
			int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
	}

	@GetMapping("/showCouponDetail")
	public String showCouponDetail(@RequestParam("couponNo") Long couponNo, Model model) {
		Coupon coupon = couponMapper.selectCouponDetail(couponNo);
		model.addAttribute("coupon", coupon);
		return "coupon/couponDetailForm";
	}

	@PostMapping("/deleteCoupon")
	@ResponseBody
	public String deleteCoupon(@RequestParam("couponNo") Long couponNo) {
	
		couponMapper.updateCouponStatus(couponNo);
		
		return "200";
	}

	@GetMapping("/getCouponHistoryData/{couponNo}/{pageNum}")
	@ResponseBody
	public String getCouponHistoryData(@PathVariable("couponNo") Long couponNo,
			@PathVariable("pageNum") Integer pageNum, @RequestParam(value = "sort", required = false) String sort) {
		
		Gson gson = new Gson();
		Map<String, Object> result = new HashMap<>();
		
		int pageSize = props.getPageSize();
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
		
		paramMap.put("couponNo", couponNo);
		
		List<Map<String, String>> pageData = couponMapper.selectCouponUseList(paramMap);
		int totalRecordNum = couponMapper.getTotalCouponUseCount(paramMap);
		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
				
		result.put("page", page);
		return gson.toJson(result);
	}
}
