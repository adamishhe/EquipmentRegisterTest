package ru.adamishhe.equipmentregistertest.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DeviceTypeDTO {

    @NotBlank
    @Size(min = 3, max = 50)
    private String name;
}
