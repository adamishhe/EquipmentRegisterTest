package ru.adamishhe.equipmentregistertest.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.adamishhe.equipmentregistertest.dto.DeviceDTO;
import ru.adamishhe.equipmentregistertest.dto.DeviceTypeDTO;
import ru.adamishhe.equipmentregistertest.model.Device;
import ru.adamishhe.equipmentregistertest.service.DeviceService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;
    private final ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(DeviceTypeController.class);

    public DeviceController(DeviceService deviceService, ModelMapper modelMapper) {
        this.deviceService = deviceService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<DeviceDTO> getAllDevices() {
        return deviceService.getAllDevices().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public DeviceDTO getDeviceById(@PathVariable Long id) {
        return convertToDTO(deviceService.getDeviceById(id));
    }

    @PostMapping
    public ResponseEntity<HttpStatus> createDevice(@RequestBody @Valid DeviceDTO deviceDTO, BindingResult bindingResult) {
        Device device = convertToEntity(deviceDTO);

        if (deviceService.getDevice(device).isPresent()){
            logger.warn("Такой вид техники уже есть");
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Возврат статуса 409 Conflict
        }

        if (bindingResult.hasErrors()) {
            logger.error("Неверное тело запроса: {}", bindingResult.getAllErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Возврат статуса 400 Bad Request
        }
        deviceService.saveDevice(device);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> updateDevice(@PathVariable Long id, @RequestBody @Valid DeviceDTO deviceDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            logger.error("Неверное тело запроса: {}", bindingResult.getAllErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Возврат статуса 400 Bad Request
        }

        Device existingDevice = deviceService.getDeviceById(id);
        if (existingDevice == null) {
            logger.warn("Вид техники с ID {} не найден", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Возврат статуса 404 Not Found
        }

        Device updatedDevice = convertToEntity(deviceDTO);
        updatedDevice.setId(id);
        deviceService.saveDevice(updatedDevice);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteDevice(@PathVariable Long id) {
        Device existingDevice = deviceService.getDeviceById(id);

        if (existingDevice == null) {
            logger.warn("Вид техники с ID {} не найден", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Возврат статуса 404 Not Found
        }

        deviceService.deleteDevice(id);
        logger.info("Вид техники с ID {} успешно удален", id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    public DeviceDTO convertToDTO(Device device) {
        DeviceDTO deviceDTO = modelMapper.map(device, DeviceDTO.class);
        if(device.getDeviceType() != null)
            deviceDTO.setDeviceTypeDTO(modelMapper.map(device.getDeviceType(), DeviceTypeDTO.class));
        return deviceDTO;
    }

    public Device convertToEntity(DeviceDTO deviceDTO) {
        return modelMapper.map(deviceDTO, Device.class);
    }

}
