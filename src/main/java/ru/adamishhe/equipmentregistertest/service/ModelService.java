package ru.adamishhe.equipmentregistertest.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.adamishhe.equipmentregistertest.model.Device;
import ru.adamishhe.equipmentregistertest.model.Model;
import ru.adamishhe.equipmentregistertest.model.util.Size;
import ru.adamishhe.equipmentregistertest.repository.ModelRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ModelService {

    private final DeviceService deviceService;

    private final ModelRepository modelRepository;

    private final SizeService sizeService;

    public ModelService(DeviceService deviceService, ModelRepository modelRepository, SizeService sizeService) {
        this.deviceService = deviceService;
        this.modelRepository = modelRepository;
        this.sizeService = sizeService;
    }

    public List<Model> getAllModels() {
        return modelRepository.findAll();
    }

    public Model getModelById(Long id) {
        Optional<Model> model = modelRepository.findById(id);
        return model.orElseThrow(EntityNotFoundException::new);
    }

    public Optional<Model> getModel(Model model) {
        Optional<Size> sizeOptional = Optional.empty();
        Optional<Device> deviceOptional = Optional.empty();
        if(model.getSize() != null) {
            sizeOptional = sizeService.getSize(model.getSize());
        }
        if(model.getDevice() != null) {
            deviceOptional = deviceService.getDevice(model.getDevice());
        }

        return modelRepository.findOneByDeviceAndNameAndColorAndSizeAndPriceAndStockAvailable(
                deviceOptional.orElse(null),
                model.getName(),
                model.getColor(),
                sizeOptional.orElse(null),
                model.getPrice(),
                model.getStockAvailable()
        );
    }

    public void saveModel(Model model) {
        Optional<Model> modelFromDatabase = getModel(model);
        if(modelFromDatabase.isPresent()) {
            model.setId(modelFromDatabase.get().getId());
            model.setSerialNumber(modelFromDatabase.get().getSerialNumber());
        } else {
            sizeService.save(model.getSize());
            deviceService.saveDevice(model.getDevice());

            String uniqueSerialNumber = UUID.randomUUID().toString();
            model.setSerialNumber(uniqueSerialNumber);

            modelRepository.save(model);
        }
    }

    public void deleteModel(Long id) { ///!!!!
        modelRepository.findById(id)
                .ifPresentOrElse(
                        model -> modelRepository.deleteById(id),
                        () -> { throw new EntityNotFoundException("Model with ID " + id + " not found."); }
                );
    }


    public List<Model> findAllByFilters(String name, String deviceName, String color, Long minPrice, Long maxPrice){
        Specification<Model> spec = Specification.where(null);

        if (name != null && !name.isEmpty()){
            spec = spec.and(((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%")));
        }
        if (deviceName != null && !deviceName.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Model, Device> join = root.join("technic", JoinType.INNER);
                return criteriaBuilder.like(criteriaBuilder.lower(join.get("name")), "%" + deviceName.toLowerCase() + "%");
            });
        }
        if (color != null && !color.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(criteriaBuilder.lower(root.get("color")), color.toLowerCase()));
        }
        if (minPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
        }

        if (maxPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        return modelRepository.findAll(spec);
    }

    public List<Model> findAllSortedByName(){
        return modelRepository.findAllByOrderByName();
    }

    public List<Model> findAllSortedByPrice(){
        return modelRepository.findAllByOrderByPrice();
    }

}
