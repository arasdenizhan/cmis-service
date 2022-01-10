package com.solviads.cmis.business;

import com.solviads.cmis.dto.*;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CmisService {

    List<CmisObjectDto> getAllCmisObjects();
    CmisObject getCmisObjectByObjectId(String objectId);
    FolderDto getFolderByObjectId(String objectId);
    DocumentDto getDocumentByObjectId(String objectId);
    Document createDocumentText(MultipartFile multipartFile, String objectId);
    String getDocumentContentByObjectId(String objectId);
    Folder createFolder(String folderName, Folder hostFolder);
    void deleteObjectByObjectId(String objectId);
    ObjectId updateDocumentContent(String objectId, String content);
    String readDocumentByObjectId(String objectId);
}
