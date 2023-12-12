package com.neo.weights.repository;

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
public interface SeedRepository extends RepositoryInterface<Seed> {

    @Query(value = "SELECT s FROM Seed s WHERE s.date BETWEEN :startDate AND :endDate " +
            "AND s.time >= :startTime AND s.time <= :endTime ORDER BY s.time ASC")
    Page<Seed> getSeedsDataAtPeriod(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate,
                                    @Param("startTime") LocalTime startTime,
                                    @Param("endTime") LocalTime endTime,
                                    Pageable pageable);
}
