package ru.adamishhe.equipmentregistertest.model.vacuumCleaner;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.adamishhe.equipmentregistertest.model.Model;

@Entity
@Table(name = "vacuum_cleaner")
@Data
public class VacuumCleaner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Valid
    @ManyToOne
    @JoinColumn(name = "model_id", referencedColumnName = "id")
    private Model model;

    @NotNull
    @Positive
    @Column
    private Integer volumeOfDustCollector;

    @NotNull
    @Positive
    @Column
    private Integer numberOfModes;

}
