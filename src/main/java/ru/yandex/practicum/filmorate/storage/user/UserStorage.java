package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User create(User user);

    void update(User user);

    User get(Integer id);

    List<User> getFriends(Integer id);

    List<User> getMutualFriends(Integer firstId, Integer secondId);
}
