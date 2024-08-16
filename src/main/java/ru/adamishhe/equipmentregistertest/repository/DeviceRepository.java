package ru.adamishhe.equipmentregistertest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.adamishhe.equipmentregistertest.model.Device;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByNameAndCountryOfOriginAndManufacturerAndOnlineOrderAvailableAndInstallmentAvailable(String name,
                                                                                                                String countryOfOrigin,
                                                                                                                String manufacturer,
                                                                                                                Boolean onlineOrderAvailable,
                                                                                                                Boolean installmentAvailable);

}