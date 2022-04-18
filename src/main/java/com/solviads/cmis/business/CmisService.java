package com.solviads.cmis.business;

import com.solviads.cmis.dto.*;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CmisService {
    void getSessionPool();
    List<CmisObjectDto> getAllCmisObjects();
    FolderDto getFolderByObjectId(String objectId);
    DocumentDto getDocumentByObjectId(String objectId);
    ReturnFileDto getDocumentContent(String objectId);
    Document createDocument(MultipartFile multipartFile, String hostFolderId);
    String getDocumentContentByObjectId(String objectId);
    Folder createFolder(String folderName, String hostFolderId);
    Boolean deleteObjectByObjectId(String objectId);
    ObjectId updateDocumentContent(String objectId, String content);
    CmisObject updateDocumentName(String objectId, String newName);
    List<CmisObjectDto> testGetAllObjects();
    FolderDto testGetFolderByObjectId(String objectId);
    DocumentDto testGetDocumentByObjectId(String objectId);
}
