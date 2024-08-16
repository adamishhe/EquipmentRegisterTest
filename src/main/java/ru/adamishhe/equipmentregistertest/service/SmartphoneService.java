package ru.adamishhe.equipmentregistertest.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.adamishhe.equipmentregistertest.model.Model;
import ru.adamishhe.equipmentregistertest.model.smartphone.Smartphone;
import ru.adamishhe.equipmentregistertest.repository.SmartphoneRepository;

import java.util.List;
import java.util.Optional;


@Service
public class SmartphoneService {

    private final SmartphoneRepository smartphoneRepository;
    private final ModelService modelService;

    public SmartphoneService(SmartphoneRepository smartphoneRepository, ModelService modelService) {
        this.smartphoneRepository = smartphoneRepository;
        this.modelService = modelService;
    }


    public List<Smartphone> getAllSmartphones(){return smartphoneRepository.findAll();}


    public Smartphone getSmartphoneById(Long id) {
        Optional<Smartphone> smartphone = smartphoneRepository.findById(id);
        return smartphone.orElseThrow(EntityNotFoundException::new);
    }


    public Optional<Smartphone> getSmartphone(Smartphone smartphone) {
        Optional<Model> modelOptional = Optional.empty();
        if(smartphone.getModel() != null)
            modelOptional = modelService.getModel(smartphone.getModel());

        return smartphoneRepository.findOneByModelAndMemoryAndNumberOfCameras(
                modelOptional.orElse(null),
                smartphone.getMemory(),
                smartphone.getNumberOfCameras()
        );
    }

    public void saveSmartphone(Smartphone smartphone) {
        Optional<Smartphone> smartphoneFromDatabase = getSmartphone(smartphone);

        if(smartphoneFromDatabase.isEmpty()){
            modelService.saveModel(smartphone.getModel());
            smartphoneRepository.save(smartphone);
        }
    }

    public void deleteSmartphone(Long id) {
        smartphoneRepository.findById(id)
                .ifPresentOrElse(
                        smartphone -> smartphoneRepository.deleteById(id),
                        () -> { throw new EntityNotFoundException("Smartphone with ID " + id + " not found."); }
                );
    }


    public List<Smartphone> findAllByFilters(String name, String color, Long minPrice, Long maxPrice, Integer minMemory,
                                             Integer maxMemory, Integer minNumberOfCameras, Integer maxNumberOfCameras){
        Specification<Smartphone> spec = Specification.where(null);

        if(name != null && !name.isEmpty()){
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Smartphone, Model> join = root.join("model", JoinType.INNER);
                return criteriaBuilder.like(criteriaBuilder.lower(join.get("name")), "%" + name.toLowerCase() + "%");
            });
        }
        if (color != null && !color.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Smartphone, Model> join = root.join("model", JoinType.INNER);
                return criteriaBuilder.equal(criteriaBuilder.lower(join.get("color")), color.toLowerCase());
            });
        }
        if (minPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Smartphone, Model> join = root.join("model", JoinType.INNER);
                return criteriaBuilder.greaterThanOrEqualTo(join.get("price"), minPrice);
            });
        }
        if (maxPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Smartphone, Model> join = root.join("model", JoinType.INNER);
                return criteriaBuilder.lessThanOrEqualTo(join.get("price"), maxPrice);
            });
        }
        if(minMemory != null){
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("memory"), minMemory));
        }
        if(maxMemory != null){
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("memory"), maxMemory));
        }
        if(minNumberOfCameras != null){
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("numberOfCameras"), minNumberOfCameras));
        }
        if(maxNumberOfCameras != null){
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("numberOfCameras"), maxNumberOfCameras));
        }

        System.out.println(spec.toString());

        return smartphoneRepository.findAll(spec);
    }

}
