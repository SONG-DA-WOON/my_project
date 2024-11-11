package kr.co.steellink.user.web.controller.main;


import kr.co.steellink.user.common.JSONResponse;
import kr.co.steellink.user.domain.advertisement.AdvertisementService;
import kr.co.steellink.user.domain.advertisement.dto.AdvertisementSaveDto;
import kr.co.steellink.user.domain.estimate.EstimateService;
import kr.co.steellink.user.domain.estimateset.dto.EstimateSetListDto;
import kr.co.steellink.user.domain.estimateset.EstimateSetService;
import kr.co.steellink.user.domain.freemarket.FreeMarketService;
import kr.co.steellink.user.domain.mainBanner.MainBannerService;
import kr.co.steellink.user.domain.mainBanner.dto.MainBannerListDto;
import kr.co.steellink.user.domain.mainReview.MainReviewService;
import kr.co.steellink.user.domain.mainReview.dto.MainReviewListDto;
import kr.co.steellink.user.domain.member.entity.Member;
import kr.co.steellink.user.domain.steelpriceinfo.SteelPriceInfoService;
import kr.co.steellink.user.domain.steelpriceinfo.dto.RealTimePriceInfoDto;
import kr.co.steellink.user.domain.terms.TermsRepository;
import kr.co.steellink.user.domain.terms.enumset.TermsType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final MainReviewService mainReviewService;
    private final MainBannerService mainBannerService;
    private final SteelPriceInfoService steelPriceInfoService;
    private final EstimateSetService estimateSetService;
    private final FreeMarketService freeMarketService;
    private final EstimateService estimateService;
    private final TermsRepository termsRepository;
    private final AdvertisementService advertisementService;

    /**
     * 메인 페이지
     *
     * @return
     */
    @GetMapping({"/", "/main", ""})
    public String main(Model model) {

        // 파이어베이스 실행(토큰 발급 위해)
//        fcmService.init();

        // 사용자 후기
        List<MainReviewListDto> mainReviewList = mainReviewService.searchMainReview();

        // 메인 배너
        List<MainBannerListDto> mainBannerList = mainBannerService.searchMainBannerList();

        // 아이디로 철강재 유통가격 정보 가져오기
        List<RealTimePriceInfoDto> realTimePriceInfo = steelPriceInfoService.searchRealTimePriceInfo();

        // 최근 올라온 견적 문의 3개 가져오기
        List<EstimateSetListDto> estimateSetListDtoForMainPList = estimateSetService.searchEstimateSetListDtoForMainP();

        model.addAttribute("realTimePriceInfo", realTimePriceInfo);
        model.addAttribute("estimateSetListDtoForMainPList", estimateSetListDtoForMainPList);
        model.addAttribute("mainReviewList", mainReviewList);
        model.addAttribute("mainBannerList", mainBannerList);

        return "main/index";
    }

    /**
     * 앱 헬스체크
     * @return
     */
    @GetMapping("/health-check")
    @ResponseBody
    public String appHealthCheck() {
        return "ok";
    }

    /**
     * 메인 전체 검색 결과 페이지
     * @return
     */
    @GetMapping("/search/result")
    public String mainSearch(Model model, @RequestParam("search") String search, @AuthenticationPrincipal Member member) {

        // 검색 결과를 모델에 추가할 수 있습니다.
        model.addAttribute("estimateList", estimateService.searchEstimateMain(search));
        model.addAttribute("search", search);
        model.addAttribute("freeMarketList", freeMarketService.searchFreeMarketMain(search));
        boolean isAuthenticated = (member != null);
        model.addAttribute("isAuthenticated", isAuthenticated);
        return "main/search_result";
    }

    /**
     * 메인 > 약관보기
     * @param category 약관 종류
     */
    @GetMapping("/api/search/term/{category}")
    @ResponseBody
    public Map<String,Object> getTerm(@PathVariable("category") String category) {

        Map<String,Object> result = new HashMap<>();
        String term = termsRepository.findTermsContentByCategoryType(TermsType.ofCode(category));

        result.put("content", term);

        return result;
    }

    /**
     * 메인 > 관고 문의
     * @param advertisementSaveDto 광고 문의 저장 객체
     *
     */
    @PostMapping("/api/save/adbertisement")
    @ResponseBody
    public JSONResponse<?> saveAdbertisement( AdvertisementSaveDto advertisementSaveDto) {

        try {
            advertisementService.saveAdvertisement(advertisementSaveDto);

        } catch (Exception e) {
            return new JSONResponse<>(500, e.getMessage());
        }
        return new JSONResponse<>(200, "SUCCESS");
    }
}
