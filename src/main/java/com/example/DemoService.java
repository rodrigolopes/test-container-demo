package com.example;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoService {

    private final DemoRepository demoRepository;

    public DemoService(DemoRepository demoRepository) {
        this.demoRepository = demoRepository;
    }

    public DemoEntity getDemoEntity(Long id) {
        return demoRepository.findById(id).orElseThrow(() -> new RuntimeException("Entity not found"));
    }

    public DemoEntity saveEntity(DemoEntity entity) {
        return demoRepository.save(entity);
    }

    public List<DemoEntity> getDemoEntities() {
        return demoRepository.findAll();
    }
}
