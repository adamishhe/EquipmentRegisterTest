package ru.adamishhe.equipmentregistertest.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.adamishhe.equipmentregistertest.model.Model;
import ru.adamishhe.equipmentregistertest.model.smartphone.Smartphone;
import ru.adamishhe.equipmentregistertest.model.television.Television;

import java.util.List;
import java.util.Optional;

@Repository
public interface SmartphoneRepository extends JpaRepository<Smartphone, Long> {
    Optional<Smartphone> findOneByModelAndMemoryAndNumberOfCameras(Model model, Integer memory, Integer numberOfCameras);

    List<Smartphone> findAll(Specification<Smartphone> specification);
}
