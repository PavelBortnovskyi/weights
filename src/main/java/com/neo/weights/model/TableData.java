package com.neo.weights.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TableData {
    private LocalDate date;
    private LocalTime time;
    private Float seeds;
    private Float hulls;
    private Float meals;
    private Float oil;
}
