package com.solviads.cmis.business;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;

import java.util.List;
import java.util.Map;

public interface CmisService {

    List<CmisObject> getAllCmisObjects();
    CmisObject getCmisObjectByObjectId(ObjectId objectId);
    Folder getFolderByObjectId(ObjectId objectId);
    Document getDocumentByObjectId(ObjectId objectId);
    Document createDocument(String documentName, String content, Folder hostFolder);
    String getDocumentContentByObjectId(ObjectId objectId);
    Folder createFolder(String folderName, Folder hostFolder);
    void deleteObjectByObjectId(ObjectId objectId);
    ObjectId updateDocumentContent(ObjectId objectId, String content);
    String readDocumentByObjectId(ObjectId objectId);
}
