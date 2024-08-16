package ru.adamishhe.equipmentregistertest.model.television;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.adamishhe.equipmentregistertest.model.Model;

@Entity
@Table(name = "Television")
@Data
public class Television {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "model_id", referencedColumnName = "id")
    private Model model;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "technology")
    private Technology technology;

}
