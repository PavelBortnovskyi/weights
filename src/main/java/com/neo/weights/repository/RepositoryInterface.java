package com.neo.weights.repository;

import com.neo.weights.model.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;


public interface RepositoryInterface<E extends BaseEntity> extends JpaRepository<E, Long> {

}
