package no.toreb.parallelprocessing;

import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        properties = {
                "spring.security.user.name=theUser",
                "spring.security.user.password=thePass",
                "spring.security.user.roles=user",
                "spring.r2dbc.url=r2dbc:h2:mem:///testdb",
                "spring.r2dbc.username=sa",
                "spring.r2dbc.password=",
                "spring.flyway.url=jdbc:h2:mem:testdb",
        })
class ApplicationTests {

    @FlywayTest
    @BeforeEach
    void setUp() {
    }

    @Test
    void contextLoads() {
    }

}
