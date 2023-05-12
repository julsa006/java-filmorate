package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.storage.film.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.impl.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDbStorage;


import static org.assertj.core.api.Assertions.assertThat;

import static ru.yandex.practicum.filmorate.storage.UserStorageTest.createUser;

import static ru.yandex.practicum.filmorate.storage.FilmStorageTest.createFilm;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LikeStorageTest {
    private final UserDbStorage userStorage;
    private final LikeDbStorage likeStorage;
    private final FilmDbStorage filmStorage;

    @BeforeEach
    void before() {
        userStorage.create(createUser(1));
        userStorage.create(createUser(2));
        filmStorage.create(createFilm(1));
        filmStorage.create(createFilm(2));

    }

    @Test
    void getNumberOfLikesTest() {
        assertThat(likeStorage.getNumberOfLikes(1)).isEqualTo(0);
        likeStorage.add(1, 1);
        assertThat(likeStorage.getNumberOfLikes(1)).isEqualTo(1);
        assertThat(likeStorage.getNumberOfLikes(2)).isEqualTo(0);
        likeStorage.add(1, 2);
        assertThat(likeStorage.getNumberOfLikes(1)).isEqualTo(2);
        assertThat(likeStorage.getNumberOfLikes(2)).isEqualTo(0);
    }

    @Test
    void removeTest() {
        likeStorage.add(1, 1);
        likeStorage.add(1, 2);
        likeStorage.add(2, 1);
        assertThat(likeStorage.getNumberOfLikes(1)).isEqualTo(2);
        assertThat(likeStorage.getNumberOfLikes(2)).isEqualTo(1);

        likeStorage.remove(1, 2);
        assertThat(likeStorage.getNumberOfLikes(1)).isEqualTo(1);
        assertThat(likeStorage.getNumberOfLikes(2)).isEqualTo(1);

        likeStorage.remove(2, 1);
        assertThat(likeStorage.getNumberOfLikes(1)).isEqualTo(1);
        assertThat(likeStorage.getNumberOfLikes(2)).isEqualTo(0);

        likeStorage.remove(1, 1);
        assertThat(likeStorage.getNumberOfLikes(1)).isEqualTo(0);
        assertThat(likeStorage.getNumberOfLikes(2)).isEqualTo(0);
    }
}
