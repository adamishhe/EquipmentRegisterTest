package ru.adamishhe.equipmentregistertest.service;


import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.adamishhe.equipmentregistertest.model.Model;
import ru.adamishhe.equipmentregistertest.model.vacuumCleaner.VacuumCleaner;
import ru.adamishhe.equipmentregistertest.repository.VacuumCleanerRepository;

import java.util.List;
import java.util.Optional;

@Service
public class VacuumCleanerService {

    private final VacuumCleanerRepository vacuumCleanerRepository;
    private final ModelService modelService;

    public VacuumCleanerService(VacuumCleanerRepository vacuumCleanerRepository, ModelService modelService) {
        this.vacuumCleanerRepository = vacuumCleanerRepository;
        this.modelService = modelService;
    }


    public List<VacuumCleaner> getAllVacuumCleaners(){return vacuumCleanerRepository.findAll();}


    public VacuumCleaner getVacuumCleanerById(Long id) {
        Optional<VacuumCleaner> vacuumCleaner = vacuumCleanerRepository.findById(id);
        return vacuumCleaner.orElseThrow(EntityNotFoundException::new);
    }


    public Optional<VacuumCleaner> getVacuumCleaner(VacuumCleaner vacuumCleaner) {
        Optional<Model> modelOptional = Optional.empty();
        if(vacuumCleaner.getModel() != null)
            modelOptional = modelService.getModel(vacuumCleaner.getModel());

        return vacuumCleanerRepository.findOneByModelAndVolumeOfDustCollectorAndNumberOfModes(
                modelOptional.orElse(null),
                vacuumCleaner.getVolumeOfDustCollector(),
                vacuumCleaner.getNumberOfModes()
        );
    }

    public void saveVacuumCleaner(VacuumCleaner vacuumCleaner) {
        Optional<VacuumCleaner> vacuumCleanerFromDatabase = getVacuumCleaner(vacuumCleaner);

        if(vacuumCleanerFromDatabase.isEmpty()){
            modelService.saveModel(vacuumCleaner.getModel());
            vacuumCleanerRepository.save(vacuumCleaner);
        }
    }

    public void deleteVacuumCleaner(Long id) {
        vacuumCleanerRepository.findById(id)
                .ifPresentOrElse(
                        smartphone -> vacuumCleanerRepository.deleteById(id),
                        () -> { throw new EntityNotFoundException("Vacuum Cleaner with ID " + id + " not found."); }
                );
    }


    public List<VacuumCleaner> findAllByFilters(String name, String color, Long minPrice, Long maxPrice, Integer minVolumeOfDustCollector,
                                                Integer maxVolumeOfDustCollector, Integer minNumberOfModes, Integer maxNumberOfModes){
        Specification<VacuumCleaner> spec = Specification.where(null);

        if(name != null && !name.isEmpty()){
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<VacuumCleaner, Model> join = root.join("model", JoinType.INNER);
                return criteriaBuilder.like(criteriaBuilder.lower(join.get("name")), "%" + name.toLowerCase() + "%");
            });
        }
        if (color != null && !color.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<VacuumCleaner, Model> join = root.join("model", JoinType.INNER);
                return criteriaBuilder.equal(criteriaBuilder.lower(join.get("color")), color.toLowerCase());
            });
        }
        if (minPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<VacuumCleaner, Model> join = root.join("model", JoinType.INNER);
                return criteriaBuilder.greaterThanOrEqualTo(join.get("price"), minPrice);
            });
        }
        if (maxPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<VacuumCleaner, Model> join = root.join("model", JoinType.INNER);
                return criteriaBuilder.lessThanOrEqualTo(join.get("price"), maxPrice);
            });
        }
        if(minVolumeOfDustCollector != null){
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("volumeOfDustCollector"), minVolumeOfDustCollector));
        }
        if(maxVolumeOfDustCollector != null){
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("volumeOfDustCollector"), maxVolumeOfDustCollector));
        }
        if(minNumberOfModes != null){
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("numberOfModes"), minNumberOfModes));
        }
        if(maxNumberOfModes != null){
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("numberOfModes"), maxNumberOfModes));
        }

        return vacuumCleanerRepository.findAll(spec);
    }
}
