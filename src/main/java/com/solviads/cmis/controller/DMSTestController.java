package com.solviads.cmis.controller;

import com.solviads.cmis.business.CmisService;
import com.solviads.cmis.dto.CmisObjectDto;
import com.solviads.cmis.dto.DocumentDto;
import com.solviads.cmis.dto.FolderDto;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test/dms")
public class DMSTestController {

    private final CmisService cmisService;

    @Autowired
    public DMSTestController(CmisService cmisService) {
        this.cmisService = cmisService;
    }

    @GetMapping("/getAllFoldersAndDocs")
    public ResponseEntity<List<CmisObjectDto>> getAll(){
        List<CmisObjectDto> allCmisObjects = cmisService.testGetAllObjects();
        if(!allCmisObjects.isEmpty()){
            return new ResponseEntity<>(allCmisObjects, HttpStatus.OK);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/folder")
    public ResponseEntity<FolderDto> getFolderById(@RequestParam("objectId") String objectId){
        if(!Strings.isEmpty(objectId)){
            return new ResponseEntity<>(cmisService.testGetFolderByObjectId(objectId), HttpStatus.OK);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping(value = "/document")
    public ResponseEntity<DocumentDto> getDocumentById(@RequestParam("objectId") String objectId){
        if(!Strings.isEmpty(objectId)){
            return new ResponseEntity<>(cmisService.testGetDocumentByObjectId(objectId), HttpStatus.OK);
        }
        return ResponseEntity.badRequest().build();
    }
}
