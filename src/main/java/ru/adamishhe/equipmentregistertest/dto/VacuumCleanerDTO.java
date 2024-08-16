package ru.adamishhe.equipmentregistertest.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class VacuumCleanerDTO {
    @NotNull
    @Valid
    private ModelDTO modelDTO;

    @NotNull
    @Positive
    private Integer volumeOfDustCollector;

    @NotNull
    @Positive
    private Integer numberOfModes;

}
