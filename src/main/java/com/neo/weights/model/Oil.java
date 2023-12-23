package com.neo.weights.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Entity
@NoArgsConstructor
@Data
@Table(name = "oil")
public class Oil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDate date = LocalDate.now();

    private LocalTime time = LocalTime.now();

    private Float oilFT;

    private Float oilCounter;
}
