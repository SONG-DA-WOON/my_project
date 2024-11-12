package com.project.web.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.co.steellink.user.common.ApiResponse;
import kr.co.steellink.user.common.JSONResponse;
import kr.co.steellink.user.common.YN;
import kr.co.steellink.user.domain.commoncode.CommonRepository;
import kr.co.steellink.user.domain.deliveryregion.DeliveryRegionRepository;
import kr.co.steellink.user.domain.member.MemberService;
import kr.co.steellink.user.domain.member.dto.*;
import kr.co.steellink.user.domain.member.entity.Member;
import kr.co.steellink.user.domain.tokencheck.TokenCheckRepository;
import kr.co.steellink.user.domain.tokencheck.entity.TokenCheck;
import kr.co.steellink.user.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * 회원 조회,수정,삭제
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {
    private final CommonRepository commonRepository;
    private final DeliveryRegionRepository deliveryRegionRepository;
    private final MemberService memberService;
    private final JwtUtils jwtUtils;

    /**
     * 회원가입 페이지
     *
     * @return
     */
    @GetMapping("/join")
    public String join(Model model, @AuthenticationPrincipal Member member) {
        if (member != null) {
            return "redirect:/";
        }
        // 판재류
        model.addAttribute("thickPlateList", commonRepository.findByGroupCdAndViewYn("THICK_PLATE", YN.Y));
        // 철근류
        model.addAttribute("rebarList", commonRepository.findByGroupCdAndViewYn("REBAR", YN.Y));
        // 형강류
        model.addAttribute("sectionsSteelList", commonRepository.findByGroupCdAndViewYn("SECTIONS_STEEL", YN.Y));
        //강관류
        model.addAttribute("steelPipeList", commonRepository.findByGroupCdAndViewYn("STEEL_PIPE", YN.Y));
        // 구매/판매 지역
        model.addAttribute("regionList", deliveryRegionRepository.findByStatus(YN.Y));
        return "member/join_write";
    }

    /**
     * 아이디 중복 체크
     *
     * @param dto 중복 체크 할 아이디
     * @return
     */
    @PostMapping("/join/id/check")
    @ResponseBody
    public ApiResponse<Boolean> joinIdCheck(@RequestBody kr.co.steellink.user.domain.member.dto.MemberLoginIdCheckDto dto) {

        return ApiResponse.ok(memberService.checkLoginId(dto));
    }


    /**
     * 회원가입 저장
     *
     * @param dto
     * @return
     */
    @PostMapping("/join/save")
    @ResponseBody
    public ApiResponse<String> joinSave(@RequestBody @Validated MemberSaveDto dto) {

        return ApiResponse.ok(memberService.saveMember(dto));
    }

    /**
     * 회원가입 완료 페이지
     *
     * @param token token 으로 확인
     * @param model
     * @return
     */
    @GetMapping("/join/complete")
    public String joinComplete(@RequestParam("token") String token, Model model) {
        TokenCheck tokenCheck = tokenCheckRepository.findByJwtToken(token);
        Map<String, Object> payload = jwtUtils.getPayload(token);
        boolean isVerified = jwtUtils.verifyToken(token);

        if (token == null || !isVerified || tokenCheck.getUseYn().equals(YN.Y)) {
            return "redirect:/main";
        }

        Date iatDate = (Date) payload.get("iat");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(iatDate);
        model.addAttribute("loginId", payload.get("loginId").toString());
        model.addAttribute("day", formattedDate);
        model.addAttribute("token", token);
        return "member/join_completed";
    }

    /**
     * 회원가입 jwtToken 사용 업데이트
     *
     * @param map
     * @return
     */
    @PostMapping("/api/token/use")
    @ResponseBody
    public ApiResponse<Boolean> tokenUse(@RequestBody Map<String, Object> map) {
        return ApiResponse.ok(memberService.useToken(map));
    }

    /**
     * 회원가입 휴대폰 인증 번호 전송
     *
     * @param dto 전송할 전화번호
     * @return
     */
    @PostMapping("/api/phone/auth")
    @ResponseBody
    public JSONResponse<?> phoneAuth(@RequestBody MemberPhoneAuthDto dto, Authentication authentication) {
        return new JSONResponse<>(200, "SUCCESS", memberService.authPhoneNumber(dto, authentication));
    }

    /**
     * 회원가입 인증번호 확인
     *
     * @return
     */
    @PostMapping("/api/phone/auth/check")
    @ResponseBody
    public JSONResponse<?> authCheck(@RequestBody MemberAuthNumberDto dto) {
        System.out.println("넘어온 값 확인 : " + dto.getAuthNumber());
        return new JSONResponse<>(200, "SUCCESS", memberService.checkAuthNumber(dto));
    }


    /**
     * 아이디 찾기 페이지
     *
     * @return
     */
    @GetMapping("/find/id")
    public String findId() {
        return "member/find_id";
    }


    /**
     * 아이디 찾기
     *
     * @param dto
     * @return
     */
    @PostMapping("/api/find/id")
    @ResponseBody
    public JSONResponse<?> apiFindId(@RequestBody MemberFindIdDto dto) {
        return new JSONResponse<>(200, "SUCCESS", memberService.searchMemberByNameAndPhoneNo(dto));
    }

    /**
     * 비밀번호 찾기 페이지
     *
     * @return
     */
    @GetMapping("/find/password")
    public String findPassword() {
        return "member/find_password";
    }

    /**
     * 비밀번호 찾기 알림톡 전송
     *
     * @param dto
     * @return
     */
    @PostMapping("/api/find/password")
    @ResponseBody
    public JSONResponse<?> apiFindPassword(@RequestBody MemberFindPasswordDto dto) {
        return new JSONResponse<>(200, "SUCCESS", memberService.searchMemberByLoginIdAndPhoneNo(dto));
    }

    /**
     * 비밀번호 변경 페이지
     *
     * @param token
     * @return
     */
    @GetMapping("/find/password/result")
    public String findPasswordResult(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "member/password_reset";
    }

    /**
     * 비밀번호 변경 API
     *
     * @param data 변경을 위한 정보
     * @return
     */
    @PostMapping("/api/find/password/change")
    @ResponseBody
    public JSONResponse<?> apiFindPasswordChange(@RequestBody Map<String, Object> data) {
        return new JSONResponse<>(200, "SUCCESS", memberService.changePassword(data));
    }

    /**
     * 사업자번호 인증 API
     *
     * @param map
     * @return
     * @throws Exception
     */
    @PostMapping("/api/find/company")
    @ResponseBody
    public JSONResponse<?> apiFindCompany(@RequestBody Map<String, Object> map) throws Exception {
        return new JSONResponse<>(200, "SUCCESS", memberService.findCompany(map));
    }

    /**
     * 장바구니 카운트 API
     *
     * @param member
     * @return
     */
    @PostMapping("/api/cart/count")
    @ResponseBody
    public JSONResponse<?> apiCartCount(@AuthenticationPrincipal Member member) {
        return new JSONResponse<>(200, "SUCCESS", memberService.countCart(member));
    }

    @PostMapping("/api/withdraw/save")
    @ResponseBody
    public JSONResponse<?> apiWithdrawSave(@RequestBody MemberWithdrawReasonDto dto, @AuthenticationPrincipal Member member) {
        return new JSONResponse<>(200, "SUCCESS", memberService.saveWithdraw(dto, member));
    }


    /**
     * mypage > 회원 정보 수정
     *
     * @param dto 회원정보
     */
    @PostMapping("/api/member/update")
    @ResponseBody
    public JSONResponse<?> update(MemberUpdateDto dto) {

        JSONResponse<?> response;

        try {
            response = memberService.updateMember(dto);
        } catch (Exception e) {
            log.error(e.getMessage());
            response = new JSONResponse<>(500, "수정 중 오류가 발생했습니다.");
        }

        return response;
    }

    /**
     * 파이어 베이스 토큰 저장 (푸쉬 알림)
     *
     * @param map 푸쉬 알림 토큰
     * @return
     */
    @PostMapping("/api/token/save")
    @ResponseBody
    public JSONResponse<?> saveToken(@RequestBody Map<String, Object> map) {

        JSONResponse<?> response;
        try {
            response = memberService.saveToken(map);
        } catch (Exception e) {
            return new JSONResponse<>(500, e.getMessage());
        }
        return response;
    }

    @PostMapping("/api/mypage/updatePassword")
    @ResponseBody
    public JSONResponse<?> checkPassword(@RequestBody MemberPasswordUpdateDto request) {

        JSONResponse<?> response;

        response = memberService.checkPassword(request.getCurrentPassword());

        if (response != null) {
            return response;
        }

        response = memberService.validatePassword(request.getNewPassword(), request.getPasswordCheck());

//         validation 통과 여부
        if (response != null) {
            return response;
        }

        // 비밀번호 업데이트 로직

        response = memberService.updatePassword(request.getNewPassword());

        // 1) 현재 비번 일치 여부 체크
//        2) 비번, 비번 확인 입력 여부, 비번 패턴, 비밀번호 다른 여부 체크
        // 3) 비번 변경


        return response;

    }
}
