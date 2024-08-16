package ru.adamishhe.equipmentregistertest.controllers;


import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.adamishhe.equipmentregistertest.dto.ComputerDTO;
import ru.adamishhe.equipmentregistertest.dto.DeviceDTO;
import ru.adamishhe.equipmentregistertest.dto.ModelDTO;
import ru.adamishhe.equipmentregistertest.dto.SizeDTO;
import ru.adamishhe.equipmentregistertest.model.Device;
import ru.adamishhe.equipmentregistertest.model.Model;
import ru.adamishhe.equipmentregistertest.model.computer.Computer;
import ru.adamishhe.equipmentregistertest.model.util.Size;
import ru.adamishhe.equipmentregistertest.service.ComputerService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/computers")
public class ComputerController {

    private final ComputerService computerService;
    private final ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(DeviceTypeController.class);

    public ComputerController(ComputerService computerService, ModelMapper modelMapper) {
        this.computerService = computerService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<ComputerDTO> getAllComputers(){
        return computerService.getAllComputers().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ComputerDTO getById(@PathVariable("id") Long id){
        return convertToDTO(computerService.getComputerById(id));
    }

    @PostMapping
    public ResponseEntity<HttpStatus> createComputer(@RequestBody @Valid ComputerDTO computerDTO, BindingResult bindingResult){
        Computer computer = convertToEntity(computerDTO);

        if(bindingResult.hasErrors()){
            logger.error("Неверное тело запроса: {}", bindingResult.getAllErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Возврат статуса 400 Bad Request
        }

        computerService.saveComputer(computer);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> updateComputer(@PathVariable Long id, @RequestBody @Valid ComputerDTO computerDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Неверное тело запроса: {}", bindingResult.getAllErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Возврат статуса 400 Bad Request
        }

        Computer existingComputer = computerService.getComputerById(id);
        if (existingComputer == null) {
            logger.warn("Компьютер с ID {} не найден", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Возврат статуса 404 Not Found
        }

        Computer updatedComputer = convertToEntity(computerDTO);
        updatedComputer.setId(id);
        computerService.saveComputer(updatedComputer);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteComputer(@PathVariable Long id) {
        Computer existingComputer = computerService.getComputerById(id);
        if (existingComputer == null) {
            logger.warn("Компьютер с ID {} не найден", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Возврат статуса 404 Not Found
        }

        computerService.deleteComputer(id);
        logger.info("Компьютер с ID {} успешно удален", id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/search")
    public List<ComputerDTO> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String processorType) {

        return computerService.findAllByFilters(name, color, minPrice, maxPrice, category, processorType).stream().map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ComputerDTO convertToDTO(Computer computer) {
        ComputerDTO computerDTO = modelMapper.map(computer, ComputerDTO.class);
        Model model = computer.getModel();
        if(model != null) {
            computerDTO.setModelDTO(modelMapper.map(model, ModelDTO.class));

            Size size = model.getSize();
            if(size != null)
                computerDTO.getModelDTO().setSizeDTO(modelMapper.map(size, SizeDTO.class));

            Device device = model.getDevice();
            if(device != null)
                computerDTO.getModelDTO().setDeviceDTO(modelMapper.map(device, DeviceDTO.class));
        }

        return computerDTO;
    }

    public Computer convertToEntity(ComputerDTO computerDTO) {
        return modelMapper.map(computerDTO, Computer.class);
    }

}
