package com.project.domain.memberbusinessregion;

import kr.co.steellink.user.domain.memberbusinessregion.dto.MemberBusinessRegionListDtoForMemUpdate;

import java.util.List;

public interface MemberBusinessRegionRepositoryCustom {

    List<MemberBusinessRegionListDtoForMemUpdate> searchMemberBizRegion(Long memId);
}
