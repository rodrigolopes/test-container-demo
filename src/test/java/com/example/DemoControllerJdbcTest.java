package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(
        classes = DemoApplication.class,
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = { "spring.datasource.url=jdbc:tc:postgresql:15.2-alpine:///databasename" }
    )
@ActiveProfiles("test")
public class DemoControllerJdbcTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    DemoRepository demoRepository;

    @Test
    public void simpleJPATest() {
        DemoEntity demoEntity = new DemoEntity();
        demoEntity.setName("Some value");
        demoRepository.save(demoEntity);

        DemoEntity result = restTemplate.getForObject("/" + demoEntity.getId(), DemoEntity.class);

        assertThat(result.getName()).as("value is set").isEqualTo("Some value");
    }
}
