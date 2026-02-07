package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_interactions", schema = "public")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "event_id", nullable = false)
    Long eventId;

    @Column(name = "rating", nullable = false)
    Double rating;

    @Column(name = "registration_time", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    LocalDateTime registrationTime;
}
