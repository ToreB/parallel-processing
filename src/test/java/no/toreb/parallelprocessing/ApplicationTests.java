package no.toreb.parallelprocessing;

import no.toreb.parallelprocessing.domain.Command;
import no.toreb.parallelprocessing.domain.CommandType;
import no.toreb.parallelprocessing.domain.Event;
import no.toreb.parallelprocessing.domain.EventType;
import no.toreb.parallelprocessing.repository.CommandRepository;
import no.toreb.parallelprocessing.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = ApplicationRunner.class,
        properties = {
                "spring.security.user.name=theUser",
                "spring.security.user.password=thePass",
                "spring.security.user.roles=user",
                "spring.datasource.url=jdbc:h2:mem:testdb",
                "spring.r2dbc.url=r2dbc:h2:mem:///testdb",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "app.scheduled-tasks.enable=false"
        })
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DirtiesContext
class ApplicationTests {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CommandRepository commandRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void contextLoads() {
    }

    @Test
    void dataR2dbcWorks() {
        eventRepository.save(Event.builder()
                                  .type(EventType.ITEM_DATA1_FETCHED)
                                  .itemId("1")
                                  .data("Event data")
                                  .build())
                       .block();

        eventRepository.findAll()
                       .count()
                       .doOnSuccess(count -> assertThat(count).isOne())
                       .block();

        commandRepository.save(Command.builder()
                                      .type(CommandType.FETCH_ITEM_DATA1)
                                      .itemId("1")
                                      .data("Command data")
                                      .build())
                         .block();

        commandRepository.findAll()
                         .count()
                         .doOnSuccess(count -> assertThat(count).isOne())
                         .block();
    }

}
