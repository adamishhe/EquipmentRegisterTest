package ru.adamishhe.equipmentregistertest.dto;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.adamishhe.equipmentregistertest.model.television.Category;
import ru.adamishhe.equipmentregistertest.model.television.Technology;

@Data
public class TelevisionDTO {
    @NotNull
    @Valid
    private ModelDTO modelDTO;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Technology technology;

}
