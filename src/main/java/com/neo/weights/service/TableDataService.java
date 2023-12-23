package com.neo.weights.service;

import com.neo.weights.dto.InputDataDTO;
import com.neo.weights.model.*;
import com.neo.weights.repository.HullRepository;
import com.neo.weights.repository.MealRepository;
import com.neo.weights.repository.OilRepository;
import com.neo.weights.repository.SeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
        this.seedRepository.save(seed);

        Hull hull = new Hull();
        hull.setProd(inputDataDTO.getHullProd());
        hull.setProdCurrent(inputDataDTO.getHullProdCurrent());
        hull.setChangeCounter(inputDataDTO.getHullChangeCounter());
        hull.setConstCounter(inputDataDTO.getHullConstCounter());
        this.hullRepository.save(hull);

        Meal meal = new Meal();
        meal.setProd(inputDataDTO.getMealProd());
        meal.setProdCurrent(inputDataDTO.getMealProdCurrent());
        meal.setChangeCounter(inputDataDTO.getMealChangeCounter());
        meal.setConstCounter(inputDataDTO.getMealConstCounter());
        this.mealRepository.save(meal);

        Oil oil = new Oil();
        oil.setOilFT(inputDataDTO.getOilFT());
        oil.setOilCounter(inputDataDTO.getOilCounter());
        this.oilRepository.save(oil);
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

        for (int i = 0; i < seedsPage.getContent().size(); i++) {
            TableData data = new TableData();
            data.setDate(seedsPage.getContent().get(i).getDate());
            data.setTime(seedsPage.getContent().get(i).getTime());
            data.setSeeds(dataExtractor.extract(seedsPage, i, paramTypes.get(0)));
            data.setHulls(dataExtractor.extract(hullsPage, i, paramTypes.get(1)));
            data.setMeals(dataExtractor.extract(mealsPage, i, paramTypes.get(2)));
            data.setOil(dataExtractor.extract(oilPage, i, paramTypes.get(3)));
            result.add(data);
        }
        return new PageImpl<>(result, pageable, seedsPage.getTotalElements());
    }
}
