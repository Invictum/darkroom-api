package com.github.darkroom.cases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "{collection}/cases")
public class CaseController {

    private final CaseService caseService;

    @Autowired
    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> send(@PathVariable String collection, @RequestPart CaseMetadata meta, @RequestPart MultipartFile image) {
        caseService.saveCase(collection, meta, image);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
