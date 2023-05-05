package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;


    @Autowired
    public UserService(UserStorage userStorage, FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    public User createUser(User user) {
        return userStorage.create(user);
    }

    public void updateUser(User user) {
        checkUserExist(user.getId());
        userStorage.update(user);
    }

    public User getUser(Integer userId) {
        checkUserExist(userId);
        return userStorage.get(userId);
    }

    public Collection<User> getAllUsers() {
        return userStorage.findAll();
    }


    public void addFriend(Integer firstId, Integer secondId) {
        checkUserExist(firstId);
        checkUserExist(secondId);
        friendshipStorage.add(firstId, secondId);
    }

    public void removeFriend(Integer firstId, Integer secondId) {
        checkUserExist(firstId);
        checkUserExist(secondId);
        friendshipStorage.remove(firstId, secondId);
    }

    public Collection<User> getMutualFriends(Integer firstId, Integer secondId) {
        checkUserExist(firstId);
        checkUserExist(secondId);
        Set<Integer> firstFriends = friendshipStorage.getFriends(firstId);
        Set<Integer> secondFriends = friendshipStorage.getFriends(secondId);
        List<User> mutual = new ArrayList<>();

        if ((firstFriends == null) || (secondFriends == null)) {
            return mutual;
        }

        for (Integer firstFriend : firstFriends) {
            if (secondFriends.contains(firstFriend)) {
                mutual.add(userStorage.get(firstFriend));
            }
        }
        return mutual;
    }

    public Collection<User> getFriends(Integer id) {
        checkUserExist(id);
        List<User> friends = new ArrayList<>();
        for (Integer friendId : friendshipStorage.getFriends(id)) {
            friends.add(userStorage.get(friendId));
        }
        return friends;
    }

    void checkUserExist(Integer id) {
        if (!userStorage.contains(id)) {
            throw new NotFoundException(String.format("User %d not found", id));
        }
    }
}
