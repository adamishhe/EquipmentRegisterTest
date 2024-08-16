package ru.adamishhe.equipmentregistertest.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.adamishhe.equipmentregistertest.model.Model;
import ru.adamishhe.equipmentregistertest.model.computer.Computer;
import ru.adamishhe.equipmentregistertest.repository.ComputerRepository;

import java.util.List;
import java.util.Optional;


@Service
public class ComputerService {
    private final ComputerRepository computerRepository;
    private final ModelService modelService;

    public ComputerService(ComputerRepository computerRepository, ModelService modelService) {
        this.computerRepository = computerRepository;
        this.modelService = modelService;
    }


    public List<Computer> getAllComputers(){return computerRepository.findAll();}


    public Computer getComputerById(Long id) {
        Optional<Computer> computer = computerRepository.findById(id);
        return computer.orElseThrow(EntityNotFoundException::new);
    }


    public Optional<Computer> getComputer(Computer computer) {
        Optional<Model> modelOptional = Optional.empty();
        if(computer.getModel() != null)
            modelOptional = modelService.getModel(computer.getModel());

        return computerRepository.findOneByModelAndCategoryAndProcessorType(
                modelOptional.orElse(null),
                computer.getCategory(),
                computer.getProcessorType()
        );
    }

    public void saveComputer(Computer computer) {
        Optional<Computer> computerFromDatabase = getComputer(computer);

        if(computerFromDatabase.isEmpty()){
            modelService.saveModel(computer.getModel());
            computerRepository.save(computer);
        }
    }

    public void deleteComputer(Long id) {
        computerRepository.findById(id)
                .ifPresentOrElse(
                        computer -> computerRepository.deleteById(id),
                        () -> { throw new EntityNotFoundException("PC with ID " + id + " not found."); }
                );
    }


    public List<Computer> findAllByFilters(String name, String color, Long minPrice, Long maxPrice, String category, String processorType){
        Specification<Computer> spec = Specification.where(null);

        if(name != null && !name.isEmpty()){
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Computer, Model> join = root.join("model", JoinType.INNER);
                return criteriaBuilder.like(criteriaBuilder.lower(join.get("name")), "%" + name.toLowerCase() + "%");
            });
        }
        if (color != null && !color.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Computer, Model> join = root.join("model", JoinType.INNER);
                return criteriaBuilder.equal(criteriaBuilder.lower(join.get("color")), color.toLowerCase());
            });
        }
        if (minPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Computer, Model> join = root.join("model", JoinType.INNER);
                return criteriaBuilder.greaterThanOrEqualTo(join.get("price"), minPrice);
            });
        }
        if (maxPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Computer, Model> join = root.join("model", JoinType.INNER);
                return criteriaBuilder.lessThanOrEqualTo(join.get("price"), maxPrice);
            });
        }
        if(category != null && !category.isEmpty()){
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(criteriaBuilder.lower(root.get("category")), category.toLowerCase()));
        }
        if(processorType != null && !processorType.isEmpty()){
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(criteriaBuilder.lower(root.get("processorType")), processorType.toLowerCase()));
        }
        return computerRepository.findAll(spec);
    }
}
