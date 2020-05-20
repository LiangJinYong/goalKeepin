package goalKeepin.web;

import java.util.HashMap;
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

import goalKeepin.config.GoalKeepinProps;
import goalKeepin.data.NoticeMapper;
import goalKeepin.model.Notice;
import goalKeepin.model.Page;
import goalKeepin.service.PageService;
import goalKeepin.util.FileUploadUtils;
import goalKeepin.util.SortUtils;

@Controller
@RequestMapping("/notice")
public class NoticeController {
	
	@Autowired
	private PageService pageService;
	
	@Autowired
	private GoalKeepinProps props;

	@Autowired
	private NoticeMapper noticeMapper;
	
	@Value("${goalkeepin.upload.notice}")
	private String uploadPath;
	
	@GetMapping("/showNoticeList/{pageNum}")
	public String showNoticeList(@PathVariable("pageNum") Integer pageNum, Model model, @RequestParam(value="sort", required=false) String sort) {
		
		int pageSize = props.getPageSize();
		Map<String, Object> paramMap = SortUtils.getParamMap(pageNum, pageSize, model, sort);
		
		List<Notice> pageData = noticeMapper.selectNoticeList(paramMap);
		int totalRecordNum = noticeMapper.getTotalNoticeCount();
		
		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		
		model.addAttribute("page", page);
		
		return "notice/noticeList";
	}
	
	@GetMapping("/createNewNotice")
	public String createNewNotice(Model model) {
		model.addAttribute("notice", new Notice());
		return "notice/noticeDetailForm";
	}
	
	@PostMapping("/processNoticeDetail")
	public String processNoticeDetail(Notice notice, @RequestParam("noticeImgs") MultipartFile[] noticeImgs, @RequestParam(value="editOption", required=false) String editOption) {
		
		long imageSizeEn = noticeImgs[0].getSize();
		long imageSizeTc = noticeImgs[1].getSize();
		long imageSizeSc = noticeImgs[2].getSize();
		
		if (imageSizeEn > 0) {
			String noticeImgUrlEn = FileUploadUtils.processFileUpload(noticeImgs[0], uploadPath, "en");
			notice.setNoticeImgUrlEn("/app/goalkeepinImage/noticeImage/" + noticeImgUrlEn);
		}
		
		if (imageSizeTc > 0) {
			String noticeImgUrlTc = FileUploadUtils.processFileUpload(noticeImgs[1], uploadPath, "tc");
			notice.setNoticeImgUrlTc("/app/goalkeepinImage/noticeImage/" + noticeImgUrlTc);
		}
		
		if (imageSizeSc > 0) {
			String noticeImgUrlSc = FileUploadUtils.processFileUpload(noticeImgs[2], uploadPath, "sc");
			notice.setNoticeImgUrlSc("/app/goalkeepinImage/noticeImage/" + noticeImgUrlSc);
		}
		
		noticeMapper.insertOrUpdateNoticeImgUrl(notice);
		
		noticeMapper.insertOrUpdateNoticeTitle(notice);
		noticeMapper.insertOrUpdateNoticeContent(notice);
		noticeMapper.insertOrUpdateNotice(notice);
		
		return "redirect:/notice/showNoticeList/1";
	}
	
	@GetMapping("/showNoticeDetail")
	public String showNoticeDetail(@RequestParam("noticeNo") Long noticeNo, Model model) {
		
		Notice notice = noticeMapper.selectNoticeDetail(noticeNo);
		model.addAttribute("notice", notice);
		return "notice/noticeDetailForm";
	}
	
	@PostMapping("/deleteNotice")
	@ResponseBody
	public String deleteNotice(@RequestParam("noticeNo") Long noticeNo) {
		
		try {
			noticeMapper.deleteNotice(noticeNo);
		} catch (Exception e) {
			e.printStackTrace();
			return "500";
		}
		return "200";
	}
}
