package com.neo.weights.model;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "seeds")
@EqualsAndHashCode(callSuper = true)
@SequenceGenerator(name = "custom_gen", sequenceName = "seeds_id_seq", allocationSize = 1)
public class Seed extends BaseEntity{
}
