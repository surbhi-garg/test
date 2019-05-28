package com.pwc.ttn.consumeFileAndProduceTestData.service.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.pwc.ttn.consumeFileAndProduceTestData.config.AppConfig;
import com.pwc.ttn.consumeFileAndProduceTestData.service.FileReaderWriterService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import static com.pwc.ttn.consumeFileAndProduceTestData.utils.ApplicationUtils.getFileExtension;
import static java.util.Objects.isNull;

@Service
public class FileReaderWriterServiceImpl implements FileReaderWriterService {
    @Autowired
    AppConfig appConfig;
    Logger logger = LoggerFactory.getLogger(FileReaderWriterService.class);

    @Override
    public List<String[]> readCsvFile(MultipartFile multipartFile) {
        try (
                Reader reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()));
                CSVReader csvReader = new CSVReader(reader)
        ) {
            List<String[]> dataRows = csvReader.readAll();
            return dataRows;
        } catch (IOException e) {
            logger.error("IoException while reading file {} and exception is {}", multipartFile.getOriginalFilename(), e.toString());
            e.printStackTrace();
        }
        return new ArrayList<String[]>();
    }

    @Override
    public Boolean copyCsvFileContents(MultipartFile multipartFile) {
        String suffixData = "_test_" + LocalDateTime.now();
        File file = new File(appConfig.getTestDataDir() + "/" + multipartFile.getOriginalFilename() + suffixData + "." + getFileExtension(multipartFile.getOriginalFilename()));
        try {
            file.createNewFile();
            List<String[]> dataRows = readCsvFile(multipartFile);
            CSVWriter csvWriter = new CSVWriter(new FileWriter(file.getAbsolutePath()));
            for (int rowIndex = 0; rowIndex < dataRows.size(); rowIndex++) {
                if (rowIndex == 0)
                    csvWriter.writeNext(dataRows.get(0));
                else {
                    for (int j = 0; j < 2000; j++) {
                        csvWriter.writeNext(dataRows.get(rowIndex));
                    }
                }
            }
            csvWriter.close();
            return true;

        } catch (IOException e) {
            logger.error("Exception occurred while creating new file");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean readAndCopyExcel(MultipartFile multipartFile) {
        String suffixData = "_test_" + LocalDateTime.now();
        File file = new File(appConfig.getTestDataDir() + "/" + multipartFile.getOriginalFilename() + suffixData + "." + getFileExtension(multipartFile.getOriginalFilename()));
        DataFormatter dataFormatter = new DataFormatter();
        try {
            int rowCount = 0, colCount = 0;
            Workbook workbook = WorkbookFactory.create(convert(multipartFile));
            workbook.setMissingCellPolicy(Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            Workbook copyBook = new SXSSFWorkbook(100000);
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet copyBookSheet = copyBook.createSheet();
                Row header = workbook.getSheetAt(sheetIndex).getRow(0);
                for (int rowIndex = 0; rowIndex <= workbook.getSheetAt(sheetIndex).getLastRowNum(); rowIndex++) {
                    List<String> rowData = new ArrayList<>();
                    for (int colNum = 0; colNum < header.getPhysicalNumberOfCells(); colNum++) {
                        Cell cell = workbook
                                .getSheetAt(sheetIndex)
                                .getRow(rowIndex)
                                .getCell(colNum, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        String cellValue = "";
                        if (!isNull(cell)) {
                            cellValue = dataFormatter.formatCellValue(cell);
                        }
                        rowData.add(cellValue);
                    }
                    if (rowIndex == 0) {
                        Row copyBookRow = copyBookSheet.createRow(rowCount++);
                        for (int i = 0; i < rowData.size(); i++) {
                            Cell cell = copyBookRow.createCell(colCount++);
                            cell.setCellValue(rowData.get(i));
                        }
                    } else {
                        for (int times = 0; times < 2000; times++) {
                            Row copyBookRow = copyBookSheet.createRow(rowCount++);
                            for (int i = 0; i < rowData.size(); i++) {
                                Cell cell = copyBookRow.createCell(colCount++);
                                cell.setCellValue(rowData.get(i));
                            }
                            colCount = 0;
                        }
                    }
                }
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            copyBook.write(fileOutputStream);
            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Map<String, List<List<String>>> readExcelFile(MultipartFile multipartFile) {
        DataFormatter dataFormatter = new DataFormatter();
        Map<String, List<List<String>>> readData = new HashMap<>();
        try {
            Workbook workbook = WorkbookFactory.create(convert(multipartFile));
            workbook.setMissingCellPolicy(Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            workbook.forEach(sheet -> {
                List<List<String>> sheetData = new ArrayList<>();
                Row header = sheet.getRow(0);
                for (int rowIndex = 0; rowIndex < sheet.getLastRowNum(); rowIndex++) {
                    List<String> rowData = new ArrayList<>();
                    for (int colNum = 0; colNum < header.getPhysicalNumberOfCells(); colNum++) {
                        Cell cell = sheet.getRow(rowIndex).getCell(colNum, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        String cellValue = "";
                        if (!isNull(cell)) {
                            cellValue = dataFormatter.formatCellValue(cell);
                        }
                        rowData.add(cellValue);
                    }
                    if (rowIndex == 0)
                        sheetData.add(rowData);
                    else {
                        for (int times = 0; times < 20000; times++) {
                            sheetData.add(rowData);
                        }
                    }
                }
                ;
                readData.put(sheet.getSheetName(), sheetData);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return readData;
    }

    private static File convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    @Override
    public Boolean copyExcelFileContents(MultipartFile multipartFile) {
        String suffixData = "_test_" + LocalDateTime.now();
        File file = new File(appConfig.getTestDataDir() + "/" + multipartFile.getOriginalFilename() + suffixData + "." + getFileExtension(multipartFile.getOriginalFilename()));
        Workbook workbook = new XSSFWorkbook();
        Map<String, List<List<String>>> dataRead = readExcelFile(multipartFile);
        Set<String> sheetNames = dataRead.keySet();
        sheetNames.forEach(
                sheetName -> {
                    Sheet sheet = workbook.createSheet(sheetName);
                    List<List<String>> dataRows = dataRead.get(sheetName);
                    int rowCount = 0;
                    for (int rowIndex = 0; rowIndex < dataRows.size(); rowIndex++) {
                        int colCount = 0;
//                            if(rowIndex==0)
//                            {
//                                Row row = sheet.createRow(rowCount++);
//                                for (String cellData : dataRows.get(rowIndex)) {
//                                    Cell cell = row.createCell(colCount++);
//                                    cell.setCellValue(cellData);
//                                }
//
//                            }
//                            else
//                            {
//                                for(int j=0;j<5000;j++)
//                                {
                        Row row = sheet.createRow(rowCount++);
                        for (String cellData : dataRows.get(rowIndex)) {
                            Cell cell = row.createCell(colCount++);
                            cell.setCellValue(cellData);
                        }
//                                    colCount=0;
//                                }
                    }
//                        }
                }
        );
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            workbook.write(fileOutputStream);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }
}
