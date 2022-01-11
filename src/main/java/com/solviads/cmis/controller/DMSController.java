package com.solviads.cmis.controller;

import com.solviads.cmis.business.CmisService;
import com.solviads.cmis.constants.MIMETypes;
import com.solviads.cmis.dto.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/document")
    public ResponseEntity<DocumentDto> createDocument(@RequestParam("file") MultipartFile multipartFile, @RequestParam("objectId") String objectId){
        if(multipartFile!=null && multipartFile.getSize()>0) {
            return switch (Objects.requireNonNull(multipartFile.getContentType(),"Content-type must not be null.")) {
                case MIMETypes.TEXT -> new ResponseEntity<>(new DocumentDto(cmisService.createDocumentText(multipartFile, objectId)), HttpStatus.OK);
                case MIMETypes.IMAGE_JPEG -> new ResponseEntity<>(new DocumentDto(cmisService.createDocumentJPEG(multipartFile, objectId)), HttpStatus.OK);
                default -> ResponseEntity.badRequest().build();
            };
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
