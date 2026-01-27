package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Находит все события по ID категории.
     * Spring Data автоматически генерирует запрос по имени метода.
     *
     * @param category ID категории
     * @return список событий данной категории
     */
    List<Event> findByCategory(Long category);

    /**
     * Получает страницы событий, созданных указанным пользователем.
     *
     * @param initiator ID пользователя-инициатора
     * @param page      запрос на пагинацию (PageRequest)
     * @return страница событий
     */
    Page<Event> findByInitiator(Long initiator, PageRequest page);

    List<Event> findByInitiator(Long initiator);

    /**
     * Находит событие по ID, проверяя, что оно принадлежит указанному пользователю.
     * Используем составное условие в имени метода.
     *
     * @param id        ID события
     * @param initiator ID пользователя-инициатора
     * @return Optional<Event> — событие, если найдено и принадлежит пользователю
     */
    Optional<Event> findByIdAndInitiator(Long id, Long initiator);

    /**
     * Находит события по множеству ID.
     *
     * @param ids набор ID событий
     * @return список найденных событий
     */
    List<Event> findByIdIn(Set<Long> ids);

    /**
     * Находит события по множеству ID с жадной загрузкой инициатора.
     * Для JOIN FETCH в Spring Data JPA требуется @Query, поэтому оставляем как есть.
     * Если нужно строго без @Query — этот метод придётся исключить или вынести в отдельный интерфейс.
     *
     * @param ids набор ID событий
     * @return список событий с загруженным полем initiator
     */
    @Query("SELECT e FROM Event e WHERE e.id IN :ids")
    List<Event> findEventsByIdSet(@Param("ids") Set<Long> ids);

    /**
     * Проверяет, существуют ли события с указанной категорией.
     *
     * @param category ID категории
     * @return true, если есть хотя бы одно событие с данной категорией
     */
    boolean existsByCategory(Long category);

}
