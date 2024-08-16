package ru.adamishhe.equipmentregistertest.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.adamishhe.equipmentregistertest.model.Device;
import ru.adamishhe.equipmentregistertest.model.Model;
import ru.adamishhe.equipmentregistertest.model.util.Size;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {
    Optional<Model> findOneByDeviceAndNameAndColorAndSizeAndPriceAndStockAvailable(Device device, String name, String color, Size size,
                                                                                   Long price, Boolean stockAvailable);

    List<Model> findAllByOrderByName();

    List<Model> findAllByOrderByPrice();

    List<Model> findAll(Specification<Model> specification);

}
