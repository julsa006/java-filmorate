package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();
    private int maxId = 0;

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validate(film, false);
        Film newFilm = film.toBuilder().id(++maxId).build();
        films.put(newFilm.getId(), newFilm);
        log.info("Film added: {}", newFilm);
        return newFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        validate(film, true);
        if (!films.containsKey(film.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User %s not found", film.getId()));
        }
        films.put(film.getId(), film);
        log.info("Film updated: {}", film);
        return film;
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
