package com.solviads.cmis.business;

import com.solviads.cmis.factory.CMISManager;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CmisServiceImp implements CmisService{

    private static final String DOCUMENT = "cmis:document";
    private static final String FOLDER = "cmis:folder";
    private static final String SAP_REPOSITORY_ID = "476931f0-daee-4867-8834-9e7130c5eb41"; //sorulacak.

    private final Map<String, Session> sessionPool = new HashMap<>();
    private final CMISManager cmisManager;
    private final Session session;
    private QueryStatement queryStatement;

    @Autowired
    public CmisServiceImp(CMISManager cmisManager) {
        this.cmisManager = cmisManager;
        cmisManager.getRepositories().forEach(repository -> sessionPool.put(repository.getId(), cmisManager.openSession(repository.getId())));
        session = sessionPool.get(SAP_REPOSITORY_ID);
        session.getDefaultContext().setCacheEnabled(true);
    }

    @Override
    public Folder getFolderByObjectId(ObjectId objectId) {
        if(session.getObject(objectId).getPropertyValue(PropertyIds.OBJECT_TYPE_ID).equals(FOLDER)) {
            return (Folder) session.getObject(objectId);
        }
        return null;
    }

    @Override
    public Document getDocumentByObjectId(ObjectId objectId) {
        if(session.getObject(objectId).getPropertyValue(PropertyIds.OBJECT_TYPE_ID).equals(DOCUMENT)) {
            return (Document) session.getObject(objectId);
        }
        return null;
    }

    @Override
    public String getDocumentContentByObjectId(ObjectId objectId) {
        Document document = this.getDocumentByObjectId(objectId);
        if(document != null) {
            InputStream inputStream = document.getContentStream().getStream();
            return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
        }
        return null;
    }

    @Override
    public void deleteObjectByObjectId(ObjectId objectId) { //this method deletes folders and documents using their objectId
        session.delete(objectId);
    }

    @Override
    public Folder createFolder(String folderName, Folder hostFolder) { //this method creates a new folder in a specified folder
        Map<String, String> properties = new HashMap<>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, FOLDER);
        properties.put(PropertyIds.NAME, folderName);
        return hostFolder.createFolder(properties);
    }

    @Override
    public Document createDocument(String documentName, String content, Folder hostFolder) { //this method creates a text document in a specified folder
        if(hostFolder != null) {
            Map<String, String> properties = new HashMap<>();
            properties.put(PropertyIds.OBJECT_TYPE_ID, DOCUMENT);
            properties.put(PropertyIds.NAME, documentName);
            byte[] buf = content.getBytes(StandardCharsets.UTF_8);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf);
            ContentStream contentStream = session.getObjectFactory().createContentStream(documentName, buf.length, "text/plain", byteArrayInputStream);
            return hostFolder.createDocument(properties, contentStream, VersioningState.MAJOR);
        }
        return null;
    }

    @Override
    public List<CmisObject> getAllCmisObjects() {
        List<CmisObject> cmisObjects = new ArrayList<>();
        session.getRootFolder().getChildren().iterator().forEachRemaining(cmisObjects::add);
        return cmisObjects;
    }

    //dto verilecek. repolar içinde geçerli dto verme işlemi.
    @Override
    public CmisObject getCmisObjectByObjectId(ObjectId objectId) {
        return session.getObject(objectId);
    }

    @Override
    public ObjectId updateDocumentContent(ObjectId objectId, String content) {
        Document document = (Document) session.getObject(objectId);
        if(document != null) {
            ObjectId newDocumentObjectId = document.checkOut(); //for updating the document's content, checkOut method will return the object id of the private working copy (pwc) of the document
            Document pwc = (Document) session.getObject(newDocumentObjectId);
            byte[] buf = content.getBytes(StandardCharsets.UTF_8);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf);
            ContentStream contentStream = session.getObjectFactory().createContentStream(document.getName(), buf.length, "text/plain" , byteArrayInputStream);
            pwc.setContentStream(contentStream, true);
            return pwc.checkIn(false, null, null, null);
        }
        return null;
    }

    @Override
    public String readDocumentByObjectId(ObjectId objectId) {
        if(session.getObject(objectId).getPropertyValue(PropertyIds.OBJECT_TYPE_ID).equals(DOCUMENT)) {
            System.out.println(String.format("the object with %s id is a document", objectId));
            Document doc = (Document) session.getObject(objectId);
            InputStream stream = doc.getContentStream().getStream();
            return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
        }
        System.out.println(String.format("the object with %s id is not a document", objectId));
        return null;
    }
}
