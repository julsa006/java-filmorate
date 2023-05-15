package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    public User createUser(User user) {
        validateUser(user, false);
        user = user.toBuilder().name(getDefaultName(user)).build();
        return userStorage.create(user);
    }

    public void updateUser(User user) {
        validateUser(user, true);
        user = user.toBuilder().name(getDefaultName(user)).build();
        userStorage.update(user);
    }

    public User getUser(Integer userId) {
        return userStorage.get(userId);
    }

    public List<User> getAllUsers() {
        return userStorage.findAll();
    }


    public void addFriend(Integer firstId, Integer secondId) {
        friendshipStorage.add(firstId, secondId);
    }

    public void removeFriend(Integer firstId, Integer secondId) {
        friendshipStorage.remove(firstId, secondId);
    }

    public List<User> getMutualFriends(Integer firstId, Integer secondId) {
        return userStorage.getMutualFriends(firstId, secondId);
    }

    public List<User> getFriends(Integer id) {
        return userStorage.getFriends(id);
    }

    private void validateUser(User user, boolean requireId) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Login cannot contain spaces");
        }
        if (requireId && user.getId() == null) {
            throw new ValidationException("Id cannot be null");
        }
    }

    private String getDefaultName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            return user.getLogin();
        }
        return user.getName();
    }
}
