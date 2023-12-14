package com.neo.weights.repository;

import com.neo.weights.model.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {

    @Transactional
    @Query("UPDATE Setting s set s.recordAgeType = :recordAgeType, s.recordAgeValue = :recordAgeValue WHERE s.id = 1")
    void changeRecordAgeParams(String recordAgeType, Integer recordAgeValue);
}
