package ru.adamishhe.equipmentregistertest.service;

import org.springframework.stereotype.Service;
import ru.adamishhe.equipmentregistertest.model.util.Size;
import ru.adamishhe.equipmentregistertest.repository.SizeRepository;

import java.util.Optional;

@Service
public class SizeService {
    private final SizeRepository sizeRepository;

    public SizeService(SizeRepository sizeRepository) {
        this.sizeRepository = sizeRepository;
    }

    public Optional<Size> getSize(Size size){
        return sizeRepository.findOneByLengthAndWidthAndHeight(
                size.getLength(),
                size.getWidth(),
                size.getHeight()
        );
    }

    public void save(Size size){
        if(size != null){
            Optional<Size> sizeFromDatabase = getSize(size);
            if(sizeFromDatabase.isPresent()){
                size.setId(sizeFromDatabase.get().getId());
            } else {
                sizeRepository.save(size);
            }
        }
    }
}
