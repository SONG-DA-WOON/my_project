package com.project.domain.deliveryregion;

import com.project.domain.deliveryregion.dto.DeliveryRegionListDto;
import kr.co.steellink.user.common.YN;
import kr.co.steellink.user.domain.deliveryregion.dto.DeliveryRegionListDto;
import kr.co.steellink.user.domain.deliveryregion.entity.DeliveryRegion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeliveryRegionService {

    private final DeliveryRegionRepository deliveryRegionRepository;

    public List<DeliveryRegionListDto> searchDeliveryRegionList(Map<String, Object> search) {
        return deliveryRegionRepository.searchDeliveryRegionList(search);
    }

    /**
     * 스틸 ON > 파트너스 > 지역
     */
    public List<DeliveryRegion> findAllAndStatusAndDelYn() {
        return deliveryRegionRepository.findAllAndStatusAndDelYn(YN.Y, YN.N);
    }
}
