package com.dmc.common;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter @Setter
public abstract class PageDto {
    @Parameter(description = "페이지 번호")
    protected int page = 1;

    @Parameter(description = "페이지 크기")
    protected int size = 10;

    public Pageable pageable() {
        return PageRequest.of(this.page - 1, this.size);
    }
}
