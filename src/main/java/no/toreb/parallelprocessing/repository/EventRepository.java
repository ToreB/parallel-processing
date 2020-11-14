package no.toreb.parallelprocessing.repository;

import no.toreb.parallelprocessing.domain.Event;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface EventRepository extends ReactiveCrudRepository<Event, Long> {

    @Query("select count(1) = 1 " +
           "from event e " +
           "where e.item_id = :itemId " +
           "and e.type = 'ITEM_DATA1_FETCHED' " +
           "and exists (" +
           "    select 1 " +
           "    from event e2 " +
           "    where e2.item_id = e.item_id " +
           "    and e2.type = 'ITEM_DATA2_FETCHED' " +
           ")")
    Mono<Boolean> isItemCompleted(String itemId);
}
