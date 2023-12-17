package com.neo.weights.service;

import com.neo.weights.model.TableData;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Log4j2
@Service
public class ExcelExportService {

    public Resource exportToExcel(List<TableData> dataList) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Data");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"Дата", "Час", "Насіння, т", "Лушпиння, т", "Шрот, т", "Олія, т"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowNum = 1;
            for (TableData data : dataList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(data.getDate());
                row.createCell(1).setCellValue(data.getTime().toString());
                row.createCell(2).setCellValue(data.getSeeds());
                row.createCell(3).setCellValue(data.getHulls());
                row.createCell(4).setCellValue(data.getMeals());
                row.createCell(5).setCellValue(data.getOil());
            }

            workbook.write(outputStream);
            byte[] fileContent = outputStream.toByteArray();
            log.info(String.format("Exported %d records from %s to %s",
                    dataList.size(), dataList.get(0).getDate(), dataList.get(dataList.size() - 1).getDate()));
            return new ByteArrayResource(fileContent);
        } catch (IOException e) {
            log.error("Error during data export: " + e.getMessage(), e);
            return null;
        }
    }
}
