package com.pwc.ttn.consumeFileAndProduceTestData.controllers;

import com.pwc.ttn.consumeFileAndProduceTestData.service.TestDataPreparerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class TestDataPreparationController {
    @Autowired
    TestDataPreparerService testDataPreparerService;

    @PostMapping("/prepareTestData")
    @ResponseBody
    ResponseEntity<String> prepareTestData(@RequestParam MultipartFile inputFile)
    {
        return testDataPreparerService.prepareTestData(inputFile);
    }
}
