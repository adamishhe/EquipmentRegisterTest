package ru.adamishhe.equipmentregistertest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SmartphoneDTO {

    @NotNull
    @Valid
    private ModelDTO modelDTO;

    @NotNull
    @Positive
    private Integer memory;

    @NotNull
    @Positive
    private Integer numberOfCameras;

}
