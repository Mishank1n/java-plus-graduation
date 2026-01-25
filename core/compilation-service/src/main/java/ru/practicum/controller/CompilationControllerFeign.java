package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.service.CompilationService;

@RestController
@RequestMapping("/feign/compilation")
@RequiredArgsConstructor
public class CompilationControllerFeign {

    private final CompilationService service;

    @PostMapping("/event/delete")
    public void deleteEventFromCompilation(@RequestParam("eventId") Long eventId) {
        service.deleteEventFromCompilation(eventId);
    }
}
