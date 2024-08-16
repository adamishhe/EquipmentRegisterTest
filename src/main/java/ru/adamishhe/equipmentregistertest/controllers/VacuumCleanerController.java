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
import ru.adamishhe.equipmentregistertest.dto.VacuumCleanerDTO;
import ru.adamishhe.equipmentregistertest.model.Device;
import ru.adamishhe.equipmentregistertest.model.Model;
import ru.adamishhe.equipmentregistertest.model.util.Size;
import ru.adamishhe.equipmentregistertest.model.vacuumCleaner.VacuumCleaner;
import ru.adamishhe.equipmentregistertest.service.VacuumCleanerService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vacuumcleaners")
public class VacuumCleanerController {

    private final VacuumCleanerService vacuumCleanerService;
    private final ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(DeviceTypeController.class);

    public  VacuumCleanerController(VacuumCleanerService vacuumCleanerService, ModelMapper modelMapper) {
        this.vacuumCleanerService = vacuumCleanerService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<VacuumCleanerDTO> getAllVacuumCleaners(){
        return vacuumCleanerService.getAllVacuumCleaners().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public VacuumCleanerDTO getById(@PathVariable("id") Long id){
        return convertToDTO(vacuumCleanerService.getVacuumCleanerById(id));
    }

    @PostMapping
    public ResponseEntity<HttpStatus> createVacuumCleaner(@RequestBody @Valid VacuumCleanerDTO vacuumCleanerDTO, BindingResult bindingResult){
        VacuumCleaner vacuumCleaner = convertToEntity(vacuumCleanerDTO);

        if(bindingResult.hasErrors()){
            logger.error("Неверное тело запроса: {}", bindingResult.getAllErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Возврат статуса 400 Bad Request
        }

        vacuumCleanerService.saveVacuumCleaner(vacuumCleaner);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> updateVacuumCleaner(@PathVariable Long id, @RequestBody @Valid VacuumCleanerDTO vacuumCleanerDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Неверное тело запроса: {}", bindingResult.getAllErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Возврат статуса 400 Bad Request
        }

        VacuumCleaner existingVacuumCleaner = vacuumCleanerService.getVacuumCleanerById(id);
        if (existingVacuumCleaner == null) {
            logger.warn("пылесос с ID {} не найден", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Возврат статуса 404 Not Found
        }

        VacuumCleaner updatedVacuumCleaner = convertToEntity(vacuumCleanerDTO);
        updatedVacuumCleaner.setId(id);
        vacuumCleanerService.saveVacuumCleaner(updatedVacuumCleaner);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteVacuumCleaner(@PathVariable Long id) {
        VacuumCleaner existingVacuumCleaner = vacuumCleanerService.getVacuumCleanerById(id);
        if (existingVacuumCleaner == null) {
            logger.warn("пылесос с ID {} не найден", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Возврат статуса 404 Not Found
        }

        vacuumCleanerService.deleteVacuumCleaner(id);
        logger.info("пылесос с ID {} успешно удален", id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/search")
    public List<VacuumCleanerDTO> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) Integer minVolumeOfDustCollector,
            @RequestParam(required = false) Integer maxVolumeOfDustCollector,
            @RequestParam(required = false) Integer minNumberOfModes,
            @RequestParam(required = false) Integer maxNumberOfModes) {

        return vacuumCleanerService.findAllByFilters(name, color, minPrice, maxPrice, minVolumeOfDustCollector, maxVolumeOfDustCollector,
                minNumberOfModes, maxNumberOfModes).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public VacuumCleanerDTO convertToDTO(VacuumCleaner vacuumCleaner) {
        VacuumCleanerDTO vacuumCleanerDTO = modelMapper.map(vacuumCleaner, VacuumCleanerDTO.class);
        Model model = vacuumCleaner.getModel();
        if(model != null) {
            vacuumCleanerDTO.setModelDTO(modelMapper.map(model, ModelDTO.class));

            Size size = model.getSize();
            if(size != null)
                vacuumCleanerDTO.getModelDTO().setSizeDTO(modelMapper.map(size, SizeDTO.class));

            Device device = model.getDevice();
            if(device != null)
                vacuumCleanerDTO.getModelDTO().setDeviceDTO(modelMapper.map(device, DeviceDTO.class));
        }

        return vacuumCleanerDTO;
    }

    public VacuumCleaner convertToEntity(VacuumCleanerDTO vacuumCleanerDTO) {
        return modelMapper.map(vacuumCleanerDTO, VacuumCleaner.class);
    }

}
