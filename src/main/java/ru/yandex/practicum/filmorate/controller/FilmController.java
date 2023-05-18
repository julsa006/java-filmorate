package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {

    private final FilmService service;

    @GetMapping
    public List<Film> findAll() {
        return service.getAllFilms();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        Film newFilm = service.createFilm(film);
        log.info("Film added: {}", newFilm);
        return newFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        service.updateFilm(film);
        log.info("Film updated: {}", film);
        return service.getFilm(film.getId());
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) {
        return service.getFilm(id);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        service.like(id, userId);
        log.info("User {} like film {}", userId, id);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        service.removeLike(id, userId);
        log.info("User {} remove like from film {}", userId, id);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") Integer count) {
        return service.getPopular(count);
    }


}
