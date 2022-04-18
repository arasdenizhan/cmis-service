package com.solviads.cmis.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.chemistry.opencmis.client.api.CmisObject;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class CmisObjectDto {
    private String id;
    private String name;
    private String lastEditDate;
    private String lastModifiedBy;

    public CmisObjectDto(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public CmisObjectDto(CmisObject cmisObject){
        this.id = cmisObject.getId();
        this.name = cmisObject.getName();
        this.lastEditDate = cmisObject.getLastModificationDate().toZonedDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss"));
        this.lastModifiedBy = cmisObject.getLastModifiedBy();
    }
}
