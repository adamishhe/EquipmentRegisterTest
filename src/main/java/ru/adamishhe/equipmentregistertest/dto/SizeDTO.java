package ru.adamishhe.equipmentregistertest.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SizeDTO {
    @NotNull
    @Positive
    private int length;

    @NotNull
    @Positive
    private int width;

    @NotNull
    @Positive
    private int height;

}
