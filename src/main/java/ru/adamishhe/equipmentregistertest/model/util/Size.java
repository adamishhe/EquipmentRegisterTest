package ru.adamishhe.equipmentregistertest.model.util;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.adamishhe.equipmentregistertest.model.Model;

import java.util.List;

@Entity
@Table(name = "Size")
@Data
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "size")
    private List<Model> models;

    @Positive
    @NotNull
    @Column(name = "length")
    private int length;

    @Positive
    @NotNull
    @Column(name = "width")
    private int width;

    @Positive
    @NotNull
    @Column(name = "height")
    private int height;

}
