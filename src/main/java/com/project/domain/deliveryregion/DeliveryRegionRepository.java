package kr.co.steellink.user.domain.deliveryregion;

import kr.co.steellink.user.common.YN;
import kr.co.steellink.user.domain.deliveryregion.entity.DeliveryRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;


public interface DeliveryRegionRepository extends
        JpaRepository<DeliveryRegion, Long>, QuerydslPredicateExecutor<DeliveryRegion>,
        DeliveryRegionRepositoryCustom {

    List<DeliveryRegion> findByStatus(YN yn);

    List<DeliveryRegion> findByStatusAndAddrContaining(YN yn, String addr);

    @Query("SELECT d FROM DeliveryRegion d WHERE d.status = :yn AND d.delYn = :yn1")
    List<DeliveryRegion> findAllAndStatusAndDelYn(YN yn, YN yn1);

}
