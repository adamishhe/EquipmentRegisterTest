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
import ru.adamishhe.equipmentregistertest.dto.FridgeDTO;
import ru.adamishhe.equipmentregistertest.dto.ModelDTO;
import ru.adamishhe.equipmentregistertest.dto.SizeDTO;
import ru.adamishhe.equipmentregistertest.model.Device;
import ru.adamishhe.equipmentregistertest.model.Model;
import ru.adamishhe.equipmentregistertest.model.fridge.Fridge;
import ru.adamishhe.equipmentregistertest.model.util.Size;
import ru.adamishhe.equipmentregistertest.service.FridgeService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fridges")
public class FridgeController {

    private final FridgeService fridgeService;
    private final ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(DeviceTypeController.class);

    public  FridgeController(FridgeService  fridgeService, ModelMapper modelMapper) {
        this.fridgeService = fridgeService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<FridgeDTO> getAllFridges(){
        return fridgeService.getAllFridges().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public FridgeDTO getById(@PathVariable("id") Long id){
        return convertToDTO(fridgeService.getFridgeById(id));
    }

    @PostMapping
    public ResponseEntity<HttpStatus> createFridge(@RequestBody @Valid FridgeDTO fridgeDTO, BindingResult bindingResult){
        Fridge fridge = convertToEntity(fridgeDTO);

        if(bindingResult.hasErrors()){
            logger.error("Неверное тело запроса: {}", bindingResult.getAllErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Возврат статуса 400 Bad Request
        }

        fridgeService.saveFridge(fridge);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> updateFridge(@PathVariable Long id, @RequestBody @Valid FridgeDTO fridgeDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Неверное тело запроса: {}", bindingResult.getAllErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Возврат статуса 400 Bad Request
        }

        Fridge existingFridge = fridgeService.getFridgeById(id);
        if (existingFridge == null) {
            logger.warn("Холодильник с ID {} не найден", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Возврат статуса 404 Not Found
        }

        Fridge updatedFridge = convertToEntity(fridgeDTO);
        updatedFridge.setId(id);
        fridgeService.saveFridge(updatedFridge);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteFridge(@PathVariable Long id) {
        Fridge existingFridge = fridgeService.getFridgeById(id);
        if (existingFridge == null) {
            logger.warn("Холодильник с ID {} не найден", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Возврат статуса 404 Not Found
        }

        fridgeService.deleteFridge(id);
        logger.info("Холодильник с ID {} успешно удален", id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/search")
    public List<FridgeDTO> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) Integer minNumberOfDoors,
            @RequestParam(required = false) Integer maxNumberOfDoors,
            @RequestParam(required = false) String compressorType) {

        return fridgeService.findAllByFilters(name, color, minPrice, maxPrice, minNumberOfDoors, maxNumberOfDoors,
                compressorType).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public FridgeDTO convertToDTO(Fridge fridge) {
        FridgeDTO fridgeDTO = modelMapper.map(fridge, FridgeDTO.class);
        Model model = fridge.getModel();
        if(model != null) {
            fridgeDTO.setModelDTO(modelMapper.map(model, ModelDTO.class));

            Size size = model.getSize();
            if(size != null)
                fridgeDTO.getModelDTO().setSizeDTO(modelMapper.map(size, SizeDTO.class));

            Device device = model.getDevice();
            if(device != null)
                fridgeDTO.getModelDTO().setDeviceDTO(modelMapper.map(device, DeviceDTO.class));
        }

        return fridgeDTO;
    }

    public Fridge convertToEntity(FridgeDTO fridgeDTO) {
        return modelMapper.map(fridgeDTO, Fridge.class);
    }

}
