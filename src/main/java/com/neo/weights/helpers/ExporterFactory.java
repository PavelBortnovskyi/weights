package com.neo.weights.helpers;

import com.neo.weights.service.ExcelExportService;
import com.neo.weights.service.Exporter;
import com.neo.weights.service.PdfExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExporterFactory {

    private final ExcelExportService excelExporter;
    private final PdfExportService pdfExporter;

    public Exporter getExporter(String exportType) {
        switch (exportType) {
            case "excel":
                return excelExporter;
            case "pdf":
                return pdfExporter;
            default:
                throw new IllegalArgumentException("Unsupported export type: " + exportType);
        }
    }
}
