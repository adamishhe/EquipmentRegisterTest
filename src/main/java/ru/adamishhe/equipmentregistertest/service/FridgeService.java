package ru.adamishhe.equipmentregistertest.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.adamishhe.equipmentregistertest.model.Model;
import ru.adamishhe.equipmentregistertest.model.fridge.Fridge;
import ru.adamishhe.equipmentregistertest.repository.FridgeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FridgeService {

    private final FridgeRepository fridgeRepository;
    private final ModelService modelService;

    public FridgeService(FridgeRepository fridgeRepository, ModelService modelService) {
        this.fridgeRepository = fridgeRepository;
        this.modelService = modelService;
    }


    public List<Fridge> getAllFridges(){return fridgeRepository.findAll();}


    public Fridge getFridgeById(Long id) {
        Optional<Fridge> fridge = fridgeRepository.findById(id);
        return fridge.orElseThrow(EntityNotFoundException::new);
    }


    public Optional<Fridge> getFridge(Fridge fridge) {
        Optional<Model> modelOptional = Optional.empty();
        if(fridge.getModel() != null)
            modelOptional = modelService.getModel(fridge.getModel());

        return fridgeRepository.findOneByModelAndNumberOfDoorsAndCompressorType(
                modelOptional.orElse(null),
                fridge.getNumberOfDoors(),
                fridge.getCompressorType()
        );
    }

    public void saveFridge(Fridge fridge) {
        Optional<Fridge> fridgeFromDatabase = getFridge(fridge);

        if(fridgeFromDatabase.isEmpty()){
            modelService.saveModel(fridge.getModel());
            fridgeRepository.save(fridge);
        }
    }

    public void deleteFridge(Long id) {
        fridgeRepository.findById(id)
                .ifPresentOrElse(
                        fridge -> fridgeRepository.deleteById(id),
                        () -> { throw new EntityNotFoundException("Fridge with ID " + id + " not found."); }
                );
    }


    public List<Fridge> findAllByFilters(String name, String color, Long minPrice, Long maxPrice, Integer minNumberOfDoors,
                                               Integer maxNumberOfDoors, String compressorType){
        Specification<Fridge> spec = Specification.where(null);

        if(name != null && !name.isEmpty()){
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Fridge, Model> join = root.join("model", JoinType.INNER);
                return criteriaBuilder.like(criteriaBuilder.lower(join.get("name")), "%" + name.toLowerCase() + "%");
            });
        }
        if (color != null && !color.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Fridge, Model> join = root.join("model", JoinType.INNER);
                return criteriaBuilder.equal(criteriaBuilder.lower(join.get("color")), color.toLowerCase());
            });
        }
        if (minPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Fridge, Model> join = root.join("model", JoinType.INNER);
                return criteriaBuilder.greaterThanOrEqualTo(join.get("price"), minPrice);
            });
        }
        if (maxPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Fridge, Model> join = root.join("model", JoinType.INNER);
                return criteriaBuilder.lessThanOrEqualTo(join.get("price"), maxPrice);
            });
        }
        if(minNumberOfDoors != null){
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("numberOfDoors"), minNumberOfDoors));
        }
        if(maxNumberOfDoors != null){
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("numberOfDoors"), maxNumberOfDoors));
        }
        if(compressorType != null && !compressorType.isEmpty()){
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(criteriaBuilder.lower(root.get("compressorType")), compressorType.toLowerCase()));
        }

        return fridgeRepository.findAll(spec);
    }
}
