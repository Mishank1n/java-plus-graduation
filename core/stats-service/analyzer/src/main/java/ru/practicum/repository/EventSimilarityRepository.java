package ru.practicum.repository;


import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.EventSimilarity;
import ru.practicum.model.RepositoryResponse;

import java.util.List;
import java.util.Optional;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {

    Optional<EventSimilarity> findByFirstEventAndSecondEvent(Long firstEvent, Long secondEvent);

    @Query("""
            SELECT new ru.practicum.model.RepositoryResponse(
                     CASE WHEN s.firstEvent = :eventId THEN s.secondEvent ELSE s.firstEvent END,
                     s.similarity)
              FROM EventSimilarity s
             WHERE :eventId IN (s.firstEvent, s.secondEvent)
               AND NOT EXISTS (SELECT 'no user interactions'
                                 FROM UserInteraction i
                                WHERE i.userId = :userId
                                  AND i.eventId = CASE
                                                    WHEN s.firstEvent = :eventId THEN s.secondEvent
                                                    ELSE s.firstEvent
                                                  END)
             ORDER BY s.similarity DESC
            """)
    List<RepositoryResponse> getSimilarEvents(@Param("userId") Long userId, @Param("eventId") Long eventId, Limit limit);

    @Query("""
            SELECT similar_eventId
              FROM (SELECT CASE
                             WHEN s.firstEvent = i.eventId THEN s.secondEvent
                             ELSE s.firstEvent
                           END similar_eventId,
                           MAX(s.similarity) similarity_max
                      FROM UserInteraction i
                           JOIN EventSimilarity s
                             ON i.eventId IN (s.firstEvent, s.secondEvent)
                     WHERE i.userId = :userId
                       AND NOT EXISTS (SELECT 'no user interactions'
                                         FROM UserInteraction i2
                                        WHERE i2.userId = :userId
                                          AND i2.eventId = CASE
                                                             WHEN s.firstEvent = i.eventId THEN s.secondEvent
                                                             ELSE s.firstEvent
                                                           END)
                     GROUP BY CASE
                                WHEN s.firstEvent = i.eventId THEN s.secondEvent
                                ELSE s.firstEvent
                              END)
             ORDER BY similarity_max DESC
            """)
    List<Long> getSimilarEventIdsForUser(@Param("userId") Long userId, Limit limit);

    @Query("""
            SELECT sum(i.rating * s.similarity) / sum(s.similarity)
              FROM EventSimilarity s
                   JOIN UserInteraction i
                     ON i.eventId IN (s.firstEvent, s.secondEvent)
                    AND i.eventId != :eventId
                    AND i.userId = :userId
             WHERE :eventId IN (s.firstEvent, s.secondEvent)
            """)
    Optional<Double> calculateRating(@Param("userId") Long userId, @Param("eventId") Long eventId);
}

