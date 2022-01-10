package com.solviads.cmis.controller;

import com.solviads.cmis.business.CmisService;
import com.solviads.cmis.constants.MIMETypes;
import com.solviads.cmis.dto.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/dms")
public final class DMSController {

    private final CmisService cmisService;

    @Autowired
    public DMSController(CmisService cmisService) {
        this.cmisService = cmisService;
    }

    public ResponseEntity<DocumentDto> createDocument(@RequestParam MultipartFile multipartFile, @RequestParam("objectId") String objectId){
        if(multipartFile!=null) { //TODO: multipart null da olmamalı, false de olmamalı.
            //TODO: MIMETypes'lara göre service fonksiyonlarına gönder.
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

    @GetMapping("/document")
    public ResponseEntity<DocumentDto> getDocumentById(@RequestParam("objectId") String objectId){
        if(!Strings.isEmpty(objectId)){
            return new ResponseEntity<>(cmisService.getDocumentByObjectId(objectId), HttpStatus.OK);
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
