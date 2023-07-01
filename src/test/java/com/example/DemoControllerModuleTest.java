package com.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    classes = DemoApplication.class,
    webEnvironment = WebEnvironment.RANDOM_PORT
)
public class DemoControllerModuleTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    DemoRepository demoRepository;

    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
    }

    @BeforeAll
    public static void startContainer() {
        postgresContainer.start();
    }


    @Test
    public void simpleJPATest() {
        DemoEntity demoEntity = new DemoEntity();
        demoEntity.setName("Some value");
        demoRepository.save(demoEntity);

        DemoEntity result = restTemplate.getForObject("/" + demoEntity.getId(), DemoEntity.class);

        assertThat(result.getName()).as("value is set").isEqualTo("Some value");
    }
}
