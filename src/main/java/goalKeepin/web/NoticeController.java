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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import goalKeepin.config.GoalKeepinProps;
import goalKeepin.data.NoticeMapper;
import goalKeepin.model.Notice;
import goalKeepin.model.Page;
import goalKeepin.service.PageService;
import goalKeepin.util.FileUploadUtils;

@Controller
@RequestMapping("/notice")
public class NoticeController {
	
	@Autowired
	private PageService pageService;
	
	@Autowired
	private GoalKeepinProps props;

	@Autowired
	private NoticeMapper noticeMapper;
	
	private final static String FILE_UPLOAD_PATH = "/var/lib/tomcat8/webapps/goalkeepinImage/noticeImage/";
	
	@GetMapping("/showNoticeList/{pageNum}")
	public String showNoticeList(@PathVariable("pageNum") Integer pageNum, Model model, @RequestParam(value="sort", required=false) String sort) {
		
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
	public String processNoticeDetail(Notice notice, @RequestParam("noticeImgs") MultipartFile[] noticeImgs) {
		
		if (noticeImgs.length > 0) {
			String noticeImgUrlEn = FileUploadUtils.processFileUpload(noticeImgs[0], FILE_UPLOAD_PATH, "en");
			String noticeImgUrlTc = FileUploadUtils.processFileUpload(noticeImgs[1], FILE_UPLOAD_PATH, "tc");
			String noticeImgUrlSc = FileUploadUtils.processFileUpload(noticeImgs[2], FILE_UPLOAD_PATH, "sc");
			
			notice.setNoticeImgUrlEn("/app/goalkeepinImage/noticeImage/" + noticeImgUrlEn);
			notice.setNoticeImgUrlTc("/app/goalkeepinImage/noticeImage/" + noticeImgUrlTc);
			notice.setNoticeImgUrlSc("/app/goalkeepinImage/noticeImage/" + noticeImgUrlSc);
			
			noticeMapper.insertOrUpdateNoticeImgUrl(notice);
		}
		
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
