package com.validity.monolithstarter.rest;

import com.validity.monolithstarter.service.DuplicateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

@RestController
@RequestMapping("/api")
public class DuplicateController {

    @Inject
    private DuplicateService duplicateservice;

    @GetMapping("/duplicate")
    public String getDuplicates() {
        return duplicateservice.createJsonWithOnlyDuplicates("../../../../../../../test-files/normal.csv");
    }

    @GetMapping("/nonDuplicate")
    public String getNonDuplicates(){
        return duplicateservice.createJsonWithoutDuplicates("../../../../../../../test-files/normal.csv");
    }
}