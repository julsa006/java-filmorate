package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserStorageTest {
    private final UserDbStorage userStorage;

    @Test
    void createUserTest() {
        User user1 = createUser(1);
        User createdUser1 = userStorage.create(user1);
        assertThat(createdUser1.getId()).isEqualTo(1);
        user1 = user1.toBuilder().id(1).build();
        assertThat(createdUser1).isEqualTo(user1);
        User user2 = createUser(2);
        User createdUser2 = userStorage.create(user2);
        assertThat(createdUser2.getId()).isEqualTo(2);
        user2 = user2.toBuilder().id(2).build();
        assertThat(createdUser2).isEqualTo(user2);
    }

    @Test
    void getUserTest() {
        User user = createUser(1);
        user = userStorage.create(user);
        assertThat(userStorage.get(user.getId())).isEqualTo(user);
    }

    @Test
    void updateUserTest() {
        User user = createUser(1);
        user = userStorage.create(user);
        User newUser = user.toBuilder().name("new-name").build();
        userStorage.update(newUser);
        assertThat(userStorage.get(user.getId())).isEqualTo(newUser);
    }

    @Test
    void findAllUserTest() {
        assertThat(userStorage.findAll()).isEmpty();
        User user1 = createUser(1);
        user1 = userStorage.create(user1);
        List<User> users = userStorage.findAll();
        assertThat(users).hasSize(1).contains(user1);

        User user2 = createUser(2);
        user2 = userStorage.create(user2);
        users = userStorage.findAll();
        assertThat(users).hasSize(2).contains(user1, user2);
    }

    private User createUser(int i) {
        User user = new User(1, "email" + i + "@email.com", "login" + i, "name" + i,
                LocalDate.of(1990, 12, 31).plusDays(i));
        return user.toBuilder().id(null).build();
    }
}
