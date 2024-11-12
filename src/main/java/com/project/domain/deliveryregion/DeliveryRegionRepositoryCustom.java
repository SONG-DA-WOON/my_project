package com.project.domain.deliveryregion;

import com.project.domain.deliveryregion.dto.DeliveryRegionListDto;
import kr.co.steellink.user.domain.deliveryregion.dto.DeliveryRegionListDto;

import java.util.List;
import java.util.Map;

public interface DeliveryRegionRepositoryCustom {

    List<DeliveryRegionListDto> searchDeliveryRegionList(Map<String, Object> search);
}
