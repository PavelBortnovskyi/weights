package com.neo.weights.controller;

import com.neo.weights.dto.InputDataDTO;
import com.neo.weights.service.DBCleanupService;
import com.neo.weights.service.TableDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/data")
public class DataManageController {

    private final DBCleanupService DBCleanupService;
    private final TableDataService tableDataService;

    @PostMapping(path = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public void handleDataInput(@RequestBody InputDataDTO inputDataDTO) {
        tableDataService.saveData(inputDataDTO);
    }

    @PostMapping(value = "/settings", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> handleSaveSettingsParam(@RequestParam(name = "records-age-type") String ageType,
                                                           @RequestParam(name = "records-age-value") Integer ageValue) {
        DBCleanupService.setDateCleanupParams(ageType, ageValue);
        return ResponseEntity.ok("Settings saved");
    }

    @PostMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> handleDeleteData(@RequestParam(name = "cutoffDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate cutoffDate) {
        DBCleanupService.deleteAllBefore(cutoffDate);
        return ResponseEntity.ok("Cutoff date set and all earlier records deleted");
    }

    @PostMapping(value = "/delete_by_date", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> handleDeleteDataByDate(@RequestParam(name = "deleteDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate deleteDate) {
        DBCleanupService.deleteDate(deleteDate);
        return ResponseEntity.ok("Records at " + deleteDate + " was deleted");
    }

}
