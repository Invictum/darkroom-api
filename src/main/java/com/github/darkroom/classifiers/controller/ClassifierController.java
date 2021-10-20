package com.github.darkroom.classifiers.controller;

import com.github.darkroom.cases.CaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "{collection}/classifiers")
public class ClassifierController {

    private final CaseService caseService;

    @Autowired
    public ClassifierController(CaseService caseService) {
        this.caseService = caseService;
    }

    @PostMapping(path = "{classifier}/base/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> setBase(@PathVariable String collection, @PathVariable String classifier, @PathVariable long id) {
        caseService.updateBase(collection, classifier, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
