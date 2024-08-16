package ru.adamishhe.equipmentregistertest.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DeviceDTO {

    @Valid
    private DeviceTypeDTO deviceTypeDTO;

    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

    @NotBlank
    @Size(min = 3, max = 50)
    private String countryOfOrigin;

    @NotBlank
    @Size(min = 3, max = 50)
    private String manufacturer;

    @NotNull
    private Boolean onlineOrderAvailable;

    @NotNull
    private Boolean installmentAvailable;

}
