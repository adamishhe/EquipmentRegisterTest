package ru.adamishhe.equipmentregistertest.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.adamishhe.equipmentregistertest.model.Model;
import ru.adamishhe.equipmentregistertest.model.television.Category;
import ru.adamishhe.equipmentregistertest.model.television.Technology;
import ru.adamishhe.equipmentregistertest.model.television.Television;

import java.util.List;
import java.util.Optional;

@Repository
public interface TelevisionRepository extends JpaRepository<Television, Long> {
    Optional<Television> findOneByModelAndCategoryAndTechnology(Model model, Category category, Technology technology);

    List<Television> findAll(Specification<Television> specification);

}
