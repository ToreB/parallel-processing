package no.toreb.parallelprocessing.domain;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
public class Command {

    @Id
    Long id;

    String itemId;

    CommandType type;

    String data;

    boolean completed;

    @CreatedDate
    LocalDateTime createdAt;

    @CreatedBy
    String createdBy;
}
