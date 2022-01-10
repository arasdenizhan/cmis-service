package com.solviads.cmis.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.chemistry.opencmis.client.api.Document;

import java.util.List;

@Getter
@Setter
public class DocumentDto extends CmisObjectDto {


    private List<String> paths;
    private static final String TYPE = "cmis:document";


    public DocumentDto(Document document) {
        super(document.getId(), document.getName());
        this.paths = document.getPaths();
    }
}
