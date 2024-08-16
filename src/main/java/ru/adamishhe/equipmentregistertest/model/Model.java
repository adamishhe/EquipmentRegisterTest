package ru.adamishhe.equipmentregistertest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.adamishhe.equipmentregistertest.model.computer.Computer;
import ru.adamishhe.equipmentregistertest.model.fridge.Fridge;
import ru.adamishhe.equipmentregistertest.model.smartphone.Smartphone;
import ru.adamishhe.equipmentregistertest.model.television.Television;
import ru.adamishhe.equipmentregistertest.model.util.Size;
import ru.adamishhe.equipmentregistertest.model.vacuumCleaner.VacuumCleaner;

import java.util.List;

@Entity
@Table(name = "model")
@Data
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    @NotNull
    private String name;

    @Column(length = 50)
    @NotBlank
    private String serialNumber;

    @Column(length = 30)
    @NotBlank
    private String color;

    @ManyToOne
    @JoinColumn(name = "size_id", referencedColumnName = "id")
    private Size size;

    @Column
    @Positive
    private Long price;

    @Column
    @NotNull
    private Boolean stockAvailable;

    @OneToMany(mappedBy = "model")
    private List<Computer> computers;

    @OneToMany(mappedBy = "model")
    private List<Fridge> fridges;

    @OneToMany(mappedBy = "model")
    private List<Smartphone> smartphones;

    @OneToMany(mappedBy = "model")
    private List<Television> televisions;

    @OneToMany(mappedBy = "model")
    private List<VacuumCleaner> vacuumCleaners;

    @ManyToOne
    @JoinColumn(name = "device_id", referencedColumnName = "id")
    private Device device;

}