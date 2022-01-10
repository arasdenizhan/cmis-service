package com.solviads.cmis.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmisObjectDto {
    private String id;
    private String name;

    public CmisObjectDto(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
