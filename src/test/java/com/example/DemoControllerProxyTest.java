package com.example;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.ToxiproxyContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    classes = DemoApplication.class,
    webEnvironment = WebEnvironment.RANDOM_PORT
)
@Testcontainers
public class DemoControllerProxyTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    DemoRepository demoRepository;

    private static final Network network = Network.newNetwork();

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres")
            .withNetwork(network)
            .withNetworkAliases("postgres");
    @Container
    private static final ToxiproxyContainer toxiproxy = new ToxiproxyContainer("ghcr.io/shopify/toxiproxy:2.5.0")
            .withNetwork(network);

    private static Proxy postgresqlProxy;

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) throws IOException {

        var toxiproxyClient = new ToxiproxyClient(toxiproxy.getHost(), toxiproxy.getControlPort());
        postgresqlProxy = toxiproxyClient.createProxy("postgresql", "0.0.0.0:8666", "postgres:5432");
        var jdbcUrl = "jdbc:postgresql://%s:%d/%s".formatted(toxiproxy.getHost(), toxiproxy.getMappedPort(8666), postgres.getDatabaseName());

        registry.add("spring.datasource.url", () -> jdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    public static void startContainer() {
        postgres.start();
    }


    @Test
    public void simpleJPATest() throws IOException {
        DemoEntity demoEntity = new DemoEntity();
        demoEntity.setName("Some value");
        demoRepository.save(demoEntity);

        postgresqlProxy.toxics().latency("postgresql-latency", ToxicDirection.DOWNSTREAM, 1600).setJitter(100);

        DemoEntity result = restTemplate.getForObject("/" + demoEntity.getId(), DemoEntity.class);

        assertThat(result.getName()).as("value is set").isEqualTo("Some value");
    }
}
