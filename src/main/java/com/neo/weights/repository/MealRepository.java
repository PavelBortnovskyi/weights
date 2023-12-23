package com.neo.weights.repository;

import com.neo.weights.model.Hull;
import com.neo.weights.model.Meal;
import com.neo.weights.model.Seed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Repository
public interface MealRepository extends RepositoryInterface<Meal> {

    @Query(value = "SELECT m FROM Meal m WHERE m.date BETWEEN :startDate AND :endDate " +
            "AND m.time >= :startTime AND m.time <= :endTime ORDER BY m.date ASC, m.time ASC")
    Page<Meal> getMealsDataAtPeriod(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate,
                                    @Param("startTime") LocalTime startTime,
                                    @Param("endTime") LocalTime endTime,
                                    Pageable pageable);

    void deleteByDateBefore(LocalDate date);

    long countByDateBefore(LocalDate date);

    void deleteByDate(LocalDate date);
}
