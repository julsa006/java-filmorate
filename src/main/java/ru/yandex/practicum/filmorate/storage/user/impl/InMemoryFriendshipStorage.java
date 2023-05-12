package ru.yandex.practicum.filmorate.storage.user.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.user.FriendshipStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryFriendshipStorage implements FriendshipStorage {

    private final Map<Integer, Set<Integer>> friendship = new HashMap<>();

    @Override
    public void add(Integer firstId, Integer secondId) {
        if (!friendship.containsKey(firstId)) {
            friendship.put(firstId, new HashSet<>());
        }
        friendship.get(firstId).add(secondId);
        if (!friendship.containsKey(secondId)) {
            friendship.put(secondId, new HashSet<>());
        }
        friendship.get(secondId).add(firstId);
    }

    @Override
    public void remove(Integer firstId, Integer secondId) {
        if (friendship.containsKey(firstId)) {
            friendship.get(firstId).remove(secondId);
            if (friendship.get(firstId).isEmpty()) {
                friendship.remove(firstId);
            }
        }
        if (friendship.containsKey(secondId)) {
            friendship.get(secondId).remove(firstId);
            if (friendship.get(secondId).isEmpty()) {
                friendship.remove(secondId);
            }
        }
    }

    @Override
    public Set<Integer> getFriends(Integer userId) {
        return friendship.get(userId);
    }
}
