package com.neo.weights.repository;

import com.neo.weights.model.Hull;
import com.neo.weights.model.Oil;
import com.neo.weights.model.Seed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;


@Repository
public interface OilRepository extends JpaRepository<Oil, Long> {

    @Query(value = "SELECT o FROM Oil o WHERE o.date BETWEEN :startDate AND :endDate " +
            "AND o.time >= :startTime AND o.time <= :endTime ORDER BY o.date ASC, o.time ASC")
    Page<Oil> getOilDataAtPeriod(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate,
                                    @Param("startTime") LocalTime startTime,
                                    @Param("endTime") LocalTime endTime,
                                    Pageable pageable);
}
