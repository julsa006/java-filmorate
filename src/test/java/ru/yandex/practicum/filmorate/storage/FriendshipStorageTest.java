package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.storage.user.impl.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDbStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.yandex.practicum.filmorate.storage.UserStorageTest.createUser;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendshipStorageTest {
    private final UserDbStorage userStorage;
    private final FriendshipDbStorage friendshipDbStorage;

    @BeforeEach
    void before() {
        userStorage.create(createUser(1));
        userStorage.create(createUser(2));
        userStorage.create(createUser(3));
        userStorage.create(createUser(4));
        userStorage.create(createUser(5));
    }

    @Test
    void getFriendsTest() {
        assertThat(friendshipDbStorage.getFriends(1)).isEmpty();
        friendshipDbStorage.add(1, 2);
        assertThat(friendshipDbStorage.getFriends(1)).hasSize(1).contains(2);
        assertThat(friendshipDbStorage.getFriends(2)).isEmpty();
        friendshipDbStorage.add(1, 4);
        assertThat(friendshipDbStorage.getFriends(1)).hasSize(2).contains(2, 4);
        assertThat(friendshipDbStorage.getFriends(4)).isEmpty();
        friendshipDbStorage.add(2, 1);
        assertThat(friendshipDbStorage.getFriends(1)).hasSize(2).contains(2, 4);
        assertThat(friendshipDbStorage.getFriends(2)).hasSize(1).contains(1);

    }

    @Test
    void removeTest() {
        friendshipDbStorage.add(1, 2);
        friendshipDbStorage.add(2, 1);
        friendshipDbStorage.add(1, 4);
        assertThat(friendshipDbStorage.getFriends(1)).hasSize(2).contains(2, 4);
        assertThat(friendshipDbStorage.getFriends(2)).hasSize(1).contains(1);

        friendshipDbStorage.remove(1, 2);
        assertThat(friendshipDbStorage.getFriends(1)).hasSize(1).contains(4);
        assertThat(friendshipDbStorage.getFriends(2)).hasSize(1).contains(1);

        friendshipDbStorage.remove(2, 1);
        assertThat(friendshipDbStorage.getFriends(1)).hasSize(1).contains(4);
        assertThat(friendshipDbStorage.getFriends(2)).isEmpty();

        friendshipDbStorage.remove(1, 4);
        assertThat(friendshipDbStorage.getFriends(1)).isEmpty();
    }
}
