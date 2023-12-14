package com.neo.weights.repository;

import com.neo.weights.model.Hull;
import com.neo.weights.model.Seed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;


@Repository
public interface HullRepository extends RepositoryInterface<Hull> {

    @Query(value = "SELECT h FROM Hull h WHERE h.date BETWEEN :startDate AND :endDate " +
            "AND h.time >= :startTime AND h.time <= :endTime ORDER BY h.date ASC, h.time ASC")
    Page<Hull> getHullsDataAtPeriod(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate,
                                    @Param("startTime") LocalTime startTime,
                                    @Param("endTime") LocalTime endTime,
                                    Pageable pageable);

    void deleteByDateBefore(LocalDate date);

    long countByDateBefore(LocalDate date);
}
