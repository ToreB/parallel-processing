package no.toreb.parallelprocessing.repository;

import no.toreb.parallelprocessing.domain.Command;
import no.toreb.parallelprocessing.domain.CommandType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommandRepository extends ReactiveCrudRepository<Command, Long> {

    @Query("select count(1) = 0 from command where completed = false")
    Mono<Boolean> areAllCommandsCompleted();

    @Query("select * from command " +
           "where type = :commandType " +
           "and completed = false " +
           "order by created_at, id " +
           "limit :limit")
    Flux<Command> findUncompletedByType(final CommandType commandType, final int limit);
}
