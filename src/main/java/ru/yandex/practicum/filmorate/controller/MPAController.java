package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MPAService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@AllArgsConstructor
public class MPAController {

    private final MPAService service;

    @GetMapping
    public List<MPA> findAll() {
        return service.getAllMPAs();
    }


    @GetMapping("/{id}")
    public MPA getUser(@PathVariable Integer id) {
        return service.getMPA(id);
    }
}
