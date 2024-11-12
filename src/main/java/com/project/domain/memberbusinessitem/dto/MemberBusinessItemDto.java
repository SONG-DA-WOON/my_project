package com.project.domain.memberbusinessitem.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class MemberBusinessItemDto {
    private List<String> plate;
    private List<String> rebar;
    private List<String> section;
    private List<String> pipe;
}
