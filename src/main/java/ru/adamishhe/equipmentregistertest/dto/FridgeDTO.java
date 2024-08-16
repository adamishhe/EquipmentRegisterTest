package ru.adamishhe.equipmentregistertest.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.adamishhe.equipmentregistertest.model.fridge.CompressorType;

@Data
public class FridgeDTO {
    @NotNull
    @Valid
    private ModelDTO modelDTO;

    @NotNull
    @Positive
    private Integer numberOfDoors;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CompressorType compressorType;

}