package ru.adamishhe.equipmentregistertest.model.fridge;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.adamishhe.equipmentregistertest.model.Model;

@Entity
@Table(name = "Fridge")
@Data
public class Fridge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "model_id", referencedColumnName = "id")
    private Model model;

    @NotNull
    @Positive
    @Column
    private Integer numberOfDoors;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column
    private CompressorType compressorType;

}
