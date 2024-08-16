package ru.adamishhe.equipmentregistertest.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.adamishhe.equipmentregistertest.model.Model;
import ru.adamishhe.equipmentregistertest.model.fridge.CompressorType;
import ru.adamishhe.equipmentregistertest.model.fridge.Fridge;
import ru.adamishhe.equipmentregistertest.model.television.Television;

import java.util.List;
import java.util.Optional;

@Repository
public interface FridgeRepository extends JpaRepository<Fridge, Long> {
    Optional<Fridge> findOneByModelAndNumberOfDoorsAndCompressorType(Model model, Integer numberOfDoors, CompressorType compressorType);

    List<Fridge> findAll(Specification<Fridge> specification);
}
