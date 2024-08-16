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
import ru.adamishhe.equipmentregistertest.dto.TelevisionDTO;
import ru.adamishhe.equipmentregistertest.model.Device;
import ru.adamishhe.equipmentregistertest.model.Model;
import ru.adamishhe.equipmentregistertest.model.television.Television;
import ru.adamishhe.equipmentregistertest.model.util.Size;
import ru.adamishhe.equipmentregistertest.service.TelevisionService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/televisions")
public class TelevisionController {
    private final TelevisionService televisionService;
    private final ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(DeviceTypeController.class);

    public  TelevisionController(TelevisionService televisionService, ModelMapper modelMapper) {
        this.televisionService = televisionService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<TelevisionDTO> getAllTelevisions(){
        return televisionService.getAllTelevisions().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TelevisionDTO getById(@PathVariable("id") Long id){
        return convertToDTO(televisionService.getTelevisionById(id));
    }

    @PostMapping
    public ResponseEntity<HttpStatus> createTelevision(@RequestBody @Valid TelevisionDTO televisionDTO, BindingResult bindingResult){
        Television television = convertToEntity(televisionDTO);

        if(bindingResult.hasErrors()){
            logger.error("Неверное тело запроса: {}", bindingResult.getAllErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Возврат статуса 400 Bad Request
        }

        televisionService.saveTelevision(television);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> updateTelevision(@PathVariable Long id, @RequestBody @Valid TelevisionDTO televisionDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Неверное тело запроса: {}", bindingResult.getAllErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Возврат статуса 400 Bad Request
        }

        Television existingTelevision = televisionService.getTelevisionById(id);
        if (existingTelevision == null) {
            logger.warn("TV с ID {} не найден", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Возврат статуса 404 Not Found
        }

        Television updatedTelevision = convertToEntity(televisionDTO);
        updatedTelevision.setId(id);
        televisionService.saveTelevision(updatedTelevision);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteTelevision(@PathVariable Long id) {
        Television existingTelevision = televisionService.getTelevisionById(id);
        if (existingTelevision == null) {
            logger.warn("TV с ID {} не найден", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Возврат статуса 404 Not Found
        }

        televisionService.deleteTelevision(id);
        logger.info("TV с ID {} успешно удален", id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/search")
    public List<TelevisionDTO> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String technology) {

        return televisionService.findAllByFilters(name, color, minPrice, maxPrice, category, technology)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    public TelevisionDTO convertToDTO(Television television) {
        TelevisionDTO televisionDTO = modelMapper.map(television, TelevisionDTO.class);
        Model model = television.getModel();
        if(model != null) {
            televisionDTO.setModelDTO(modelMapper.map(model, ModelDTO.class));

            Size size = model.getSize();
            if(size != null)
                televisionDTO.getModelDTO().setSizeDTO(modelMapper.map(size, SizeDTO.class));

            Device device = model.getDevice();
            if(device != null)
                televisionDTO.getModelDTO().setDeviceDTO(modelMapper.map(device, DeviceDTO.class));
        }

        return televisionDTO;
    }


    public Television convertToEntity(TelevisionDTO televisionDTO) {
        return modelMapper.map(televisionDTO, Television.class);
    }
}
