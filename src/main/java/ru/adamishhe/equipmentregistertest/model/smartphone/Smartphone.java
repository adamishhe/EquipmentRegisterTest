package ru.adamishhe.equipmentregistertest.model.smartphone;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.adamishhe.equipmentregistertest.model.Model;

@Entity
@Table(name = "Smartphone")
@Data
public class Smartphone {
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
    private Integer memory;

    @NotNull
    @Positive
    @Column
    private Integer numberOfCameras;

}
