package ru.adamishhe.equipmentregistertest.service;


import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.adamishhe.equipmentregistertest.model.Model;
import ru.adamishhe.equipmentregistertest.model.television.Television;
import ru.adamishhe.equipmentregistertest.repository.TelevisionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TelevisionService {
    private final TelevisionRepository televisionRepository;
    private final ModelService modelService;

    public TelevisionService(TelevisionRepository televisionRepository, ModelService modelService) {
        this.televisionRepository = televisionRepository;
        this.modelService = modelService;
    }


    public List<Television> getAllTelevisions(){return televisionRepository.findAll();}


    public Television getTelevisionById(Long id) {
        Optional<Television> television = televisionRepository.findById(id);
        return television.orElseThrow(EntityNotFoundException::new);
    }


    public Optional<Television> getTelevision(Television television) {
        Optional<Model> modelOptional = Optional.empty();
        if(television.getModel() != null)
            modelOptional = modelService.getModel(television.getModel());

        return televisionRepository.findOneByModelAndCategoryAndTechnology(
                modelOptional.orElse(null),
                television.getCategory(),
                television.getTechnology()
        );
    }

    public void saveTelevision(Television television) {
        Optional<Television> televisionFromDatabase = getTelevision(television);

        if(televisionFromDatabase.isEmpty()){
            modelService.saveModel(television.getModel());
            televisionRepository.save(television);
        }
    }

    public void deleteTelevision(Long id) {
        televisionRepository.findById(id)
                .ifPresentOrElse(
                        television -> televisionRepository.deleteById(id),
                        () -> { throw new EntityNotFoundException("TV with ID " + id + " not found."); }
                );
    }


    public List<Television> findAllByFilters(String name, String color, Long minPrice, Long maxPrice, String category, String technology){
        Specification<Television> spec = Specification.where(null);

        if(name != null && !name.isEmpty()){
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Television, Model> join = root.join("model", JoinType.INNER);
                return criteriaBuilder.like(criteriaBuilder.lower(join.get("name")), "%" + name.toLowerCase() + "%");
            });
        }
        if (color != null && !color.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Television, Model> join = root.join("model", JoinType.INNER);
                return criteriaBuilder.equal(criteriaBuilder.lower(join.get("color")), color.toLowerCase());
            });
        }
        if (minPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Television, Model> join = root.join("model", JoinType.INNER);
                return criteriaBuilder.greaterThanOrEqualTo(join.get("price"), minPrice);
            });
        }
        if (maxPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Television, Model> join = root.join("model", JoinType.INNER);
                return criteriaBuilder.lessThanOrEqualTo(join.get("price"), maxPrice);
            });
        }
        if(category != null && !category.isEmpty()){
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(criteriaBuilder.lower(root.get("category")), category.toLowerCase()));
        }
        if(technology != null && !technology.isEmpty()){
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(criteriaBuilder.lower(root.get("technology")), technology.toLowerCase()));
        }
        return televisionRepository.findAll(spec);
    }
}
