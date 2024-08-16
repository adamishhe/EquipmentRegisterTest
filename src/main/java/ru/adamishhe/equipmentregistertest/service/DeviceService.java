package ru.adamishhe.equipmentregistertest.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import ru.adamishhe.equipmentregistertest.model.Device;
import ru.adamishhe.equipmentregistertest.model.Model;
import ru.adamishhe.equipmentregistertest.repository.DeviceRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceTypeService deviceTypeService;

    public DeviceService(DeviceRepository deviceRepository, DeviceTypeService deviceTypeService) {
        this.deviceRepository = deviceRepository;
        this.deviceTypeService = deviceTypeService;
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    public Device getDeviceById(Long id) {
        Optional<Device> device = deviceRepository.findById(id);
        return device.orElseThrow(EntityNotFoundException::new);
    }

    public Optional<Device> getDevice(Device device) {
        return deviceRepository.findByNameAndCountryOfOriginAndManufacturerAndOnlineOrderAvailableAndInstallmentAvailable(
                device.getName(),
                device.getCountryOfOrigin(),
                device.getManufacturer(),
                device.getOnlineOrderAvailable(),
                device.getInstallmentAvailable());
    }

//    public void saveDevice(Device device) { //////////////////////////////////////peredelat'!!!???!??!!!?
//        if (device != null) {
//            Optional<Device> deviceFromDatabase = getDevice(device);
//            deviceFromDatabase.ifPresent(type -> device.setId(type.getId()));
//            deviceRepository.save(device);
//        }
//    }

    public void saveDevice(Device device) {
        Optional<Device> deviceFromDatabase = getDevice(device);

        if(deviceFromDatabase.isPresent()) {
            device.setId(deviceFromDatabase.get().getId());
        } else {
            deviceTypeService.saveDeviceType(device.getDeviceType());

            deviceRepository.save(device);
        }
    }

    public void deleteDevice(Long id) {
        deviceRepository.findById(id)
                .ifPresentOrElse(
                        device -> deviceRepository.deleteById(id),
                        () -> { throw new EntityNotFoundException("Device with ID " + id + " not found."); }
                );
    }

}
