package ru.adamishhe.equipmentregistertest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.adamishhe.equipmentregistertest.model.util.Size;

import java.util.Optional;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {
    Optional<Size> findOneByLengthAndWidthAndHeight(int length, int width, int height);
}
