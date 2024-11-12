package com.project.domain.deliveryregion;


import com.project.domain.deliveryregion.dto.DeliveryRegionListDto;
import kr.co.steellink.user.common.YN;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;


public interface DeliveryRegionRepository extends
        JpaRepository<DeliveryRegion, Long>, QuerydslPredicateExecutor<DeliveryRegion>,
        kr.co.steellink.user.domain.deliveryregion.DeliveryRegionRepositoryCustom {

    List<DeliveryRegion> findByStatus(YN yn);

    List<DeliveryRegion> findByStatusAndAddrContaining(YN yn, String addr);

    @Query("SELECT d FROM DeliveryRegion d WHERE d.status = :yn AND d.delYn = :yn1")
    List<DeliveryRegion> findAllAndStatusAndDelYn(YN yn, YN yn1);

}
