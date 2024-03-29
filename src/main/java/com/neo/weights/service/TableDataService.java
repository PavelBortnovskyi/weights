package com.neo.weights.service;

import com.neo.weights.dto.InputDataDTO;
import com.neo.weights.model.*;
import com.neo.weights.repository.HullRepository;
import com.neo.weights.repository.MealRepository;
import com.neo.weights.repository.OilRepository;
import com.neo.weights.repository.SeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class TableDataService {

    private final SeedRepository seedRepository;
    private final HullRepository hullRepository;
    private final MealRepository mealRepository;
    private final OilRepository oilRepository;
    private final DataExtractor dataExtractor;

    @Transactional
    public void saveData(InputDataDTO inputDataDTO) {
        Seed seed = new Seed();
        seed.setProd(inputDataDTO.getSeedProd());
        seed.setProdCurrent(inputDataDTO.getSeedProdCurrent());
        seed.setChangeCounter(inputDataDTO.getSeedChangeCounter());
        seed.setConstCounter(inputDataDTO.getSeedConstCounter());
        seed = this.seedRepository.save(seed);
        log.info(String.format("Seed record: %s added", seed));

        Hull hull = new Hull();
        hull.setProd(inputDataDTO.getHullProd());
        hull.setProdCurrent(inputDataDTO.getHullProdCurrent());
        hull.setChangeCounter(inputDataDTO.getHullChangeCounter());
        hull.setConstCounter(inputDataDTO.getHullConstCounter());
        hull = this.hullRepository.save(hull);
        log.info(String.format("Hull record: %s added", hull));

        Meal meal = new Meal();
        meal.setProd(inputDataDTO.getMealProd());
        meal.setProdCurrent(inputDataDTO.getMealProdCurrent());
        meal.setChangeCounter(inputDataDTO.getMealChangeCounter());
        meal.setConstCounter(inputDataDTO.getMealConstCounter());
        meal = this.mealRepository.save(meal);
        log.info(String.format("Meal record: %s added", meal));

        Oil oil = new Oil();
        oil.setOilFT(inputDataDTO.getOilFT());
        oil.setOilCounter(inputDataDTO.getOilCounter());
        oil = this.oilRepository.save(oil);
        log.info(String.format("Oil record: %s added", oil));
    }


    public List<TableData> getDataFromPeriod(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, List<String> paramTypes) {
        return getPageDataFromPeriod(startDate, endDate, startTime, endTime, Pageable.ofSize(Integer.MAX_VALUE), paramTypes).getContent();
    }

    public Page<TableData> getPageDataFromPeriod(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime,
                                                 Pageable pageable, List<String> paramTypes) {
        List<TableData> result = new ArrayList<>();
        Page<Seed> seedsPage = seedRepository.getSeedsDataAtPeriod(startDate, endDate, startTime, endTime, pageable);
        Page<Hull> hullsPage = hullRepository.getHullsDataAtPeriod(startDate, endDate, startTime, endTime, pageable);
        Page<Meal> mealsPage = mealRepository.getMealsDataAtPeriod(startDate, endDate, startTime, endTime, pageable);
        Page<Oil> oilPage = oilRepository.getOilDataAtPeriod(startDate, endDate, startTime, endTime, pageable);

        int resultPointer = 0;
        for (int i = 0; i < seedsPage.getContent().size(); i++) {
            TableData data = new TableData();
            data.setDate(seedsPage.getContent().get(i).getDate());
            data.setTime(seedsPage.getContent().get(i).getTime());
            data.setSeeds(dataExtractor.extract(seedsPage, i, paramTypes.get(0)));
            data.setHulls(dataExtractor.extract(hullsPage, i, paramTypes.get(1)));
            data.setMeals(dataExtractor.extract(mealsPage, i, paramTypes.get(2)));
            data.setOil(dataExtractor.extract(oilPage, i, paramTypes.get(3)));
            if (result.size() == 0 || (result.get(resultPointer - 1).getTime().getHour() != data.getTime().getHour())) {
                result.add(data);
                resultPointer++;
            }
        }
        return new PageImpl<>(result, pageable, seedsPage.getTotalElements());
    }
}
