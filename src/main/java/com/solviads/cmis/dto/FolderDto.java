package com.solviads.cmis.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.chemistry.opencmis.client.api.Folder;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class FolderDto extends CmisObjectDto {

    private String path;
    private static final String TYPE = "cmis:folder";
    private final Map<String, CmisObjectDto> subObjects = new LinkedHashMap<>();


    public FolderDto(Folder folder) {
        super(folder.getId(), folder.getName());
        path = folder.getPath();
        folder.getChildren().forEach(cmisObject -> subObjects.put(cmisObject.getId(), new CmisObjectDto(cmisObject)));
    }
}
