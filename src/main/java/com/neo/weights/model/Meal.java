package com.neo.weights.model;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "meals")
@EqualsAndHashCode(callSuper = true)
@SequenceGenerator(name = "custom_gen", sequenceName = "meal_id_seq", allocationSize = 1)
public class Meal extends BaseEntity{
}
