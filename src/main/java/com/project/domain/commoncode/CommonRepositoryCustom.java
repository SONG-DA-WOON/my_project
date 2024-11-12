package com.project.domain.commoncode;


import com.project.domain.commoncode.dto.BankInfoDto;

import java.util.List;

public interface CommonRepositoryCustom {

    List<BankInfoDto> searchBankInfo();

}
