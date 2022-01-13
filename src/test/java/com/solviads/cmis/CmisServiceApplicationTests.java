package com.solviads.cmis;

import com.solviads.cmis.business.CmisService;
import com.solviads.cmis.business.CmisServiceImp;
import com.solviads.cmis.factory.CMISManager;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.objenesis.SpringObjenesis;

import javax.print.Doc;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SpringBootTest
class CmisServiceApplicationTests {

    @Autowired
    CMISManager cmisManager;
    @Autowired
    CmisServiceImp cmisService;

    private final Logger logger = Logger.getLogger(CmisServiceApplicationTests.class.getName());

    @Test
    void shouldGetRepositories_successfully() {
        List<Repository> repositories = cmisManager.getRepositories();
        Assertions.assertThat(repositories.size()).isEqualTo(1);
        Repository repository = repositories.get(0);
        logger.info("ID: " + repository.createSession().getRootFolder().getId());
        logger.info(String.format("repository name: %s\nproduct name: %s\nvendor name: %s", repository.getName(), repository.getProductName(), repository.getVendorName()));
    }

    void testFunc(){
    }
//
//    @Test
//    void shouldGetObjects_successfully() {
//        Repository repository = cmisManager.getRepositories().get(0);
//        Session session = repository.createSession();
//        session.getRootFolder().getFolderTree(1);
//
//    }
//
//    @Test
//    void shouldGetObjectByObjectId_successfully() {
//        Repository repository = cmisManager.getRepositories().get(0);
//        Session session = repository.createSession();
//        session.getObject("you must to replace objectId instead of this string");
//    }
//
//    @Test
//    void shouldDeleteObjectByObjectId_successfully() {
//        Repository repository = cmisManager.getRepositories().get(0);
//        Session session = repository.createSession();
//        CmisObject object = session.getObject("zNUPDOvP4L6TSYC6GY9K2wOXZJU73CDHsZ_LUsazx4o");
//        object.delete(true);
//    }
//
//    @Test
//    void shouldUpdateObjectByObjectId_successfully() {
//        Repository repository = cmisManager.getRepositories().get(0);
//        Session session = repository.createSession();
//        Map<String, String> properties = new HashMap<>();
//        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
//        properties.put(PropertyIds.NAME, "Re-updated deneme 3.txt");
//        //Assertions.assertThat(cmisService.updateDocumentByObjectId(session.createObjectId("v5vPF5rSsz5oxdSqeaibZC1vXHjSjMED2-9__JMiLd8"), properties).getName()).isEqualTo("Re-updated deneme 3.txt");
//    }
//
//    @Test
//    void shouldUpdateDocumentContent_successfully() {
//        ObjectId objectId = new ObjectIdImpl("EwsyW4rOvdPjk1KpOEDDxldeyKm2STF0PEEGf29gm7o");
//        String content = "this content is written by some seniors";
//        ObjectId updated = cmisService.updateDocumentContent(objectId, content);
//        System.out.println(updated.getId());
//        String updatedContent = cmisService.getDocumentContentByObjectId(updated);
//        Assertions.assertThat(updatedContent).isEqualTo(content);
//        logger.info("updated content of document is: " + updatedContent);
//    }
//
//    @Test
//    void LogAllCmisObjectsTest() {
//        Repository repository = cmisManager.getRepositories().get(0);
//        Session session = repository.createSession();
//        List<Tree<FileableCmisObject>> folderTrees = session.getRootFolder().getFolderTree(3);
//        folderTrees.forEach(fileableCmisObjectTree -> logger.info(fileableCmisObjectTree.getItem().getName() + " * " + fileableCmisObjectTree.getItem().getId()));
//
//        Folder folder = (Folder) session.getObject(string2);
//        for(CmisObject cmisObject : folder.getChildren()) {
//            logger.info(cmisObject.getName() + " - " + cmisObject.getId());
//        }
//
//    }
//
//    String string1 = "EwsyW4rOvdPjk1KpOEDDxldeyKm2STF0PEEGf29gm7o";
//    String string2 = "mpI4fcGCkNUTHquJPqtj9atBlB0tvoVhkIc9YeygoT4";
//
//    @Test
//    void readDocumentTest() {
//        ObjectId objectId = new ObjectIdImpl("v5vPF5rSsz5oxdSqeaibZC1vXHjSjMED2-9__JMiLd8");
//        String documentContent = cmisService.getDocumentContentByObjectId(objectId);
//        logger.info(String.format("the content of the document is: %s", documentContent));
//    }
//
//    @Test
//    void getFolderByObjectId() {
//        ObjectId docObjectId = new ObjectIdImpl("v5vPF5rSsz5oxdSqeaibZC1vXHjSjMED2-9__JMiLd8");
//        ObjectId folderObjectId = new ObjectIdImpl("mpI4fcGCkNUTHquJPqtj9atBlB0tvoVhkIc9YeygoT4");
//        Folder folder = cmisService.getFolderByObjectId(folderObjectId);
//        logger.info(folder.getName() + " * " + folder.getId());
//    }
//
//    @Test
//    void createFolderTest() {
//        ObjectId hostFolderObjectId = new ObjectIdImpl("mpI4fcGCkNUTHquJPqtj9atBlB0tvoVhkIc9YeygoT4"); //the host folder which will contain the new folder must be given
//        Map<String, String> properties = new HashMap<>();
//        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
//        properties.put(PropertyIds.NAME, "new folder");
//
//        try {
//            Folder hostFolder = cmisService.getFolderByObjectId(hostFolderObjectId);
//            hostFolder.createFolder(properties);
//        }catch (ClassCastException classCastException) {
//            logger.info(String.format("this id [%s] does not belong to a folder", hostFolderObjectId.getId()));
//        }
//    }
//
//    @Test
//    void createDocumentTest() {
//        ObjectId hostFolderObjectId = new ObjectIdImpl("mpI4fcGCkNUTHquJPqtj9atBlB0tvoVhkIc9YeygoT4"); //the host folder which will contain the new folder must be given
//        Folder folder = cmisService.getFolderByObjectId(hostFolderObjectId);
//        Document document = cmisService.createDocument("newest document", "solvia digital solutions", folder);
//        Assertions.assertThat(document.getName()).isEqualTo("newest document");
//    }
//
//    @Test
//    void getAllFoldersAndDocs() {
//        cmisService.getAllCmisObjects().forEach(cmisObject -> logger.info(cmisObject.getName() + " * " + cmisObject.getId()));
//    }
}
