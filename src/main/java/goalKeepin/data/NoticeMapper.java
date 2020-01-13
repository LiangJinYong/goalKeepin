package goalKeepin.data;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.model.Notice;

@Mapper
public interface NoticeMapper {

	int getTotalNoticeCount();

	List<Notice> selectNoticeList(int startIndex);

	void insertOrUpdateNoticeTitle(Notice notice);

	void insertOrUpdateNoticeContent(Notice notice);

	void insertOrUpdateNoticeImgUrl(Notice notice);

	void insertOrUpdateNotice(Notice notice);

	Notice selectNoticeDetail(Long noticeNo);

	void deleteNotice(Long noticeNo);

}
