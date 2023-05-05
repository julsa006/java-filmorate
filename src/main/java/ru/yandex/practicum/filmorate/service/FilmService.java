package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.LikeStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserService userService;


    @Autowired
    public FilmService(FilmStorage filmStorage, LikeStorage likeStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.userService = userService;
    }

    public Film getFilm(Integer id) {
        checkFilmExist(id);
        return filmStorage.get(id);
    }

    public Film createFilm(Film film) {
        return filmStorage.create(film);
    }

    public void updateFilm(Film film) {
        checkFilmExist(film.getId());
        filmStorage.update(film);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.findAll();
    }

    public void like(Integer filmId, Integer userId) {
        checkFilmExist(filmId);
        userService.checkUserExist(userId);
        likeStorage.add(filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        checkFilmExist(filmId);
        userService.checkUserExist(userId);
        likeStorage.remove(filmId, userId);
    }

    public Collection<Film> getPopular(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparing(f -> likeStorage.getNumberOfLikes(f.getId()), Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void checkFilmExist(Integer id) {
        if (!filmStorage.contains(id)) {
            throw new NotFoundException(String.format("Film %d not found", id));
        }
    }

}
