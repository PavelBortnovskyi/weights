package com.neo.weights.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@NoArgsConstructor
@Data
@Table(name = "seeds")
@EqualsAndHashCode(callSuper = true)
@SequenceGenerator(name = "custom_gen", sequenceName = "seeds_id_seq", allocationSize = 1)
public class Seed extends BaseEntity{
}
