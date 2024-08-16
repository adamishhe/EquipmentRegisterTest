package ru.adamishhe.equipmentregistertest.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.adamishhe.equipmentregistertest.dto.DeviceTypeDTO;
import ru.adamishhe.equipmentregistertest.model.DeviceType;
import ru.adamishhe.equipmentregistertest.service.DeviceTypeService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/deviceTypes")
public class DeviceTypeController {

    private final DeviceTypeService deviceTypeService;
    private final ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(DeviceTypeController.class);


    public DeviceTypeController(DeviceTypeService deviceTypeService, ModelMapper modelMapper) {
        this.deviceTypeService = deviceTypeService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<DeviceTypeDTO> getAllDeviceTypes() {
        return deviceTypeService.getAllDeviceTypes().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public DeviceTypeDTO getDeviceTypeById(@PathVariable Long id) {
        return convertToDTO(deviceTypeService.getDeviceTypeById(id));
    }

    @PostMapping
    public ResponseEntity<HttpStatus> createDeviceType(@RequestBody @Valid DeviceTypeDTO deviceTypeDTO, BindingResult bindingResult) {
        DeviceType deviceType = convertToEntity(deviceTypeDTO);

        if (deviceTypeService.getDeviceType(deviceType).isPresent()){
            logger.warn("Такой тип техники уже есть");
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Возврат статуса 409 Conflict
        }

        if (bindingResult.hasErrors()) {
            logger.error("Неверное тело запроса: {}", bindingResult.getAllErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Возврат статуса 400 Bad Request
        }
        deviceTypeService.saveDeviceType(deviceType);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> updateDeviceType(@PathVariable Long id, @RequestBody @Valid DeviceTypeDTO deviceTypeDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Неверное тело запроса: {}", bindingResult.getAllErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Возврат статуса 400 Bad Request
        }

        DeviceType existingDeviceType = deviceTypeService.getDeviceTypeById(id);
        if (existingDeviceType == null) {
            logger.warn("Тип техники с ID {} не найден", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Возврат статуса 404 Not Found
        }

        DeviceType updatedDeviceType = convertToEntity(deviceTypeDTO);
        updatedDeviceType.setId(id);
        deviceTypeService.saveDeviceType(updatedDeviceType);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteDeviceType(@PathVariable Long id) {
        DeviceType existingDeviceType = deviceTypeService.getDeviceTypeById(id);
        if (existingDeviceType == null) {
            logger.warn("Тип техники с ID {} не найден", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Возврат статуса 404 Not Found
        }

        deviceTypeService.deleteDeviceType(id);
        logger.info("Тип техники с ID {} успешно удален", id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    public DeviceTypeDTO convertToDTO(DeviceType deviceType) {
        return modelMapper.map(deviceType, DeviceTypeDTO.class);
    }

    public DeviceType convertToEntity(DeviceTypeDTO deviceTypeDTO) {
        return modelMapper.map(deviceTypeDTO, DeviceType.class);
    }
}