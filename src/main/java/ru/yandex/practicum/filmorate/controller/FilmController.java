package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final FilmService service;

    @Autowired
    public FilmController(FilmService service) {
        this.service = service;
    }


    @GetMapping
    public Collection<Film> findAll() {
        return service.getAllFilms();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validate(film, false);
        Film newFilm = service.createFilm(film);
        log.info("Film added: {}", newFilm);
        return newFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        validate(film, true);
        service.updateFilm(film);
        log.info("Film updated: {}", film);
        return film;
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
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10") Integer count) {
        return service.getPopular(count);
    }


    private void validate(Film film, boolean requireId) {
        if (requireId && (film.getId() == null)) {
            throw new ValidationException("Id cannot be null");
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException(String.format("Release date must be after %s", MIN_RELEASE_DATE));
        }
    }

}
