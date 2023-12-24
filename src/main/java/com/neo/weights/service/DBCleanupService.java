package com.neo.weights.service;

import com.neo.weights.model.Setting;
import com.neo.weights.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class DBCleanupService {

    private final SeedRepository seedRepository;
    private final HullRepository hullRepository;
    private final MealRepository mealRepository;
    private final OilRepository oilRepository;
    private final SettingRepository settingRepository;

    private LocalDate cutoffDate = LocalDate.now().minusYears(1);

    @Transactional
    @Scheduled(cron = "0 0 16 * * 5") // запланова очистка кожної п'ятниці о 16:00
    public synchronized void cleanupOldData() {
        Optional<Setting> maybeUserSetting = settingRepository.findById(1L);
        if (maybeUserSetting.isPresent()) {
            Setting userSetting = maybeUserSetting.get();
            switch (userSetting.getRecordAgeType()) {
                case "years" -> cutoffDate = LocalDate.now().minusYears(userSetting.getRecordAgeValue());
                case "months" -> cutoffDate = LocalDate.now().minusMonths(userSetting.getRecordAgeValue());
                case "days" -> cutoffDate = LocalDate.now().minusDays(userSetting.getRecordAgeValue());
            }
        }
        log.info(String.format("Scheduled DB clean procedure checking for DB records before: %s to make cleanup", cutoffDate));
        long numberRecordsToRemove = seedRepository.countByDateBefore(cutoffDate) * 4;
        log.info(String.format("Found %d records to delete", numberRecordsToRemove));
        this.deleteRecordsBefore(cutoffDate);
    }

    @Transactional
    public void deleteAllBefore(LocalDate cutoffDate) {
        this.deleteRecordsBefore(cutoffDate);
    }

    public void setDateCleanupParams(String recordAgeType, Integer recordAgeValue) {
        if (this.settingRepository.findById(1L).isEmpty()) {
            this.settingRepository.save(new Setting(recordAgeType, recordAgeValue));
            log.info(String.format("Created user settings record with params recordAgeType = %s, recordAgeValue == %d",
                    recordAgeType, recordAgeValue));
        } else {
            log.info(String.format("User settings record changed with params recordAgeType = %s, recordAgeValue == %d",
                    recordAgeType, recordAgeValue));
            this.settingRepository.changeRecordAgeParams(recordAgeType, recordAgeValue);
        }
    }

    @Transactional
    public void deleteDate(LocalDate deleteDate) {
        this.seedRepository.deleteByDate(deleteDate);
        this.hullRepository.deleteByDate(deleteDate);
        this.mealRepository.deleteByDate(deleteDate);
        this.oilRepository.deleteByDate(deleteDate);
        log.info("Deleted all records at " + deleteDate);
    }

    private void deleteRecordsBefore(LocalDate cutoffDate) {
        long numberRecordsToRemove = seedRepository.countByDateBefore(cutoffDate) * 4;
        if (numberRecordsToRemove > 0) {
            seedRepository.deleteByDateBefore(cutoffDate);
            hullRepository.deleteByDateBefore(cutoffDate);
            mealRepository.deleteByDateBefore(cutoffDate);
            oilRepository.deleteByDateBefore(cutoffDate);
            log.info(String.format("%d records was deleted before cutoff date %s", numberRecordsToRemove, cutoffDate.toString()));
        } else log.info("No records for delete found");
    }
}
