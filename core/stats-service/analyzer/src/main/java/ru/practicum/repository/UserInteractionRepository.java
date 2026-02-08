package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.RepositoryResponse;
import ru.practicum.model.UserInteraction;

import java.util.List;
import java.util.Optional;

public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {
    Optional<UserInteraction> findByEventIdAndUserId(Long eventId, Long userId);

    @Query("""
            SELECT new ru.practicum.model.RepositoryResponse(i.eventId, sum(i.rating))
              FROM UserInteraction i
             WHERE i.eventId IN (:eventIds)
             GROUP BY i.eventId
            """)
    List<RepositoryResponse> getInteractionsSumByEventIds(@Param("eventIds") List<Long> eventIds);
}