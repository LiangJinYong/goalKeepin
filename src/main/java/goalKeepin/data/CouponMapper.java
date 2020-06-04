package goalKeepin.data;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.model.Coupon;

@Mapper
public interface CouponMapper {

	List<Map<String, Object>> selectCouponList(Map<String, Object> paramMap);

	int getTotalCouponNum();

	List<String> selectCurrentCouponCodeList();

	void insertNewCoupon(Coupon coupon);

	Coupon selectCouponDetail(Long couponNo);

	void updateCouponStatus(Long couponNo);

	List<Map<String, String>> selectCouponUseList(Map<String, Object> paramMap);

	int getTotalCouponUseCount(Map<String, Object> paramMap);

}
