package com.neo.weights.controller;

import com.neo.weights.dto.InputDataDTO;
import com.neo.weights.model.*;
import com.neo.weights.service.TableDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.time.LocalTime;

//@CrossOrigin(originPatterns = {"http://localhost:3000", "https://final-step-fe-8-fs-2.vercel.app"})
@Log4j2
@RestController
@RequestMapping("/api/v1/input")
@Validated
@RequiredArgsConstructor
public class DataInputController {

    private final TableDataService tableDataService;

    @PostMapping(path = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public void handleDataInput(@RequestBody InputDataDTO inputDataDTO) {
        tableDataService.saveData(inputDataDTO);
    }

    @PostMapping(path = "/production_view", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<TableData>> handleJsonFeedback(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                @RequestParam("startTime") @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
                                                @RequestParam("endTime") @DateTimeFormat(pattern = "HH:mm") LocalTime endTime) {
        return ResponseEntity.ok(tableDataService.getPageDataFromPeriod(startDate, endDate, startTime, endTime, Pageable.ofSize(12)));
    }
}
