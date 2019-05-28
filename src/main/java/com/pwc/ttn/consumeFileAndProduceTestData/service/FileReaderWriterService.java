package com.pwc.ttn.consumeFileAndProduceTestData.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileReaderWriterService {
    List<String[]> readCsvFile(MultipartFile multipartFile);
    Boolean copyCsvFileContents(MultipartFile multipartFile);
    Map<String, List<List<String>>> readExcelFile(MultipartFile multipartFile);
    Boolean copyExcelFileContents(MultipartFile multipartFile);
    public boolean readAndCopyExcel(MultipartFile multipartFile);
}
