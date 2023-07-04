package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DemoController {

    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping("/{id}")
    public DemoEntity getDemoEntity(@PathVariable("id") Long id) {
        return demoService.getDemoEntity(id);
    }

    @GetMapping
    public List<DemoEntity> getDemoEntities() {
        return demoService.getDemoEntities();
    }

    @PostMapping
    public DemoEntity createDemoEntity(@RequestBody DemoEntity entity) {
        return demoService.saveEntity(entity);
    }
}
