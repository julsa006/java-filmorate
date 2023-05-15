package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int currentId = 0;
    @Autowired
    private InMemoryLikeStorage likeStorage;


    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        Film newFilm = film.toBuilder().id(++currentId).build();
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public void update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException(String.format("Film %s not found", film.getId()));
        }
        films.put(film.getId(), film);
    }

    @Override
    public Film get(Integer id) {
        return films.get(id);
    }

    @Override
    public List<Film> getPopular(int count) {
        return films.values().stream()
                .sorted(Comparator.comparing(f -> likeStorage.getNumberOfLikes(f.getId()), Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
