package com.neo.weights.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@MappedSuperclass
@NoArgsConstructor
@Getter
@Setter
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "custom_gen")
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    private long id;

    //private LocalDateTime timestamp = LocalDateTime.now();

    private LocalDate date = LocalDate.now();

    private LocalTime time = LocalTime.now();

    private Float prod;

    private Float prodCurrent;

    private Float changeCounter;

    private Float constCounter;
}
