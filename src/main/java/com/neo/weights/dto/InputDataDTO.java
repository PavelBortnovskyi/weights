package com.neo.weights.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InputDataDTO {

    @NotNull(message = "Need to specify seedProd parameter!")
    Float seedProd;

    @NotNull(message = "Need to specify seedProdCurrent parameter!")
    Float seedProdCurrent;

    @NotNull(message = "Need to specify seedChangeCounter parameter!")
    Float seedChangeCounter;

    @NotNull(message = "Need to specify seedConstCounter parameter!")
    Float seedConstCounter;

    @NotNull(message = "Need to specify hullProd parameter!")
    Float hullProd;

    @NotNull(message = "Need to specify hullProdCurrent parameter!")
    Float hullProdCurrent;

    @NotNull(message = "Need to specify hullChangeCounter parameter!")
    Float hullChangeCounter;

    @NotNull(message = "Need to specify hullConstCounter parameter!")
    Float hullConstCounter;

    @NotNull(message = "Need to specify mealProd parameter!")
    Float mealProd;

    @NotNull(message = "Need to specify mealProdCurrent parameter!")
    Float mealProdCurrent;

    @NotNull(message = "Need to specify mealChangeCounter parameter!")
    Float mealChangeCounter;

    @NotNull(message = "Need to specify mealConstCounter parameter!")
    Float mealConstCounter;

    @NotNull(message = "Need to specify oilFT parameter!")
    Float oilFT;

    @NotNull(message = "Need to specify oilCounter parameter!")
    Float oilCounter;
}
