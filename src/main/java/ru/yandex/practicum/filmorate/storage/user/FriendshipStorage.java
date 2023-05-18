package ru.yandex.practicum.filmorate.storage.user;

public interface FriendshipStorage {
    void add(Integer firstId, Integer secondId);

    void remove(Integer firstId, Integer secondId);
}
