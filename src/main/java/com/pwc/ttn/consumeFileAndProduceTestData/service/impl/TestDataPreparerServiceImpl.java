package com.pwc.ttn.consumeFileAndProduceTestData.service.impl;

import com.pwc.ttn.consumeFileAndProduceTestData.config.AppConfig;
import com.pwc.ttn.consumeFileAndProduceTestData.service.FileReaderWriterService;
import com.pwc.ttn.consumeFileAndProduceTestData.service.TestDataPreparerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.pwc.ttn.consumeFileAndProduceTestData.utils.ApplicationUtils.getFileExtension;
@Service
public class TestDataPreparerServiceImpl implements TestDataPreparerService {
    Logger logger= LoggerFactory.getLogger(TestDataPreparerService.class);
    @Autowired
    FileReaderWriterService fileReaderWriterService;
    @Autowired
    AppConfig appConfig;
    @Override
    public ResponseEntity<String> prepareTestData(MultipartFile file) {
        boolean isFileValid=isValidFile(file);
        if(isFileValid)
        {
            LocalDateTime startTime=LocalDateTime.now();
            LocalDateTime endTime=null;
            boolean result=false;
            String fileExtension=getFileExtension(file.getOriginalFilename());
            if("csv".equals(fileExtension))
            {
                 result=fileReaderWriterService.copyCsvFileContents(file);
                 endTime=LocalDateTime.now();
            }
            else if("xls".equals(fileExtension)||"xlsx".equals(fileExtension))
            {
                fileReaderWriterService.readExcelFile(file);
//                result=fileReaderWriterService.copyExcelFileContents(file);
                result=fileReaderWriterService.readAndCopyExcel(file);
                endTime=LocalDateTime.now();
            }
            if(result)
            {
                logger.info("Created test data for {} successfully ",file.getOriginalFilename());
                return new ResponseEntity<>("Test data created successfully\nStartTime"+startTime+"\nEndTime"+endTime, HttpStatus.OK);
            }
            else
            {
                return new ResponseEntity<>("Some internal error has occurred",HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>("Could not fulfil request because of invalid file",HttpStatus.BAD_REQUEST);
    }

    //utility method
    private boolean isValidFile(MultipartFile file)
    {
        if (!appConfig.getSupportedFileFormats()
                .contains(getFileExtension(file.getOriginalFilename()))) {
            logger.info("File validattion for file {} failed because of invalid format",file.getOriginalFilename());
            return false;
        }

        if (file.getSize() > appConfig.getMaxFileSize()) {
            logger.info("File validation for file {} failed because it exceeds maximum size",file.getOriginalFilename());
            return false;
        }

        if (file.getName().contains("..")) {
            logger.info("File validation for file {} failed because of invalid filename",file.getOriginalFilename());
            return false;
        }

        return true;
    }
}
