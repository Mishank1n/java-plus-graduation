package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_similarities", schema = "public")
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class EventSimilarity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "event1", nullable = false)
    Long firstEvent;

    @Column(name = "event2", nullable = false)
    Long secondEvent;

    @Column(name = "similarity", nullable = false)
    Double similarity;

    @Column(name = "registration_time", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    LocalDateTime registrationTime;
}
