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
import ru.adamishhe.equipmentregistertest.dto.ModelDTO;
import ru.adamishhe.equipmentregistertest.dto.SizeDTO;
import ru.adamishhe.equipmentregistertest.model.DeviceType;
import ru.adamishhe.equipmentregistertest.model.Model;
import ru.adamishhe.equipmentregistertest.service.ModelService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/models")
public class ModelController {

    private final ModelService modelService;

    private final ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(DeviceTypeController.class);

    public ModelController(ModelService modelService, ModelMapper modelMapper) {
        this.modelService = modelService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<ModelDTO> getAllModels() {
        return modelService.getAllModels().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ModelDTO getModelById(@PathVariable Long id) {
        return convertToDTO(modelService.getModelById(id));
    }

    @PostMapping
    public ResponseEntity<HttpStatus> createModel(@RequestBody @Valid ModelDTO modelDTO, BindingResult bindingResult){
        Model model = convertToEntity(modelDTO);

        if (modelService.getModel(model).isPresent()){
            logger.warn("Такая модель уже есть");
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Возврат статуса 409 Conflict
        }

        if (bindingResult.hasErrors()) {
            logger.error("Неверное тело запроса: {}", bindingResult.getAllErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Возврат статуса 400 Bad Request
        }
        modelService.saveModel(model);

        return ResponseEntity.ok(HttpStatus.OK);

    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> updateModel(@PathVariable Long id, @RequestBody @Valid ModelDTO modelDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Неверное тело запроса: {}", bindingResult.getAllErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Возврат статуса 400 Bad Request
        }

        Model existingModel = modelService.getModelById(id);
        if (existingModel == null) {
            logger.warn("Модель с ID {} не найден", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Возврат статуса 404 Not Found
        }

        Model updatedModel = convertToEntity(modelDTO);
        updatedModel.setId(id);
        modelService.saveModel(updatedModel);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteModel(@PathVariable Long id) {
        Model existingModel = modelService.getModelById(id);
        if (existingModel == null) {
            logger.warn("Модель с ID {} не найдена", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Возврат статуса 404 Not Found
        }

        modelService.deleteModel(id);
        logger.info("Модель с ID {} успешно удалена", id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/search")
    public List<ModelDTO> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String deviceName,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice) {

        return modelService.findAllByFilters(name, deviceName, color, minPrice, maxPrice).stream().map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/sortByName")
    public List<ModelDTO> sortByName(){
        return modelService.findAllSortedByName().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/sortByPrice")
    public List<ModelDTO> sortByPrice(){
        return modelService.findAllSortedByPrice().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ModelDTO convertToDTO(Model model) {
        ModelDTO modelDTO = modelMapper.map(model, ModelDTO.class);
        if(model.getDevice() != null)
            modelDTO.setDeviceDTO(modelMapper.map(model.getDevice(), DeviceDTO.class));
        if(model.getSize() != null)
            modelDTO.setSizeDTO(modelMapper.map(model.getSize(), SizeDTO.class));

        return modelDTO;
    }

    public Model convertToEntity(ModelDTO modelDTO) {
        return modelMapper.map(modelDTO, Model.class);
    }
}
