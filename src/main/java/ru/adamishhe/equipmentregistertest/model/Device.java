package ru.adamishhe.equipmentregistertest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Entity
@Table(name = "device")
@Data
@EqualsAndHashCode
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    @NotBlank
    private String name;

    @Column(length = 50)
    @NotBlank
    private String countryOfOrigin;

    @Column(length = 50)
    @NotBlank
    private String manufacturer;

    @Column
    @NotNull
    private Boolean onlineOrderAvailable;

    @Column
    @NotNull
    private Boolean installmentAvailable;

    @ManyToOne
    @JoinColumn(name = "device_type_id", referencedColumnName = "id")
    private DeviceType deviceType;

}
