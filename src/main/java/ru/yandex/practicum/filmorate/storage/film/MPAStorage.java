package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

public interface MPAStorage {
    List<MPA> findAll();

    MPA get(Integer id);
}
