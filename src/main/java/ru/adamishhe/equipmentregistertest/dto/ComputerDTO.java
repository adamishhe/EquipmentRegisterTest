package ru.adamishhe.equipmentregistertest.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.adamishhe.equipmentregistertest.model.computer.Category;
import ru.adamishhe.equipmentregistertest.model.computer.ProcessorType;

@Data
public class ComputerDTO {
    @NotNull
    @Valid
    private ModelDTO modelDTO;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProcessorType processorType;

}
