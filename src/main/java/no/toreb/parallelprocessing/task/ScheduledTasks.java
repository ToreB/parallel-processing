package no.toreb.parallelprocessing.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import no.toreb.parallelprocessing.domain.Command;
import no.toreb.parallelprocessing.domain.CommandType;
import no.toreb.parallelprocessing.domain.Event;
import no.toreb.parallelprocessing.domain.EventType;
import no.toreb.parallelprocessing.repository.CommandRepository;
import no.toreb.parallelprocessing.repository.EventRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "app.scheduled-tasks.enable", havingValue = "true")
public class ScheduledTasks {

    private final EventRepository eventRepository;

    private final CommandRepository commandRepository;

    private final int batchSize = 100;

    @Scheduled(cron = "${app.scheduled-tasks.cron}")
    @SchedulerLock(name = "fetchItemMetadataTask")
    public void fetchItemMetadata() {
        final var areAllCommandsCompleted = commandRepository.areAllCommandsCompleted()
                                                             .block();
        if (areAllCommandsCompleted == null || !areAllCommandsCompleted) {
            log.info("Waiting for existing commands to complete before fetching new items.");
            return;
        }

        log.info("Start fetchItemMetadata.");

        final var eventProducer = Flux.range(1, 500)
                                      .map(num -> {
                                          final String itemId = UUID.randomUUID() + "--" + num;
                                          return Event.builder()
                                                      .type(EventType.ITEM_METADATA_FETCHED)
                                                      .itemId(itemId)
                                                      .data("Item metadata " + itemId)
                                                      .build();
                                      })
                                      .doOnNext(event -> log.info("Created event for item {}.", event.getItemId()));
        eventRepository.saveAll(eventProducer)
                       .flatMap(event -> {
                           final var commandProducer =
                                   Flux.fromStream(Stream.of(CommandType.FETCH_ITEM_DATA1,
                                                             CommandType.FETCH_ITEM_DATA2,
                                                             CommandType.COMPLETE_ITEM))
                                       .map(commandType -> Command.builder()
                                                                  .type(commandType)
                                                                  .itemId(event.getItemId())
                                                                  .data(event.getData())
                                                                  .build());
                           return commandRepository.saveAll(commandProducer)
                                                   .doOnNext(command -> log.info(
                                                           "Created command {} for item {}.",
                                                           command.getType(),
                                                           command.getItemId()));
                       })
                       .blockLast();

        log.info("End fetchItemMetadata.");
    }

    @Scheduled(cron = "${app.scheduled-tasks.cron}")
    @SchedulerLock(name = "fetchItemData1Task")
    public void fetchItemData1() {
        log.info("Start fetchItemData1.");

        final var scheduler = Schedulers.single();
        commandRepository.findUncompletedByType(CommandType.FETCH_ITEM_DATA1, batchSize)
                         .delayElements(Duration.ofSeconds(1), scheduler)
                         .flatMap(command -> Flux.from(
                                 eventRepository.save(Event.builder()
                                                           .type(EventType.ITEM_DATA1_FETCHED)
                                                           .itemId(command.getItemId())
                                                           .data("Item data1" + command.getItemId())
                                                           .build())
                                                .doOnNext(event -> log.info("Event {} saved for item {}.",
                                                                            event.getType(),
                                                                            event.getItemId()))
                                                .then(commandRepository.save(command.toBuilder()
                                                                                    .completed(true)
                                                                                    .build())))
                                                 .doOnNext(command1 -> log.info(
                                                         "Command {} completed for item {}.",
                                                         command1.getType(),
                                                         command1.getItemId())))
                         .blockLast();
        scheduler.dispose();

        log.info("End fetchItemData1.");
    }

    @Scheduled(cron = "${app.scheduled-tasks.cron}")
    @SchedulerLock(name = "fetchItemData2Task")
    public void fetchItemData2() {
        log.info("Start fetchItemData2.");

        final var scheduler = Schedulers.single();
        commandRepository.findUncompletedByType(CommandType.FETCH_ITEM_DATA2, batchSize)
                         .delayElements(Duration.ofSeconds(2), scheduler)
                         .flatMap(command -> Flux.from(
                                 eventRepository.save(Event.builder()
                                                           .type(EventType.ITEM_DATA2_FETCHED)
                                                           .itemId(command.getItemId())
                                                           .data("Item data2" + command.getItemId())
                                                           .build())
                                                .doOnNext(event -> log.info("Event {} saved for item {}.",
                                                                            event.getType(),
                                                                            event.getItemId()))
                                                .then(commandRepository.save(command.toBuilder()
                                                                                    .completed(true)
                                                                                    .build())
                                                                       .doOnNext(command1 -> log.info(
                                                                               "Command {} completed for item" +
                                                                               " {}.",
                                                                               command1.getType(),
                                                                               command1.getItemId())))))
                         .blockLast();
        scheduler.dispose();

        log.info("End fetchItemData2.");
    }

    @Scheduled(cron = "${app.scheduled-tasks.cron}")
    @SchedulerLock(name = "completeItemTask")
    public void completeItem() {
        log.info("Start completeItem.");

        final var scheduler = Schedulers.single();
        commandRepository.findUncompletedByType(CommandType.COMPLETE_ITEM, batchSize)
                         .filterWhen(command -> eventRepository.isItemCompleted(command.getItemId()))
                         .flatMap(command -> Flux.from(
                                 eventRepository.save(Event.builder()
                                                           .type(EventType.ITEM_COMPLETED)
                                                           .itemId(command.getItemId())
                                                           .data("Item completed " + command.getItemId())
                                                           .build())
                                                .doOnNext(event -> log.info("Event {} saved for item {}.",
                                                                            event.getType(),
                                                                            event.getItemId()))
                                                .then(commandRepository.save(command.toBuilder()
                                                                                    .completed(true)
                                                                                    .build())
                                                                       .doOnNext(command1 -> log.info(
                                                                               "Command {} completed for item" +
                                                                               " {}.",
                                                                               command1.getType(),
                                                                               command1.getItemId()))))
                                                 .delayElements(Duration.ofSeconds(1), scheduler))
                         .blockLast();
        scheduler.dispose();

        log.info("End completeItem.");
    }
}
