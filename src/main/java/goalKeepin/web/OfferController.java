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
import org.springframework.web.bind.annotation.RequestParam;

import goalKeepin.config.GoalKeepinProps;
import goalKeepin.data.OfferMapper;
import goalKeepin.model.Offer;
import goalKeepin.model.Page;
import goalKeepin.service.PageService;
import goalKeepin.util.SortUtils;

@Controller
@RequestMapping("/offer")
public class OfferController {
	
	@Autowired
	private PageService pageService;
	
	@Autowired
	private GoalKeepinProps props;

	@Autowired
	private OfferMapper offerMapper;
	
	@GetMapping("/showOfferList/{pageNum}")
	public String showOfferList(@RequestParam("offerStatusCd") String offerStatusCd, @PathVariable("pageNum") Integer pageNum, Model model, @RequestParam(value="sort", required=false) String sort) {
		
		int pageSize = props.getPageSize();
		
		Map<String, Object> paramMap = SortUtils.getParamMap(pageNum, pageSize, model, sort);
		
		if (offerStatusCd == null) {
			offerStatusCd = "VO00";
		}
		paramMap.put("offerStatusCd", offerStatusCd);
		
		List<Offer> pageData = offerMapper.selectOfferList(paramMap);
		int totalRecordNum = offerMapper.getTotalOfferCount(paramMap);
		
		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		
		model.addAttribute("page", page);
		model.addAttribute("offerStatusCd", offerStatusCd);
		
		return "offer/offerList";
	}
	
	@GetMapping("/createNewOfferForm")
	public String createNewOfferForm(Model model) {
		
		model.addAttribute("offer", new Offer());
		return "offer/offerDetail";
	}
	
	@PostMapping("/processOfferCreation")
	public String processOfferCreation(Offer offer) {
		
		String offerStatusCd = offer.getOfferStatusCd();
		
		// 투표 종료하기
		if ("VO02".equals(offerStatusCd)) {
			offerMapper.updateOfferToVoted(offer);
			return "redirect:/offer/showOfferList/1?offerStatusCd=VO03";
		} else {
			// 관리자 제안등록하기, 일반 유저 제안 등록하기
			offerMapper.insertOfferTitleTrans(offer);
			offerMapper.insertOfferAuthInfoTrans(offer);
			offerMapper.insertNewOffer(offer);
			return "redirect:/offer/showOfferList/1?offerStatusCd=VO02";
		}
	}
	
	@GetMapping("/showOfferDetail")
	public String showOfferDetail(@RequestParam("offerNo") Long offerNo, Model model) {
		Offer offer = offerMapper.selectOfferDetail(offerNo);
		model.addAttribute("offer", offer);
		return "offer/offerDetail";
	}
}
