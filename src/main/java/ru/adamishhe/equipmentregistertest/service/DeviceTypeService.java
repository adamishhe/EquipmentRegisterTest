package ru.adamishhe.equipmentregistertest.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import ru.adamishhe.equipmentregistertest.model.DeviceType;
import ru.adamishhe.equipmentregistertest.repository.DeviceTypeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceTypeService {

    private final DeviceTypeRepository deviceTypeRepository;

    public DeviceTypeService(DeviceTypeRepository deviceTypeRepository) {
        this.deviceTypeRepository = deviceTypeRepository;
    }

    public List<DeviceType> getAllDeviceTypes() {
        return deviceTypeRepository.findAll();
    }

    public DeviceType getDeviceTypeById(Long id) {
        Optional<DeviceType> deviceType = deviceTypeRepository.findById(id);
        return deviceType.orElseThrow(EntityNotFoundException::new);
    }

    public Optional<DeviceType> getDeviceType(DeviceType deviceType) {
        return deviceTypeRepository.findByName(deviceType.getName());
    }

    public void saveDeviceType(DeviceType deviceType) {

        if (deviceType != null) {
            Optional<DeviceType> deviceTypeFromDatabase = getDeviceType(deviceType);
            deviceTypeFromDatabase.ifPresent(type -> deviceType.setId(type.getId()));
            deviceTypeRepository.save(deviceType);
        }
    }

    public void deleteDeviceType(Long id) {
        deviceTypeRepository.findById(id)
                .ifPresentOrElse(
                        deviceType -> deviceTypeRepository.deleteById(id),
                        () -> { throw new EntityNotFoundException("DeviceType with ID " + id + " not found."); }
                );
    }
}