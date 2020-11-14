package no.toreb.parallelprocessing.domain;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
public class Event {

    @Id
    Long id;

    String itemId;

    EventType type;

    String data;

    @CreatedDate
    LocalDateTime createdAt;

    @CreatedBy
    String createdBy;
}
