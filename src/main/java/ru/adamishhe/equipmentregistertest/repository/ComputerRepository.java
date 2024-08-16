package ru.adamishhe.equipmentregistertest.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.adamishhe.equipmentregistertest.model.Model;
import ru.adamishhe.equipmentregistertest.model.computer.Category;
import ru.adamishhe.equipmentregistertest.model.computer.Computer;
import ru.adamishhe.equipmentregistertest.model.computer.ProcessorType;
import ru.adamishhe.equipmentregistertest.model.television.Television;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComputerRepository extends JpaRepository<Computer, Long> {
    Optional<Computer> findOneByModelAndCategoryAndProcessorType(Model model, Category category, ProcessorType processorType);

    List<Computer> findAll(Specification<Computer> specification);

}
