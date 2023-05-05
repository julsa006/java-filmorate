package ru.yandex.practicum.filmorate.storage.film;

public interface LikeStorage {
    void add(Integer filmId, Integer userId);

    void remove(Integer filmId, Integer userId);

    int getNumberOfLikes(Integer filmId);
}
