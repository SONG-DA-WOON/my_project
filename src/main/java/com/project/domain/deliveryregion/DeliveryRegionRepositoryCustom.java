package kr.co.steellink.user.domain.deliveryregion;

import kr.co.steellink.user.domain.deliveryregion.dto.DeliveryRegionListDto;

import java.util.List;
import java.util.Map;

public interface DeliveryRegionRepositoryCustom {

    List<DeliveryRegionListDto> searchDeliveryRegionList(Map<String, Object> search);
}
