package kr.co.steellink.user.domain.commoncode.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommonSearchDto {

    private String itemCd; // 품목코드
    private String itemDetailCd; // 상세품목 코드
    private String purpose; // 상세품목 코드
    private String standard; // 규격
    private String model; // 모델

}
