package com.neo.weights.controller;

import com.neo.weights.helpers.ExporterFactory;
import com.neo.weights.model.TableData;
import com.neo.weights.service.ExcelExportService;
import com.neo.weights.service.Exporter;
import com.neo.weights.service.TableDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProductionViewController {

    private final TableDataService tableDataService;

    private final ExporterFactory exporterFactory;

    //http://localhost:8080/api/v1/production_view
    @GetMapping("/production_view")
    public ModelAndView handleView(Model model, HttpServletRequest request, @RequestParam(name = "submitted", defaultValue = "false") String submitted,
                                   @RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber) {
        if (submitted.equals("true")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
            LocalDate startDate = LocalDate.parse(request.getParameter("startDate"), formatter);
            LocalDate endDate = LocalDate.parse(request.getParameter("endDate"), formatter);
            LocalTime startTime = LocalTime.parse(request.getParameter("startTime"));
            LocalTime endTime = LocalTime.parse(request.getParameter("endTime"));
            int hoursDelta = Math.abs(endTime.getHour() - startTime.getHour()) + 1;
            if (startDate != null) {
                Page<TableData> dataPage = tableDataService.getPageDataFromPeriod(startDate, endDate, startTime, endTime, Pageable.ofSize(hoursDelta).withPage(pageNumber));
                model.addAttribute("dataPage", dataPage);
            }
        }
        return new ModelAndView("table");
    }

    @PostMapping("/production_view")
    private ModelAndView handlePostViewParam(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                             @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                             @RequestParam("startTime") @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
                                             @RequestParam("endTime") @DateTimeFormat(pattern = "HH:mm") LocalTime endTime,
                                             RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("startDate", startDate);
        redirectAttributes.addAttribute("endDate", endDate);
        redirectAttributes.addAttribute("startTime", startTime);
        redirectAttributes.addAttribute("endTime", endTime);
        redirectAttributes.addAttribute("pageNumber", 0);
        redirectAttributes.addAttribute("submitted", "true");
        return new ModelAndView("redirect:/api/v1/production_view");
    }

    @PostMapping(value = "/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> handleDataExport(
            @RequestParam("exportStartDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("exportEndDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam("exportStartTime") @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
            @RequestParam("exportEndTime") @DateTimeFormat(pattern = "HH:mm") LocalTime endTime,
            @RequestParam("exportType") String exportType) throws IOException {

        String fileExtension = "";
        switch (exportType){
            case "excel" -> fileExtension = ".xlsx";
            case "pdf" -> fileExtension = ".pdf";
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data" + fileExtension);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        Resource export = exporterFactory.getExporter(exportType).export(tableDataService.getDataFromPeriod(startDate, endDate, startTime, endTime));
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(export.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(export);
    }
}
