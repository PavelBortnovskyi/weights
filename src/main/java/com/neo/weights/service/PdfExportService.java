package com.neo.weights.service;

import com.neo.weights.model.TableData;
import lombok.extern.log4j.Log4j2;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class PdfExportService implements Exporter {

    private final float marginHor = 50;
    private final float marginTop = 10;
    private final float marginBottom = 10;
    private final float tableRowHeight = 20;
    private final int rowsPerPage = 24;

    public Resource export(List<TableData> dataList, List<String> columnsFromParam) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDType0Font primaryFont = PDType0Font.load(document, Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("fonts/ArialCyr.ttf"));

            PDType0Font primaryBoldFont = PDType0Font.load(document, Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("fonts/ArialCyrBold.ttf"));

            int numberOfRows = dataList.size();
            int totalPages = (numberOfRows / rowsPerPage) + 1;

            int subListStartIndex = 0;
            int subListEndIndex = Math.min(rowsPerPage, dataList.size());
            for (int i = 0; i < totalPages; i++) {
                PDPage page = new PDPage();
                document.addPage(page);
                drawTable(document, primaryFont, primaryBoldFont, dataList.subList(subListStartIndex, subListEndIndex), columnsFromParam, i + 1);
                if (subListStartIndex + rowsPerPage < dataList.size()) subListStartIndex += rowsPerPage;
                if (subListEndIndex + rowsPerPage < dataList.size()) subListEndIndex += rowsPerPage;
                else subListEndIndex = dataList.size();
            }

            document.save(outputStream);
            byte[] fileContent = outputStream.toByteArray();
            log.info(String.format("Exported in PDF %d records from %s to %s",
                    dataList.size(), dataList.get(0).getDate(), dataList.get(dataList.size() - 1).getDate()));
            return new ByteArrayResource(fileContent);
        } catch (IOException e) {
            log.error("Error during data export to PDF: " + e.getMessage(), e);
            return null;
        }
    }

    private void drawTable(PDDocument document, PDType0Font primaryFont, PDType0Font boldFont, List<TableData> dataList, List<String> columnsFromParam, int pageNumber) throws IOException {
        List<String> columnsTitle = new ArrayList<>() {{
            add("Дата");
            add("Час");
            addAll(columnsFromParam);
        }};

        PDPage page = document.getPage(document.getNumberOfPages() - 1);
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.setFont(primaryFont, 12);
            float tableTop = page.getMediaBox().getHeight() - marginTop;
            float tableWidth = page.getMediaBox().getWidth() - 2 * marginHor;
            float columnWidth = tableWidth / columnsTitle.size();
            float cursorPositionX = marginHor + 2;
            float cursorPositionY = tableTop - 15;

            drawVertLine(contentStream, marginHor, tableTop, tableRowHeight);
            for (String title : columnsTitle) {
                drawTextInColumn(contentStream, cursorPositionX + 2, cursorPositionY, title);
                cursorPositionX += columnWidth;
                drawVertLine(contentStream, cursorPositionX, tableTop, tableRowHeight);
                cursorPositionX += 1;
            }

            drawHorLine(contentStream, marginHor, tableTop, cursorPositionX - marginHor - 1);
            drawHorLine(contentStream, marginHor, tableTop - tableRowHeight, cursorPositionX - marginHor - 1);

            for (TableData rowData : dataList) {
                cursorPositionY -= tableRowHeight;
                drawTextRow(contentStream, cursorPositionY, columnWidth, rowData);
                drawHorLine(contentStream, marginHor, cursorPositionY - 4, cursorPositionX - marginHor - 1);
            }
            contentStream.setFont(boldFont, 12);
            drawColumnSum(contentStream, cursorPositionY - tableRowHeight, columnWidth, dataList);
            contentStream.setFont(primaryFont, 12);
            drawPageNumber(page, contentStream, pageNumber);
        }
    }

    private void drawPageNumber(PDPage page, PDPageContentStream contentStream, int pageNumber) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset((page.getMediaBox().getWidth() - 2 * marginHor) / 2 - 2, marginBottom + 15);
        contentStream.showText("Сторінка " + pageNumber);
        contentStream.endText();
    }

    private void drawHorLine(PDPageContentStream contentStream, float x, float y, float width) throws IOException {
        contentStream.setLineWidth(1f);
        contentStream.moveTo(x, y);
        contentStream.lineTo(x + width, y);
        contentStream.stroke();
    }

    private void drawVertLine(PDPageContentStream contentStream, float x, float y, float height) throws IOException {
        contentStream.setLineWidth(1f);
        contentStream.moveTo(x, y);
        contentStream.lineTo(x, y - height);
        contentStream.stroke();
    }

    private void drawTextRow(PDPageContentStream contentStream, float y, float columnWidth, TableData data) throws IOException {
        List<String> content = new ArrayList<>();
        content.add(data.getDate().toString());
        content.add(data.getTime().toString());
        content.add(data.getSeeds().toString());
        content.add(data.getHulls().toString());
        content.add(data.getMeals().toString());
        content.add(data.getOil().toString());
        drawVertLine(contentStream, marginHor, tableRowHeight + y - 4, tableRowHeight);
        float cursorX = marginHor + 2;
        for (String text : content) {
            drawTextInColumn(contentStream, cursorX + 2, y + 2, text);
            cursorX += columnWidth;
            drawVertLine(contentStream, cursorX, tableRowHeight + y - 4, tableRowHeight);
            cursorX += 1;
        }
    }

    private void drawColumnSum(PDPageContentStream contentStream, float y, float columnWidth, List<TableData> dataList) throws IOException {
        List<String> content = new ArrayList<>();
        float[] seedSum = new float[1];
        float[] hullSum = new float[1];
        float[] mealSum = new float[1];
        float[] oilSum = new float[1];
        dataList.forEach(data -> {
            seedSum[0] += data.getSeeds();
            hullSum[0] += data.getHulls();
            mealSum[0] += data.getMeals();
            oilSum[0] += data.getOil();
        });
        DecimalFormat df = new DecimalFormat("#.0");
        content.add(df.format(seedSum[0]));
        content.add(df.format(hullSum[0]));
        content.add(df.format(mealSum[0]));
        content.add(df.format(oilSum[0]));

        float cursorX = marginHor + 2;
        drawTextInColumn(contentStream, cursorX, y + 2, "Усьго: ");
        cursorX += 2 * columnWidth;
        for (String text : content) {
            drawTextInColumn(contentStream, cursorX + 2, y + 2, text);
            cursorX += columnWidth + 2;
        }
    }

    private void drawTextInColumn(PDPageContentStream contentStream, float x, float y, String text) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }
}
