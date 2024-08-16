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
import ru.adamishhe.equipmentregistertest.dto.ModelDTO;
import ru.adamishhe.equipmentregistertest.dto.SizeDTO;
import ru.adamishhe.equipmentregistertest.dto.SmartphoneDTO;
import ru.adamishhe.equipmentregistertest.model.Device;
import ru.adamishhe.equipmentregistertest.model.Model;
import ru.adamishhe.equipmentregistertest.model.smartphone.Smartphone;
import ru.adamishhe.equipmentregistertest.model.util.Size;
import ru.adamishhe.equipmentregistertest.service.SmartphoneService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/smartphones")
public class SmartphoneController {
    private final SmartphoneService smartphoneService;
    private final ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(DeviceTypeController.class);

    public  SmartphoneController(SmartphoneService smartphoneService, ModelMapper modelMapper) {
        this.smartphoneService = smartphoneService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<SmartphoneDTO> getAllSmartphones(){
        return smartphoneService.getAllSmartphones().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public SmartphoneDTO getById(@PathVariable("id") Long id){
        return convertToDTO(smartphoneService.getSmartphoneById(id));
    }

    @PostMapping
    public ResponseEntity<HttpStatus> createSmartphone(@RequestBody @Valid SmartphoneDTO smartphoneDTO, BindingResult bindingResult){
        Smartphone smartphone = convertToEntity(smartphoneDTO);

        if(bindingResult.hasErrors()){
            logger.error("Неверное тело запроса: {}", bindingResult.getAllErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Возврат статуса 400 Bad Request
        }

        smartphoneService.saveSmartphone(smartphone);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> updateSmartphone(@PathVariable Long id, @RequestBody @Valid SmartphoneDTO smartphoneDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Неверное тело запроса: {}", bindingResult.getAllErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Возврат статуса 400 Bad Request
        }

        Smartphone existingSmartphone = smartphoneService.getSmartphoneById(id);
        if (existingSmartphone == null) {
            logger.warn("Смартфон с ID {} не найден", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Возврат статуса 404 Not Found
        }

        Smartphone updatedSmartphone = convertToEntity(smartphoneDTO);
        updatedSmartphone.setId(id);
        smartphoneService.saveSmartphone(updatedSmartphone);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteSmartphone(@PathVariable Long id) {
        Smartphone existingSmartphone = smartphoneService.getSmartphoneById(id);
        if (existingSmartphone == null) {
            logger.warn("Смартфон с ID {} не найден", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Возврат статуса 404 Not Found
        }

        smartphoneService.deleteSmartphone(id);
        logger.info("Смартфон с ID {} успешно удален", id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/search")
    public List<SmartphoneDTO> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) Integer minMemory,
            @RequestParam(required = false) Integer maxMemory,
            @RequestParam(required = false) Integer minNumberOfCameras,
            @RequestParam(required = false) Integer maxNumberOfCameras) {

        return smartphoneService.findAllByFilters(name, color, minPrice, maxPrice, minMemory, maxMemory,minNumberOfCameras
                ,maxNumberOfCameras).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public SmartphoneDTO convertToDTO(Smartphone smartphone) {
        SmartphoneDTO smartphoneDTO = modelMapper.map(smartphone, SmartphoneDTO.class);
        Model model = smartphone.getModel();
        if(model != null) {
            smartphoneDTO.setModelDTO(modelMapper.map(model, ModelDTO.class));

            Size size = model.getSize();
            if(size != null)
                smartphoneDTO.getModelDTO().setSizeDTO(modelMapper.map(size, SizeDTO.class));

            Device technic = model.getDevice();
            if(technic != null)
                smartphoneDTO.getModelDTO().setDeviceDTO(modelMapper.map(technic, DeviceDTO.class));
        }

        return smartphoneDTO;
    }

    public Smartphone convertToEntity(SmartphoneDTO smartphoneDTO) {
        return modelMapper.map(smartphoneDTO, Smartphone.class);
    }

}
