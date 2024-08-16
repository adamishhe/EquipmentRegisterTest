package ru.adamishhe.equipmentregistertest.model.computer;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.adamishhe.equipmentregistertest.model.Model;

@Entity
@Table(name = "Computer")
@Data
public class Computer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "model_id", referencedColumnName = "id")
    private Model model;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column
    private Category category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column
    private ProcessorType processorType;
}