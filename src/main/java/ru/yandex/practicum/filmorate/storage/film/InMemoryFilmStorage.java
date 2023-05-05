package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int currentId = 0;

    @Override
    public Collection<Film> findAll() {
        return films.values();
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User %s not found", film.getId()));
        }
        films.put(film.getId(), film);
    }

    @Override
    public Film get(Integer id) {
        return films.get(id);
    }

    @Override
    public boolean contains(Integer id) {
        return films.containsKey(id);
    }
}
