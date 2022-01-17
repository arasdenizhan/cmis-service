package com.solviads.cmis.controller;

import com.solviads.cmis.business.CmisService;
import com.solviads.cmis.dto.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.util.ArrayUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dms")
public final class DMSController {

    private final CmisService cmisService;

    @Autowired
    public DMSController(CmisService cmisService) {
        this.cmisService = cmisService;
    }

    @PostMapping("/document")
    public ResponseEntity<DocumentDto> createDocument(@RequestParam("file") MultipartFile multipartFile, @RequestParam("hostFolderId") String hostFolderId){
        if(multipartFile!=null && multipartFile.getSize()>0) {
            return new ResponseEntity<>(new DocumentDto(cmisService.createDocument(multipartFile,hostFolderId)),HttpStatus.OK);
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/folder")
    public ResponseEntity<FolderDto> createFolder(@RequestParam("folderName") String folderName, @RequestParam("hostFolderId") String hostFolderId){
        if(!folderName.isEmpty() && !hostFolderId.isEmpty()) {
            return new ResponseEntity<>(new FolderDto(cmisService.createFolder(folderName,hostFolderId)),HttpStatus.OK);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteObjectById(@RequestParam("objectId") String objectId){
        //TODO: multiFiled döküman tekrardan düşünülücek.
        if(!objectId.isEmpty() && Boolean.TRUE.equals(cmisService.deleteObjectByObjectId(objectId))){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/update-object")
    public ResponseEntity<CmisObjectDto> updateObjectById(@RequestParam("objectId") String objectId, @RequestParam("name") String name){
        if(!objectId.isEmpty()){
            return new ResponseEntity<>(new CmisObjectDto(cmisService.updateDocumentName(objectId,name)),HttpStatus.OK);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/folder")
    public ResponseEntity<FolderDto> getFolderById(@RequestParam("objectId") String objectId){
        if(!Strings.isEmpty(objectId)){
            return new ResponseEntity<>(cmisService.getFolderByObjectId(objectId), HttpStatus.OK);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping(value = "/document")
    public ResponseEntity<DocumentDto> getDocumentById(@RequestParam("objectId") String objectId){
        if(!Strings.isEmpty(objectId)){
            return new ResponseEntity<>(cmisService.getDocumentByObjectId(objectId), HttpStatus.OK);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping(value = "/document-content")
    public ResponseEntity<byte[]> getDocumentContentById(@RequestParam("objectId") String objectId){
        if(!Strings.isEmpty(objectId)){
            ReturnFileDto fileDto = cmisService.getDocumentContent(objectId);
            if(fileDto!=null){
                MultiValueMap<String,String> httpHeaders = new HttpHeaders();
                httpHeaders.put(HttpHeaders.CONTENT_TYPE, List.of(fileDto.getMimeType()));
                return new ResponseEntity<>(fileDto.getContent(),httpHeaders,HttpStatus.OK);
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/getAllFoldersAndDocs")
    public ResponseEntity<List<CmisObjectDto>> getAll(){
        List<CmisObjectDto> allCmisObjects = cmisService.getAllCmisObjects();
        if(!allCmisObjects.isEmpty()){
            return new ResponseEntity<>(allCmisObjects,HttpStatus.OK);
        }
        return ResponseEntity.badRequest().build();
    }

}
