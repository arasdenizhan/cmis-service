package com.solviads.cmis.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.chemistry.opencmis.client.api.Folder;

@Getter
@Setter
public class FolderDto extends CmisObjectDto {

    private String path;
    private static final String TYPE = "cmis:folder";


    public FolderDto(Folder folder) {
        super(folder.getId(), folder.getName());
        path = folder.getPath();
    }
}
