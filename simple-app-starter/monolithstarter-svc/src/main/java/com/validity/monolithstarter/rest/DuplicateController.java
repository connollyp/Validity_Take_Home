package com.validity.monolithstarter.rest;

import com.validity.monolithstarter.service.DuplicateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

@RestController
@RequestMapping("/api2")
public class DuplicateController {

    @Inject
    private DuplicateService duplicateservice;

    @GetMapping("/duplicate")
    public JSONObject getDuplicates() {
        return DuplicateService.createJsonWithOnlyDuplicates("/Validity_Take_Home/simple-app-starter/test-files/normal.csv");
    }

    @GetMapping("/nonDuplicate")
    public JSONObject getNonDuplicates{
        return DuplicateService.createJsonWithoutDuplicates("/Validity_Take_Home/simple-app-starter/test-files/normal.csv");
    }
}