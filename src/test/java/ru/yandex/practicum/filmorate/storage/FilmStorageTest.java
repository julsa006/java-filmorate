package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.impl.FilmDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmStorageTest {
    private final FilmDbStorage filmDbStorage;


    @Test
    void createFilmTest() {
        Film film1 = createFilm(1);
        Film createdFilm1 = filmDbStorage.create(film1);
        assertThat(createdFilm1.getId()).isEqualTo(1);
        film1 = film1.toBuilder().id(1).build();
        assertThat(createdFilm1).isEqualTo(film1);
        Film film2 = createFilm(2);
        Film createdFilm2 = filmDbStorage.create(film2);
        assertThat(createdFilm2.getId()).isEqualTo(2);
        film2 = film2.toBuilder().id(2).build();
        assertThat(createdFilm2).isEqualTo(film2);
    }

    @Test
    void getFilmTest() {
        Film film = createFilm(1);
        film = filmDbStorage.create(film);
        assertThat(filmDbStorage.get(film.getId())).isEqualTo(film);
    }

    @Test
    void updateFilmTest() {
        Film film = createFilm(1);
        film = filmDbStorage.create(film);
        Film newUser = film.toBuilder().name("new-name").build();
        filmDbStorage.update(newUser);
        assertThat(filmDbStorage.get(film.getId())).isEqualTo(newUser);
    }

    @Test
    void findAllFilmTest() {
        assertThat(filmDbStorage.findAll()).isEmpty();
        Film film1 = createFilm(1);
        film1 = filmDbStorage.create(film1);
        List<Film> films = filmDbStorage.findAll();
        assertThat(films).hasSize(1).contains(film1);

        Film film2 = createFilm(2);
        film2 = filmDbStorage.create(film2);
        films = filmDbStorage.findAll();
        assertThat(films).hasSize(2).contains(film1, film2);
    }

    Film createFilm(int i) {
        Film film = new Film(1, "film" + i, "description" + i,
                LocalDate.of(1990, 12, 31).plusDays(i),
                i, new ArrayList<>(), new MPA(1, "G"));
        return film.toBuilder().id(null).build();
    }

}
