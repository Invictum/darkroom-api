package com.github.darkroom.cases.controller;

import com.github.darkroom.cases.CaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

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

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CaseResponse> showSeveralCases(@PathVariable String collection, @RequestParam("classifier") Optional<String> classifier) {
        return classifier
                .map(name -> caseService.loadSeries(collection, name))
                .orElseGet(() -> caseService.loadRecent(collection));
    }
}
