package com.solviads.cmis.business;

import com.solviads.cmis.constants.MIMETypes;
import com.solviads.cmis.dto.CmisObjectDto;
import com.solviads.cmis.dto.*;
import com.solviads.cmis.factory.CMISManager;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
    public FolderDto getFolderByObjectId(String objectId) {
        ObjectId id = new ObjectIdImpl(objectId);
        if(session.getObject(id).getPropertyValue(PropertyIds.OBJECT_TYPE_ID).equals(FOLDER)) {
            return new FolderDto((Folder) session.getObject(id));
        }
        return null;
    }

    @Override
    public DocumentDto getDocumentByObjectId(String objectId) {
        ObjectId id = new ObjectIdImpl(objectId);
        if(session.getObject(id).getPropertyValue(PropertyIds.OBJECT_TYPE_ID).equals(DOCUMENT)) {
            return new DocumentDto((Document) session.getObject(id));
        }
        return null;
    }

    @Override
    public String getDocumentContentByObjectId(String objectId) {
        Document document = (Document) session.getObject(new ObjectIdImpl(objectId));
        if(document != null) {
            InputStream inputStream = document.getContentStream().getStream();
            return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
        }
        return null;
    }

    @Override
    public void deleteObjectByObjectId(String objectId) { //this method deletes folders and documents using their objectId
        ObjectId id = new ObjectIdImpl(objectId);
        session.delete(id);
    }

    @Override
    public Folder createFolder(String folderName, Folder hostFolder) { //this method creates a new folder in a specified folder
        Map<String, String> properties = new HashMap<>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, FOLDER);
        properties.put(PropertyIds.NAME, folderName);
        return hostFolder.createFolder(properties);
    }

    @Override
    public Document createDocumentText(MultipartFile multipartFile, String objectId) { //this method creates a text document in a specified folder
        Folder hostFolder = (Folder) session.getObject(new ObjectIdImpl(objectId));
        if(hostFolder != null) {
            Map<String, String> properties = new HashMap<>();
            properties.put(PropertyIds.OBJECT_TYPE_ID, DOCUMENT);
            properties.put(PropertyIds.NAME, multipartFile.getName());
            try {
                byte[] buf = multipartFile.getBytes();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf);
                ContentStream contentStream = session.getObjectFactory()
                        .createContentStream(multipartFile.getName(), buf.length, MIMETypes.TEXT, byteArrayInputStream);
                return hostFolder.createDocument(properties, contentStream, VersioningState.MAJOR);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Document createDocumentJPEG(MultipartFile multipartFile, String objectId) {
        Folder hostFolder = (Folder) session.getObject(new ObjectIdImpl(objectId));
        if(hostFolder!=null){
            Map<String, String> properties = new HashMap<>();
            properties.put(PropertyIds.OBJECT_TYPE_ID, DOCUMENT);
            properties.put(PropertyIds.NAME, multipartFile.getOriginalFilename());
            try{
                byte[] buf = multipartFile.getBytes();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf);
                ContentStream contentStream = session.getObjectFactory()
                        .createContentStream(multipartFile.getName(), buf.length, MIMETypes.IMAGE_JPEG, byteArrayInputStream);
                return hostFolder.createDocument(properties, contentStream, VersioningState.MAJOR);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public List<CmisObjectDto> getAllCmisObjects() {
        List<CmisObjectDto> cmisObjects = new ArrayList<>();
        for (CmisObject nextObject : session.getRootFolder().getChildren()) {
            String propertyValue = nextObject.getPropertyValue(PropertyIds.OBJECT_TYPE_ID);
            if (DOCUMENT.equals(propertyValue)) {
                cmisObjects.add(new DocumentDto((Document) nextObject));
            } else if (FOLDER.equals(propertyValue)) {
                //TODO: Child FOLDER'ların tüm child FOLDER'larını gezmemiz gerekiyor.
                //TODO: Tüm file tree'yi yazdırmalıyız. Şu an sadece root folder yazdırıyoruz.
                cmisObjects.add(new FolderDto((Folder) nextObject));
            }
        }
        return cmisObjects;
    }

    //dto verilecek. repolar içinde geçerli dto verme işlemi.
    @Override
    public CmisObject getCmisObjectByObjectId(String objectId) {
        return session.getObject(new ObjectIdImpl(objectId));
    }

    @Override
    public ObjectId updateDocumentContent(String objectId, String content) {
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
    public String readDocumentByObjectId(String objectId) {
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
