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
import org.springframework.data.domain.PageRequest;
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

    public List<TableData> getDataFromPeriod(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {

        List<TableData> result = new ArrayList<>();
        Page<Seed> seedsPage = seedRepository.getSeedsDataAtPeriod(startDate, endDate, startTime, endTime, Pageable.ofSize(24));
        Page<Hull> hullsPage = hullRepository.getHullsDataAtPeriod(startDate, endDate, startTime, endTime, Pageable.ofSize(24));
        Page<Meal> mealsPage = mealRepository.getMealsDataAtPeriod(startDate, endDate, startTime, endTime, Pageable.ofSize(24));
        Page<Oil> oilPage = oilRepository.getOilDataAtPeriod(startDate, endDate, startTime, endTime, Pageable.ofSize(24));

        System.out.println(seedsPage.getContent());
        for (int i = 1; i < seedsPage.getTotalElements(); i++) {
            TableData data = new TableData();
            data.setTime(seedsPage.getContent().get(i).getTime());
            data.setSeeds(seedsPage.getContent().get(i).getProd());
            data.setHulls(hullsPage.getContent().get(i).getProd());
            data.setMeals(mealsPage.getContent().get(i).getProd());
            data.setOil(oilPage.getContent().get(i).getOilCounter());
            result.add(data);
        }
        return result;
    }

    public Page<TableData> getPageDataFromPeriod(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, Pageable pageable) {

        List<TableData> combinedList = new ArrayList<>();
        Page<Seed> seedsPage = seedRepository.getSeedsDataAtPeriod(startDate, endDate, startTime, endTime, pageable);
        Page<Hull> hullsPage = hullRepository.getHullsDataAtPeriod(startDate, endDate, startTime, endTime, pageable);
        Page<Meal> mealsPage = mealRepository.getMealsDataAtPeriod(startDate, endDate, startTime, endTime, pageable);
        Page<Oil> oilPage = oilRepository.getOilDataAtPeriod(startDate, endDate, startTime, endTime, pageable);

        for (int i = 0; i < seedsPage.getContent().size(); i++) {
            TableData data = new TableData();
            data.setDate(seedsPage.getContent().get(i).getDate());
            data.setTime(seedsPage.getContent().get(i).getTime());
            data.setSeeds(seedsPage.getContent().get(i).getProd());
            data.setHulls(hullsPage.getContent().get(i).getProd());
            data.setMeals(mealsPage.getContent().get(i).getProd());
            data.setOil(oilPage.getContent().get(i).getOilCounter());
            combinedList.add(data);
        }

        return new PageImpl<>(combinedList, pageable, seedsPage.getTotalElements());
    }
}
