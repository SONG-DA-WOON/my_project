package kr.co.steellink.user.domain.commoncode;

import kr.co.steellink.user.domain.commoncode.dto.BankInfoDto;

import java.util.List;

public interface CommonRepositoryCustom {

    List<BankInfoDto> searchBankInfo();

}
