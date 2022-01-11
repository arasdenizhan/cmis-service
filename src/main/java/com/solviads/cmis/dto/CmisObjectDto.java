package com.solviads.cmis.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.chemistry.opencmis.client.api.CmisObject;

@Getter
@Setter
public class CmisObjectDto {
    private String id;
    private String name;

    public CmisObjectDto(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public CmisObjectDto(CmisObject cmisObject){
        this.id = cmisObject.getId();
        this.name = cmisObject.getName();
    }
}
