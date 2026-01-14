package ru.practicum.client;

import jakarta.annotation.PostConstruct;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.StatisticsGetResponseDto;
import ru.practicum.dto.StatisticsPostResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class StatsClient {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String STATS_SERVICE_NAME = "stats-server"; // должно совпадать с spring.application.name

    private static RestTemplate rest;
    private static DiscoveryClient staticDiscoveryClient;

    private final DiscoveryClient discoveryClient;

    public StatsClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @PostConstruct
    private void initStaticFields() {
        rest = new RestTemplate();
        staticDiscoveryClient = this.discoveryClient;
    }

    private static String getStatsServiceUrl() {
        try {
            ServiceInstance instance = staticDiscoveryClient.getInstances(STATS_SERVICE_NAME).getFirst();
            return instance.getUri().toString();

        } catch (Exception exception) {
            throw new StatsServerUnavailable(
                    "Ошибка получения адреса сервиса статистики: " + STATS_SERVICE_NAME);
        }
    }

    public static List<StatisticsGetResponseDto> getStats(LocalDateTime startTime, LocalDateTime endTime, @Nullable String[] uris, @Nullable Boolean unique) {
        String startString = startTime.format(TIME_FORMAT);
        String endString = endTime.format(TIME_FORMAT);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", startString);
        parameters.put("end", endString);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/stats?start={start}&end={end}");
        if (uris != null) {
            parameters.put("uris", uris);
            stringBuilder.append("&uris={uris}");
        }
        if (unique != null) {
            parameters.put("unique", unique);
            stringBuilder.append("&unique={unique}");
        }
        String fullPath = getStatsServiceUrl() + stringBuilder.toString();
        return makeAndSendGetStatsRequest(HttpMethod.GET, fullPath, parameters, null);
    }

    public static ResponseEntity<String> postHit(StatisticsPostResponseDto hit) {
        ResponseEntity<String> responseEntity = makeAndSendPostHitRequest(HttpMethod.POST, getStatsServiceUrl() + "/hit", null, hit);
        return responseEntity;
    }


    private static <T> List<StatisticsGetResponseDto> makeAndSendGetStatsRequest(HttpMethod method, String path, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<List<StatisticsGetResponseDto>> ewmServerResponse;
        try {
            if (parameters != null) {
                ewmServerResponse = rest.exchange(path, method, requestEntity, new ParameterizedTypeReference<List<StatisticsGetResponseDto>>() {
                }, parameters);
            } else {
                ewmServerResponse = rest.exchange(path, method, requestEntity, new ParameterizedTypeReference<List<StatisticsGetResponseDto>>() {
                });
            }
        } catch (HttpStatusCodeException e) {
            return null;
        }
        return ewmServerResponse.getBody();
    }

    private static <T> ResponseEntity<String> makeAndSendPostHitRequest(HttpMethod method, String path, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<String> ewmServerResponse;
        try {
            if (parameters != null) {
                ewmServerResponse = rest.exchange(path, method, requestEntity, String.class, parameters);
            } else {
                ewmServerResponse = rest.exchange(path, method, requestEntity, String.class);
            }
        } catch (HttpStatusCodeException e) {
            return null;
        }
        return ewmServerResponse;
    }

    private static HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    public static Map<Long, Long> getMapIdViews(Collection<Long> eventsId) {
        if (eventsId == null || eventsId.isEmpty()) {
            return new HashMap<>();
        }
        /*составляем список URI событий из подборки*/
        List<String> eventUris = eventsId.stream()
                .map(i -> "/events/" + i)
                .collect(Collectors.toList()); //преобразовали список событий в список URI

        String[] uriArray = new String[eventUris.size()]; //создали массив строк
        eventUris.toArray(uriArray); //заполнили массив строками из списка URI

        /*запрашиваем у клиента статистики данные по нужным URI*/
        List<StatisticsGetResponseDto> statisticsList = getStats(LocalDateTime.of(1970, 01, 01, 01, 01), LocalDateTime.now(), uriArray, true);

        if (statisticsList == null || statisticsList.isEmpty()) { //если нет статистики по эндпоинтам, возвращаем мапу с нулевыми просмотрами
            return eventsId.stream()
                    .collect(Collectors.toMap(e -> e, e -> 0L));
        }
        /*превращаем список EndpointStats в мапу <id события, кол-во просмотров>*/
        Map<Long, Long> idViewsMap = statisticsList.stream()
                .collect(Collectors.toMap(e -> {
                            String[] splitUri = e.getUri().split("/"); //делим URI /events/1
                            Arrays.asList(splitUri).forEach(s -> System.out.println("idViewsMap + elements+///+ " + s));
                            return Long.valueOf(splitUri[splitUri.length - 1]); //берем последний элемент разбитой строки - это id
                        },
                        StatisticsGetResponseDto::getHits));
        return idViewsMap;
    }
}