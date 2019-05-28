package com.pwc.ttn.consumeFileAndProduceTestData.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface TestDataPreparerService {
    ResponseEntity<String> prepareTestData(MultipartFile multipartFile);
}
