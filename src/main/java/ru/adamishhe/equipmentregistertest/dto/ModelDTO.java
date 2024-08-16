package ru.adamishhe.equipmentregistertest.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ModelDTO {
    @Valid
    private DeviceDTO deviceDTO;

    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

    @NotBlank
    @Size(min = 3, max = 30)
    private String color;

    @Valid
    private SizeDTO sizeDTO;

    @NotNull
    @Positive
    private Long price;

    @NotNull
    private Boolean stockAvailable;

}
