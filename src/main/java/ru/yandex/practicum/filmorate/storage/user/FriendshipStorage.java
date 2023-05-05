package ru.yandex.practicum.filmorate.storage.user;

import java.util.Set;

public interface FriendshipStorage {
    void add(Integer firstId, Integer secondId);

    void remove(Integer firstId, Integer secondId);

    Set<Integer> getFriends(Integer userId);
}
