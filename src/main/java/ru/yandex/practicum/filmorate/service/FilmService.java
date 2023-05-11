package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.LikeStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilmService {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserService userService;

    public Film getFilm(Integer id) {
        checkFilmExist(id);
        return filmStorage.get(id);
    }

    public Film createFilm(Film film) {
        validateFilm(film, false);
        return filmStorage.create(film);
    }

    public void updateFilm(Film film) {
        validateFilm(film, true);
        checkFilmExist(film.getId());
        filmStorage.update(film);
    }

    public List<Film> getAllFilms() {
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

    public List<Film> getPopular(int count) {
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

    private void validateFilm(Film film, boolean requireId) {
        if (requireId && film.getId() == null) {
            throw new ValidationException("Id cannot be null");
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException(String.format("Release date must be after %s", MIN_RELEASE_DATE));
        }
    }

}
