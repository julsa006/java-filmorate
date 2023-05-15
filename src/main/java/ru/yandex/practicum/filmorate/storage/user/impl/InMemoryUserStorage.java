package ru.yandex.practicum.filmorate.storage.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    @Autowired
    private InMemoryFriendshipStorage friendshipStorage;

    private final Map<Integer, User> users = new HashMap<>();
    private int currentId = 0;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        User newUser = user.toBuilder().id(++currentId).build();
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public void update(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public User get(Integer id) {
        return users.get(id);
    }

    @Override
    public List<User> getFriends(Integer id) {
        List<User> friends = new ArrayList<>();

        for (Integer friendId : friendshipStorage.getFriends(id)) {
            friends.add(users.get(friendId));
        }
        return friends;
    }

    @Override
    public List<User> getMutualFriends(Integer firstId, Integer secondId) {
        List<Integer> firstFriends = friendshipStorage.getFriends(firstId);
        List<Integer> secondFriends = friendshipStorage.getFriends(secondId);
        List<User> mutual = new ArrayList<>();

        for (Integer id : firstFriends) {
            if (secondFriends.contains(id)) {
                mutual.add(users.get(id));
            }
        }
        return mutual;
    }

}
