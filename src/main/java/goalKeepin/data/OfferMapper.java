package goalKeepin.data;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.model.Offer;

@Mapper
public interface OfferMapper {

	int getTotalOfferCount(Map<String, Object> paramMap);

	List<Offer> selectOfferList(Map<String, Object> paramMap);

	void insertOfferTitleTrans(Offer offer);

	void insertOfferAuthInfoTrans(Offer offer);

	void insertNewOffer(Offer offer);

	Offer selectOfferDetail(Long offerNo);

}
