package com.project.domain.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.domain.member.dto.MemberLoginIdCheckDto;
import com.project.domain.member.dto.MemberPhoneAuthDto;
import com.project.domain.member.dto.MemberSaveDto;
import com.project.domain.member.entity.Member;
import com.project.domain.member.enumset.MemberStatus;
import com.project.domain.member.enumset.MemberType;
import com.project.domain.memberbusinessitem.dto.MemberBusinessItemDto;
import com.project.domain.memberbusinessitem.entity.MemberBusinessItem;
import com.project.domain.memberbusinessregion.entity.MemberBusinessRegion;
import com.project.domain.tokencheck.entity.TokenCheck;
import kr.co.steellink.user.common.JSONResponse;
import kr.co.steellink.user.common.YN;
import kr.co.steellink.user.common.exception.ApiBadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final EstimateCartRepository estimateCartRepository;
    private final MemberRepository memberRepository;
    private final JwtUtils jwtUtils;
    private final TokenCheckRepository tokenCheckRepository;
    private final MemberCredentialsRepository memberCredentialsRepository;
    private final MemberBusinessRegionRepository memberBusinessRegionRepository;
    private final MemberBusinessItemRepository memberBusinessItemRepository;
    private final EstimateSetRepository estimateSetRepository;
    private final EstimateSetHistoryRepository estimateSetHistoryRepository;
    private final BidSetRepository bidSetRepository;
    private final MemberPointService memberPointService;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])[A-Za-z\\d@#$%^&+=!]{8,12}$");
    @Value("${nice.client_id}")
    private String clientId;

    @Value("${nice.client_secret}")
    private String clientSecret;

    @Value("${nice.access_token}")
    private String accessToken;

    /**
     * 로그인 아이디 중복 체크
     *
     * @param dto 중복 체크 할 데이터
     * @return
     */
    @Transactional
    public boolean checkLoginId(MemberLoginIdCheckDto dto) {
        Member member = memberRepository.findByLoginId(dto.getLoginId());

        if (member != null) {
            throw new ApiBadRequestException(404, "중복된 아이디입니다.");
        }
        return true;
    }

    /**
     * 회원 가입 저장
     *
     * @param dto 저장할 데이터
     * @return
     */
    @Transactional
    public String saveMember(MemberSaveDto dto) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (!dto.getPassword().equals(dto.getPasswordCheck())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        Member savedMember = new Member();

        Member member = Member.builder()
                .loginId(dto.getLoginId())
                .pwd(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getMemberName())
                .phoneNo(dto.getPhoneNumber())
                .businessNo(dto.getBusinessNumber())
                .ceo(dto.getCeo())
                .company(dto.getCompany())
                .sttsCd(MemberStatus.WAIT)
                .memType(MemberType.NORMAL)
                .role("ROLE_USER")
                // 판재류
                .subPlate(dto.getPlate())
                //철근류
                .subSteel(dto.getRebar())
                //형강류
                .subSectionsSteel(dto.getSection())
                //강관류
                .subSteelPipe(dto.getPipe())
                .alarmBuyYn(YN.ofYn(dto.getAlarmBuyYn()))
                .alarmSellYn(YN.ofYn(dto.getAlarmSellYn()))
                .companyFile(dto.getFileId())
                .delYn(YN.N)
                .build();

        try {
            savedMember = memberRepository.save(member);
        } catch (Exception e) {
            log.error("회원가입 저장 실패 : " + e.getMessage());
        }

        // 관심지역 저장 메소드
        try {
            saveMemberBusinessRegion(dto.getRegion(), member);
        } catch (Exception e) {
            log.error("메소드 회원 관심지역 저장 실패 ", e.getMessage());
        }

        // 관심 품목 저장 메소드
        try {
            processCategories(dto.getCategories(), member);
        } catch (Exception e) {
            log.error("메소드 관심 품목 저장 실패", e.getMessage());
        }
        // jwtToken 생성
        String jwtToken = jwtUtils.builder()
                .claim("loginId", member.getLoginId())
                .build();

        TokenCheck tokenCheck = TokenCheck.builder()
                .jwtToken(jwtToken)
                .useYn(YN.N)
                .build();
        // 토큰 테이블 저장
        try {
            tokenCheckRepository.save(tokenCheck);
        } catch (Exception e) {
            log.error("회원가입 jwtToken 저장 실패", e.getMessage());
        }
        // 포인트 저장 및 포인트 내역 저장
        try {
            memberPointService.pointPlusOrMinus(MEM_REGISTRATION, savedMember.getId());
        } catch (Exception e) {
            log.error("회원가입 포인트 저장 실패", e.getMessage());
        }


        return jwtToken;
    }

    /**
     * 관심 지역 저장 메소드
     *
     * @param region
     * @param member
     */
    @Transactional
    public void saveMemberBusinessRegion(String region, Member member) {
        String[] regionArray = region.split(",");

        List<Long> regionIdArray = Arrays.stream(regionArray)
                .map(Long::parseLong)
                .toList();

        for (Long id : regionIdArray) {
            MemberBusinessRegion memberBusinessRegion = MemberBusinessRegion.builder()
                    .memId(member.getId())
                    .deliveryRegionId(id)
                    .build();

            try {
                memberBusinessRegionRepository.save(memberBusinessRegion);
            } catch (Exception e) {
                log.error("회원 관심 지역테이블 에러", e.getMessage());
            }
        }
    }

    @Transactional
    public void processCategoryItems(List<String> items, String categoryName, Member member) {
        for (String item : items) {
            MemberBusinessItem memberBusinessItem = MemberBusinessItem.builder()
                    .memId(member.getId())
                    .itemGroupCd(categoryName)
                    .itemDetailCd(item)
                    .build();

            try {
                memberBusinessItemRepository.save(memberBusinessItem);
            } catch (Exception e) {
                log.error("memberBusinessItem 저장 실패 ", e.getMessage());
            }
            System.out.println("넘어온 item : " + item);
            System.out.println("Processing " + categoryName + ": " + item);
            // 처리 로직 추가
        }
    }

    @Transactional
    public void processCategories(MemberBusinessItemDto categoryDTO, Member member) {
        processCategoryItems(categoryDTO.getPipe(), "STEEL_PIPE", member);
        processCategoryItems(categoryDTO.getPlate(), "THICK_PLATE", member);
        processCategoryItems(categoryDTO.getRebar(), "REBAR", member);
        processCategoryItems(categoryDTO.getSection(), "SECTIONS_STEEL", member);
    }


    /**
     * jwtToken 사용으로 변경
     *
     * @param map
     * @return
     */
    @Transactional
    public Boolean useToken(Map<String, Object> map) {
        String token = map.get("jwtToken").toString();
        TokenCheck tokenCheck = tokenCheckRepository.findByJwtToken(token);

        tokenCheck.setUseYn(YN.Y);

        try {
            tokenCheckRepository.save(tokenCheck);
        } catch (Exception e) {
            log.error("토큰 사용으로 변경중 에러", e.getMessage());
        }


        return true;
    }

    /**
     * 인증번호 전송
     *
     * @param dto
     * @param authentication
     * @return
     */
    @Transactional
    public JSONResponse<?> authPhoneNumber(MemberPhoneAuthDto dto, Authentication authentication) {
        String phoneNumber = dto.getPhoneNumber();  // 요청에서 전화번호를 가져옴
        Optional<MemberCredentials> dbMemberCredentialsOptional = memberCredentialsRepository.findTopByPhoneNoOrderByRegDtDesc(phoneNumber);  // 전화번호로 최근 인증번호 정보 조회

        LocalDateTime now = LocalDateTime.now();  // 현재 시간을 가져옴

        // 3분 이내 유효한 인증번호가 존재하면 새로 발송하지 않음 (새로고침이면 발송)
        if (dbMemberCredentialsOptional.isPresent()) {
            MemberCredentials latestCredentials = dbMemberCredentialsOptional.get();  // 최신 인증번호 정보 가져옴

            // 인증번호가 유효하고 사용되지 않은 경우, 새로고침 여부 확인
            if (latestCredentials.getExpiredDt().isAfter(now.minusMinutes(3)) && latestCredentials.getUseYn() == YN.N && !isNewRequest()) {
                return new JSONResponse<>(400, "3분 이내에 이미 인증번호가 발송되었습니다.");  // 3분 이내에 인증번호가 발송되었으면 발송하지 않음
            }
        }

        String authNumber = generateRandomNumber();  // 인증번호 생성
        UUID uuidAuthNumber = generateUUIDFromAuthNumber(authNumber);
        MemberCredentials memberCredentials = buildMemberCredentials(authentication, phoneNumber, authNumber, uuidAuthNumber, now);

        try {
            memberCredentialsRepository.save(memberCredentials);
        } catch (Exception e) {
            log.error("인증번호 저장에 실패하였습니다.", e);
            return new JSONResponse<>(500, "인증번호 저장에 실패하였습니다.");
        }

        return new JSONResponse<>(200, "인증번호를 전송했습니다.", authNumber);
    }

    /**
     * 새로고침 감지
     *
     * @return
     */
    private boolean isNewRequest() {
        return true;
    }

    /**
     * {@link MemberCredentials 테이블 저장 메소드}
     *
     * @param authentication
     * @param phoneNumber
     * @param authNumber
     * @param uuidAuthNumber
     * @param now
     * @return
     */
    private MemberCredentials buildMemberCredentials(Authentication authentication, String phoneNumber, String authNumber, UUID uuidAuthNumber, LocalDateTime now) {
        // MemberCredentials 객체를 빌드하여 반환
        return MemberCredentials.builder()
                .memberId(getMemberId(authentication))  // 인증된 사용자 ID 가져오기
                .phoneNo(phoneNumber)  // 전화번호 설정
                .authNo(authNumber)  // 생성된 인증번호 설정
                .authNoToken(uuidAuthNumber.toString())  // 인증번호에 대한 UUID 설정
                .expiredDt(now.plusMinutes(3))  // 인증번호 만료 시간 설정 (3분 뒤)
                .delYn(YN.N)  // 삭제 여부 (N: 삭제되지 않음)
                .useYn(YN.N)  // 사용 여부 (N: 사용되지 않음)
                .build();
    }

    /**
     * 회원 정보 가져오기
     *
     * @param authentication
     * @return
     */
    private Long getMemberId(Authentication authentication) {
        if (authentication == null) return null;
        Member member = memberRepository.findByLoginIdAndSttsCd(authentication.getName(), MemberStatus.NORMAL);  // 인증된 사용자의 정보를 DB에서 조회
        return member != null ? member.getId() : null;
    }


    /**
     * 인증번호 생성 (랜덤 6자리 번호)
     *
     * @return
     */
    public static String generateRandomNumber() {
        Random random = new Random();
        StringBuilder authNumber = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            authNumber.append(random.nextInt(10));
        }

        // UUID 생성 후, 기존 숫자 문자열에 결합
        return authNumber.toString();
    }

    /**
     * 랜덤 6자리 uuid (확인용)
     *
     * @param authNumber 인증 번호
     * @return
     */
    public static UUID generateUUIDFromAuthNumber(String authNumber) {
        String uuidString = authNumber + UUID.randomUUID().toString().substring(6);
        return UUID.fromString(uuidString);
    }

    /**
     * 회원가입 인증번호 확인
     *
     * @param dto
     * @return
     */
    @Transactional
    public JSONResponse<?> checkAuthNumber(MemberAuthNumberDto dto) {
        MemberCredentials memberCredentials = memberCredentialsRepository.findByAuthNo(dto.getAuthNumber());
        if (memberCredentials == null) {
            return new JSONResponse<>(500, "인증번호가 일치하지 않습니다.");
        }

        LocalDateTime now = LocalDateTime.now();

        // 인증 시간이 3분이 초과되었을 경우
        if (now.isAfter(memberCredentials.getExpiredDt())) {
            return new JSONResponse<>(500, "인증 시간이 만료되었습니다. 다시 인증을 요청해주세요.");
        }

        memberCredentials.setUseYn(YN.Y);

        try {
            memberCredentialsRepository.save(memberCredentials);
        } catch (Exception e) {
            return new JSONResponse<>(500, "사용 여부 Y로 업데이트 중 오류", e.getMessage());
        }
        return new JSONResponse<>(200, "인증이 완료되었습니다.");
    }

    /**
     * 아이디 찾기
     *
     * @param dto 휴대폰번호, 이름
     * @return
     */
    @Transactional
    public JSONResponse<?> searchMemberByNameAndPhoneNo(MemberFindIdDto dto) {
        List<MemberFindIdDto> member = memberRepository.searchMemberByNameAndPhoneNo(dto);
        if (member == null || member.isEmpty()) {
            return new JSONResponse<>(500, "FALSE");
        }
        return new JSONResponse<>(200, "SUCCESS", member);
    }

    /**
     * 회원 비밀번호 찾기 알림톡 API
     *
     * @param dto
     * @return
     */
    public JSONResponse<?> searchMemberByLoginIdAndPhoneNo(MemberFindPasswordDto dto) {
        Member member = memberRepository.findByLoginIdAndPhoneNo(dto.getLoginId(), dto.getPhoneNo());

        if (member == null) {
            return new JSONResponse<>(500, "FALSE");
        }

        //TODO 추후 알림톡 작업 필요 및 추후 로직 수정 필요
        String jwtToken = jwtUtils.builder()
                .claim("memberId", member.getId())
                .build();

        TokenCheck tokenCheck = TokenCheck.builder()
                .jwtToken(jwtToken)
                .useYn(YN.N)
                .build();

        return new JSONResponse<>(200, "SUCCESS", tokenCheck);
    }

    /**
     * 비밀번호 변경 API
     *
     * @param data 비밀번호 변경을 위한 정보
     * @return
     */
    @Transactional
    public JSONResponse<?> changePassword(Map<String, Object> data) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        Map<String, Object> payload = jwtUtils.getPayload(data.get("token").toString());
        Long memberId = Long.valueOf(payload.get("memberId").toString());
        String password = data.get("password").toString();
        String passwordCheck = data.get("passwordCheck").toString();

        if (!hasText(password)) {
            return new JSONResponse<>(500, "비밀번호를 입력해주세요.");
        }

        if (!hasText(passwordCheck)) {
            return new JSONResponse<>(500, "비밀번호 확인을 입력해주세요.");
        }

        if (!password.matches(passwordCheck)) {
            return new JSONResponse<>(400, "비밀번호가 일치하지 않습니다.");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원정보가 없습니다"));

        member.setPwd(passwordEncoder.encode(password));

        try {
            memberRepository.save(member);
        } catch (Exception e) {
            return new JSONResponse<>(500, "비밀번호 변경 중 에러");
        }
        return new JSONResponse<>(200, "SUCCESS");
    }

    /**
     * 사업자 등록번호 API
     *
     * @param map 사업자 등록번호
     * @return
     * @throws JsonProcessingException
     */
    @Transactional
    public JSONResponse<?> findCompany(Map<String, Object> map) throws Exception {
        // 사업자 등록번호
        String businessNumber = map.get("businessNumber").toString();

        // 사업자 등록번호 빈문자 validation
        if (!hasText(businessNumber)) {
            return new JSONResponse<>(400, "사업자 등록번호를 입력해주세요.");
        }

        // 10자리 validation
        if (!businessNumber.matches("\\d{10}")) {
            return new JSONResponse<>(400, "사업자 등록번호는 10자리 숫자여야 합니다.");
        }

        // URL 및 timeStamp
        String apiUrl = "https://svc.niceapi.co.kr:22001/digital/niceid/api/v1.0/comp/check";
        long currentTimestamp = System.currentTimeMillis() / 1000;

        // 토큰 생성
        String tokenString = String.join(":", accessToken, String.valueOf(currentTimestamp), clientId);
        String bearerToken = "Bearer " + Base64.getEncoder().encodeToString(tokenString.getBytes(StandardCharsets.UTF_8));

        // header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", bearerToken);
        headers.set("client_id", clientId);
        headers.set("ProductID", "2101764012");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> dataBody = new HashMap<>();

        // 필수값 (사업자 등록 번호)
        dataBody.put("comp_num", businessNumber);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("dataBody", dataBody);

        String jsonBody = objectMapper.writeValueAsString(requestBody);
        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            // 실제 요청
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> resultData = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {
                });

                Map<String, Object> resultDataBody = (Map<String, Object>) resultData.get("dataBody");
                if ("".equals(resultDataBody.get("comp_name")) && "".equals(resultDataBody.get("representive_name"))) {
                    return new JSONResponse<>(500, "존재하지 않는 사업자 등록번호 입니다.");
                }
                return new JSONResponse<>(200, "인증이 완료되었습니다.", resultData);
            } else {
                System.err.println("API 요청 실패: 상태 코드 " + response.getStatusCode() + ", 응답 본문: " + response.getBody());
                return new JSONResponse<>(500, "사업자 등록번호 인증을 실패했습니다.");
            }
        } catch (HttpClientErrorException e) {
            System.err.println("클라이언트 오류 발생: " + e.getResponseBodyAsString());
            return new JSONResponse<>(500, "사업자 등록번호 인증을 실패했습니다.", e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("API 요청 중 오류 발생: " + e.getMessage());
            return new JSONResponse<>(500, "사업자 등록번호 인증을 실패했습니다.", e.getMessage());
        }
    }

    /**
     * 장바구니 카운트 API
     *
     * @param member
     * @return
     */
    public JSONResponse<?> countCart(Member member) {
        // member가 null인지 확인합니다.
        if (member == null) {
            return new JSONResponse<>(400, "MEMBER_NOT_FOUND", null);
        }

        Long count = estimateCartRepository.countByMember_Id(member.getId());
        return new JSONResponse<>(200, "SUCCESS", count);
    }

    /**
     * 회원 정보 수정 탈퇴
     *
     * @param dto
     * @param member
     * @return
     */
    public JSONResponse<?> saveWithdraw(MemberWithdrawReasonDto dto, Member member) {
        System.out.println("넘어온 이유 : " + dto.getReason());
        member.setExitDt(LocalDateTime.now());
        member.setExitReason(dto.getReason());
        member.setSttsCd(MemberStatus.WITHDRAW);

        memberRepository.save(member);
        return new JSONResponse<>(200, "SUCCESS");
    }

    /**
     * 회원 정보 업데이트 validation 체크
     *
     * @param dto 회원정보
     */
    public JSONResponse<?> validateUpdateMember(MemberUpdateDto dto) {

        if (!hasText(dto.getPhoneNumber())) {
            return new JSONResponse<>(400, "휴대폰 번호를 입력해주세요.");
        }
        if (!hasText(dto.getBank())) {
            return new JSONResponse<>(400, "은행을 선택해주세요.");
        }
        if (!hasText(dto.getAccountNo())) {
            return new JSONResponse<>(400, "계좌번호를 입력해주세요.");
        }

        if ((!hasText(dto.getRegion()))) {
            return new JSONResponse<>(400, "관심지역을 1개 이상 선택해주세요.");
        }

        return null;
    }

    /**
     * 회원 정보 업데이트
     *
     * @param dto 회원정보
     * @return
     */
    public JSONResponse<?> updateMember(MemberUpdateDto dto) {

        // validation 체크
        JSONResponse<?> errorResponse = validateUpdateMember(dto);
        if (errorResponse != null) {
            return errorResponse;
        }

        // 빈값이면 에러 발생 가능성 존재


        Member member = memberRepository.findByLoginId((dto.getLoginId()));

        List<MemberBusinessRegion> memberBusinessRegionList = memberBusinessRegionRepository.findByMemId(member.getId());

        // 관심지역이 없으면 추가하고 있는거면 업데이트하기
        if (!memberBusinessRegionList.isEmpty()) {
            memberBusinessRegionRepository.deleteAll(memberBusinessRegionList);
        }

        Long companyFileId = null;

        if (!dto.getCompanyFile().equals("null")) {
            companyFileId = Long.parseLong(dto.getCompanyFile());
        }

        member.setProfileImg(dto.getProfileImg());
        member.setPhoneNo(dto.getPhoneNumber());
        member.setBank(dto.getBank());
        member.setAccountNo(dto.getAccountNo());
        member.setBusinessNo(dto.getBusinessNumber());
        member.setCompany(dto.getCompany());
        member.setCeo(dto.getCeo());
        member.setCompanyFile(companyFileId);
        member.setSubPlate(dto.getPlate());
        member.setSubSteel(dto.getRebar());
        member.setSubSectionsSteel(dto.getSection());
        member.setSubSteelPipe(dto.getPipe());

        try {
            memberRepository.save(member);
        } catch (Exception e) {
            return new JSONResponse<>(500, e.getMessage());
        }

        String region = dto.getRegion();
        List<String> regionList = Arrays.asList(region.split(","));

        for (String regionString : regionList) {
            Long regionId = Long.parseLong(regionString);
            MemberBusinessRegion memberBusinessRegion = MemberBusinessRegion.builder()
                    .memId(member.getId())
                    .deliveryRegionId(regionId)
                    .build();
            try {
                memberBusinessRegionRepository.save(memberBusinessRegion);
            } catch (Exception e) {
                return new JSONResponse<>(500, e.getMessage());
            }
        }

        return new JSONResponse<>(200, "SUCCESS");

    }

    /**
     * 토큰 db에 저장하기
     *
     * @param map: 토큰 :토큰값
     */
    public JSONResponse<?> saveToken(Map<String, Object> map) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberRepository.findByLoginIdAndSttsCd(email, MemberStatus.NORMAL); // 이메일로 맴버 객체 가져옴
        String token = map.get("token").toString();

        member.setPushToken(token);

        try {
            memberRepository.save(member);
        } catch (Exception e) {
            return new JSONResponse<>(500, "토큰 저장에 실패하였습니다.");
        }
        return new JSONResponse<>(200, "토큰 저장에 성공하였습니다.");
    }


    /**
     * 비밀번호 pattern 체크
     *
     * @param password 비밀번호
     * @return
     */
    private boolean isValidPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * 나의 정보 > 판매 중인 거래 > 견적 문의 > 거래 취소
     *
     * @param request 견적 세트 정보가 담긴 DTO
     * @param member
     */
    @Transactional
    public void checkAccountNo(EstimateSaveDto request, Member member) {
        EstimateSet estimateSet = estimateSetRepository.findById(request.getSetId()).orElseThrow(() -> new RuntimeException("견적서가 존재하지 않습니다."));

        // 구매확정
        if ("confirm".equals(request.getTitle())) {
            BidSet bidSet = bidSetRepository.findByEstimateSetIdAndMemId(request.getSetId(), member.getId()).orElseThrow(() -> new RuntimeException("입찰서가 존재하지 않습니다."));
            Member bidMember = memberRepository.findById(bidSet.getMemId()).orElseThrow(() -> new RuntimeException("회원 정보가 존재하지 않습니다."));

            if (bidMember.getAccountNo() == null)
                throw new IllegalArgumentException("거래 확정하시려면 마이페이지에서\n" + "계좌번호를 등록해주세요.");

            estimateSet.setSttsCd(EstimateSetStatus.COMPLETE);

            // 견적 히스토리 업데이트
            estimateSetHistoryRepository.save(EstimateSetHistory.builder()
                    .estimateSetId(estimateSet.getId())
                    .sttsCd(EstimateSetStatus.COMPLETE)
                    .build());
        } else {
            // 결제하기
            Member estimateMember = memberRepository.findById(estimateSet.getMemId()).orElseThrow(() -> new RuntimeException("회원 정보가 존재하지 않습니다."));

            if (estimateMember.getAccountNo() == null)
                throw new IllegalArgumentException("마이페이지에서\n" + "계좌번호를 등록해주세요.");

        }
    }

    /**
     * 회원 알림 설정 수정
     *
     * @param map 알림 설정 여부 정보
     */
    public void updateMemberNotification(Map<String, Object> map) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberRepository.findByLoginIdAndSttsCd(email, MemberStatus.NORMAL); // 이메일로 맴버 객체 가져옴

        member.setNightAlarmYn(YN.ofYn(map.get("nightAlarmYn").toString()));
        member.setPushYn(YN.ofYn(map.get("pushYn").toString()));

        try {
            memberRepository.save(member);
        } catch (Exception e) {
            throw new RuntimeException("회원 알림 설정 실패");
        }

        // 인증 정보 갱신
        authentication = new UsernamePasswordAuthenticationToken(member, member.getPassword(), member.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);


    }

    /**
     * 입력한 비밀번호와 저장된 비밀번호 일치 판정
     *
     * @return
     * @currentPassword 입력한 비밀번호
     */
    public JSONResponse<?> checkPassword(String currentPassword) {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberRepository.findByLoginIdAndSttsCd(email, MemberStatus.NORMAL);

        if (!passwordEncoder.matches(currentPassword, member.getPwd())) {
            return new JSONResponse<>(400, "입력한 현재 비밀번호가 틀립니다.");
        }

        return null;
    }

    public JSONResponse<?> validatePassword(String newPassword, String passwordCheck) {

        if (!hasText(newPassword)) {
            return new JSONResponse<>(400, "비밀번호를 입력해주세요.");
        }
        if (!hasText(passwordCheck)) {
            return new JSONResponse<>(400, "비밀번호 확인을 입력해주세요.");
        }
        if (!isValidPassword(newPassword)) {
            return new JSONResponse<>(400, "비밀번호는 8~12자리로 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.");
        }
        if (!newPassword.equals(passwordCheck)) {
            return new JSONResponse<>(400, "비밀번호와 비밀번호 확인이 다릅니다.");
        }
        return null;
    }

    public JSONResponse<?> updatePassword(String newPassword) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberRepository.findByLoginIdAndSttsCd(email, MemberStatus.NORMAL);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        member.setPwd(passwordEncoder.encode(newPassword));

        try {
            memberRepository.save(member);
        } catch (Exception e) {
            return new JSONResponse<>(500, "비밀번호 변경 실패했습니다");
        }

        return new JSONResponse<>(200, "비밀번호 변경에 성공했습니다");
    }

}
