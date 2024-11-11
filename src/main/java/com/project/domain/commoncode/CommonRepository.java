package kr.co.steellink.user.domain.commoncode;

import kr.co.steellink.user.common.YN;
import kr.co.steellink.user.domain.commoncode.dto.BankInfoDto;
import kr.co.steellink.user.domain.commoncode.entity.CommonCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface CommonRepository extends
        JpaRepository<CommonCode, Long> ,QuerydslPredicateExecutor<CommonCode>,
        CommonRepositoryCustom{
    List<CommonCode> findByGroupCdAndViewYn(String detailCd, YN viewYn);

    List<CommonCode> findByGroupCdAndDetailNmAndViewYn(String detailCd, String detailNm,YN viewYn);

    Optional<CommonCode> findByGroupCdAndDetailCdAndViewYn(String groupCd, String detailCd, YN viewYn);
    @Query("SELECT c.detailCd FROM CommonCode c WHERE c.detailNm = :itemDetailCd")
    String findByDetailCd(String itemDetailCd);

    @Query("SELECT c.detailNm FROM CommonCode c WHERE c.detailCd = :bank AND c.groupCd = 'BANK'")
    String findDetailNmByDetailCd(@Param("bank") String bank);

}