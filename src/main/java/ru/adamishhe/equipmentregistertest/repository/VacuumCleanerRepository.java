package ru.adamishhe.equipmentregistertest.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.adamishhe.equipmentregistertest.model.Model;
import ru.adamishhe.equipmentregistertest.model.television.Television;
import ru.adamishhe.equipmentregistertest.model.vacuumCleaner.VacuumCleaner;

import java.util.List;
import java.util.Optional;

@Repository
public interface VacuumCleanerRepository extends JpaRepository<VacuumCleaner, Long> {

    Optional<VacuumCleaner> findOneByModelAndVolumeOfDustCollectorAndNumberOfModes(Model model,
                                                                                   Integer volumeOfDustCollector,
                                                                                   Integer numberOfModes);
    List<VacuumCleaner> findAll(Specification<VacuumCleaner> specification);


}
