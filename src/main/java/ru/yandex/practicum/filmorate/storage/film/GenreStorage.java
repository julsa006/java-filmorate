package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    List<Genre> findAll();

    Genre get(Integer id);
}
