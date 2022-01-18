package com.solviads.cmis.business;

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
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class CmisServiceImp implements CmisService{

    private static final String DOCUMENT = "cmis:document";
    private static final String FOLDER = "cmis:folder";
    private static final String SAP_REPOSITORY_ID = "476931f0-daee-4867-8834-9e7130c5eb41";

    private static final Logger logger = Logger.getLogger(CmisServiceImp.class.getName());

    private final Map<String, Session> sessionPool = new HashMap<>();
    private final CMISManager cmisManager;
    private Session session;
    private QueryStatement queryStatement; //query can be used later.

    @Autowired
    public CmisServiceImp(CMISManager cmisManager) {
        this.cmisManager = cmisManager;
        getSessionPool();
    }

    @Override
    public void getSessionPool() {
        sessionPool.clear();
        cmisManager.getRepositories().forEach(repository -> sessionPool.put(repository.getId(), cmisManager.openSession(repository.getId())));
        session = sessionPool.get(SAP_REPOSITORY_ID);
        session.getDefaultContext().setCacheEnabled(true);
    }

    private void checkTokenForNewSessionPool(){
        if(cmisManager.checkTokenExpiration()){
            getSessionPool();
        }
    }

    @Override
    public FolderDto getFolderByObjectId(String objectId) {
        checkTokenForNewSessionPool();
        ObjectId id = new ObjectIdImpl(objectId);
        if(session.getObject(id).getPropertyValue(PropertyIds.OBJECT_TYPE_ID).equals(FOLDER)) {
            return new FolderDto((Folder) session.getObject(id));
        }
        return null;
    }

    @Override
    public DocumentDto getDocumentByObjectId(String objectId) {
        checkTokenForNewSessionPool();
        ObjectId id = new ObjectIdImpl(objectId);
        if(session.getObject(id).getPropertyValue(PropertyIds.OBJECT_TYPE_ID).equals(DOCUMENT)) {
            return new DocumentDto((Document) session.getObject(id));
        }
        return null;
    }

    @Override
    public ReturnFileDto getDocumentContent(String objectId) {
        checkTokenForNewSessionPool();
        try {
            Document document = (Document) session.getObject(new ObjectIdImpl(objectId));
            ContentStream contentStream = document.getContentStream();
            return new ReturnFileDto(contentStream.getMimeType(), contentStream.getStream().readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Document createDocument(MultipartFile multipartFile, String hostFolderId) {
        checkTokenForNewSessionPool();
        Folder hostFolder = (Folder) session.getObject(new ObjectIdImpl(hostFolderId));
        if(hostFolder != null) {
            String originalFilename = multipartFile.getOriginalFilename();
            try{
                Objects.requireNonNull(originalFilename,"Filename must not be null.");
            } catch (NullPointerException e){
                return null;
            }
            Map<String, String> properties = new HashMap<>();
            properties.put(PropertyIds.OBJECT_TYPE_ID, DOCUMENT);
            properties.put(PropertyIds.NAME, originalFilename.substring(originalFilename.lastIndexOf("/")+1));
            try {
                byte[] buf = multipartFile.getBytes();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf);
                String mimeType = Objects.requireNonNull(multipartFile.getContentType(), "Content-type must not be null.");
                ContentStream contentStream = session.getObjectFactory().createContentStream(multipartFile.getName(), buf.length, mimeType, byteArrayInputStream);
                return hostFolder.createDocument(properties, contentStream, VersioningState.MAJOR);
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String getDocumentContentByObjectId(String objectId) {
        checkTokenForNewSessionPool();
        Document document = (Document) session.getObject(new ObjectIdImpl(objectId));
        if(document != null) {
            InputStream inputStream = document.getContentStream().getStream();
            return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
        }
        return null;
    }

    @Override
    public Boolean deleteObjectByObjectId(String objectId) {
        checkTokenForNewSessionPool();
        if(session.exists(objectId)){
            session.delete(new ObjectIdImpl(objectId));
            return true;
        }
        return false;
    }

    @Override
    public Folder createFolder(String folderName, String hostFolderId) { //this method creates a new folder in a specified folder
        checkTokenForNewSessionPool();
        Folder hostFolder = (Folder) session.getObject(new ObjectIdImpl(hostFolderId));
        Map<String, String> properties = new HashMap<>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, FOLDER);
        properties.put(PropertyIds.NAME, folderName);
        return hostFolder.createFolder(properties);
    }

    @Override
    public List<CmisObjectDto> getAllCmisObjects() {
        checkTokenForNewSessionPool();
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

    @Override
    public ObjectId updateDocumentContent(String objectId, String content) {
        checkTokenForNewSessionPool();
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
    public CmisObject updateDocumentName(String objectId, String newName) {
        checkTokenForNewSessionPool();
        if(session.exists(objectId)){
            Map<String, String> properties = new HashMap<>();
            CmisObject object = session.getObject(objectId);
            object.getProperties().forEach(property -> properties.put(property.getId(),property.getValueAsString()));
            properties.replace(PropertyIds.NAME,newName);
            return object.updateProperties(properties);
        }
        return null;
    }

}
