package com.neo.weights.controller;

import com.neo.weights.constants.Parameters;
import com.neo.weights.helpers.ExporterFactory;
import com.neo.weights.model.TableData;
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
import java.util.ArrayList;
import java.util.List;


@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProductionViewController {

    private final TableDataService tableDataService;
    private final ExporterFactory exporterFactory;

    //http://localhost:8080/api/v1/production_view
    @GetMapping("/production_view")
    public ModelAndView handleView(Model model, HttpServletRequest request,
                                   @RequestParam(name = Parameters.SUBMITTED, defaultValue = "false") String submitted,
                                   @RequestParam(name = Parameters.PAGE_NUMBER, defaultValue = "0") Integer pageNumber) {
        List<String> recordTypes = new ArrayList<>() {{
        add(request.getParameter(Parameters.SEED_RECORD_TYPE) == null ? "seedProd" : request.getParameter(Parameters.SEED_RECORD_TYPE));
        add(request.getParameter(Parameters.HULL_RECORD_TYPE) == null ? "hullProd" : request.getParameter(Parameters.HULL_RECORD_TYPE));
        add(request.getParameter(Parameters.MEAL_RECORD_TYPE) == null ? "mealProd" : request.getParameter(Parameters.MEAL_RECORD_TYPE));
        add(request.getParameter(Parameters.OIL_RECORD_TYPE) == null ? "oilFT" : request.getParameter(Parameters.OIL_RECORD_TYPE));}};
        if (submitted.equals("true")) {
            String date = request.getParameter(Parameters.START_DATE);
            DateTimeFormatter formatter = date.matches("\\d{1,2}.\\d{1,2}.\\d{4}") ? DateTimeFormatter.ofPattern("dd.MM.yyyy") : DateTimeFormatter.ofPattern("dd.MM.yy");
            LocalDate startDate = LocalDate.parse(request.getParameter(Parameters.START_DATE), formatter);
            LocalDate endDate = LocalDate.parse(request.getParameter(Parameters.END_DATE), formatter);
            LocalTime startTime = LocalTime.parse(request.getParameter(Parameters.START_TIME));
            LocalTime endTime = LocalTime.parse(request.getParameter(Parameters.END_TIME));
            int hoursDelta = Math.abs(endTime.getHour() - startTime.getHour()) + 1;
            if (startDate != null) {
                Page<TableData> dataPage = tableDataService.getPageDataFromPeriod(startDate, endDate, startTime, endTime, Pageable.ofSize(hoursDelta).withPage(pageNumber), recordTypes);
                model.addAttribute("dataPage", dataPage);
            }
        }
        return new ModelAndView("table");
    }

    @PostMapping("/production_view")
    private ModelAndView handlePostViewParam(@RequestParam(Parameters.START_DATE) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                             @RequestParam(Parameters.END_DATE) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                             @RequestParam(Parameters.START_TIME) @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
                                             @RequestParam(Parameters.END_TIME) @DateTimeFormat(pattern = "HH:mm") LocalTime endTime,
                                             @RequestParam(Parameters.SEED_RECORD_TYPE) String seedRecordType,
                                             @RequestParam(Parameters.HULL_RECORD_TYPE) String hullRecordType,
                                             @RequestParam(Parameters.MEAL_RECORD_TYPE) String mealRecordType,
                                             @RequestParam(Parameters.OIL_RECORD_TYPE) String oilRecordType,
                                             RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute(Parameters.START_DATE, startDate);
        redirectAttributes.addAttribute(Parameters.END_DATE, endDate);
        redirectAttributes.addAttribute(Parameters.START_TIME, startTime);
        redirectAttributes.addAttribute(Parameters.END_TIME, endTime);
        redirectAttributes.addAttribute(Parameters.PAGE_NUMBER, 0);
        redirectAttributes.addAttribute(Parameters.SEED_RECORD_TYPE, seedRecordType);
        redirectAttributes.addAttribute(Parameters.HULL_RECORD_TYPE, hullRecordType);
        redirectAttributes.addAttribute(Parameters.MEAL_RECORD_TYPE, mealRecordType);
        redirectAttributes.addAttribute(Parameters.OIL_RECORD_TYPE, oilRecordType);
        redirectAttributes.addAttribute(Parameters.SUBMITTED, "true");
        return new ModelAndView("redirect:/api/v1/production_view");
    }

    @PostMapping(value = "/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> handleDataExport(
            @RequestParam(Parameters.EXPORT_START_DATE) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(Parameters.EXPORT_END_DATE) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(Parameters.EXPORT_START_TIME) @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
            @RequestParam(Parameters.EXPORT_END_TIME) @DateTimeFormat(pattern = "HH:mm") LocalTime endTime,
            @RequestParam(Parameters.SEED_RECORD_TYPE) String seedRecordType,
            @RequestParam(Parameters.HULL_RECORD_TYPE) String hullRecordType,
            @RequestParam(Parameters.MEAL_RECORD_TYPE) String mealRecordType,
            @RequestParam(Parameters.OIL_RECORD_TYPE) String oilRecordType,
            @RequestParam(Parameters.EXPORT_TYPE) String exportType) throws IOException {

        List<String> paramTypes = new ArrayList<>(){{
            add(seedRecordType);
            add(hullRecordType);
            add(mealRecordType);
            add(oilRecordType);
        }};

        String fileExtension = "";
        switch (exportType){
            case "excel" -> fileExtension = ".xlsx";
            case "pdf" -> fileExtension = ".pdf";
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data" + fileExtension);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        Resource export = exporterFactory.getExporter(exportType).export(tableDataService.getDataFromPeriod(startDate, endDate, startTime, endTime, paramTypes), paramTypes);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(export.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(export);
    }
}
