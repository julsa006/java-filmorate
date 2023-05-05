package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int currentId = 0;

    @Override
    public Collection<User> findAll() {
        return users.values();
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
    public boolean contains(Integer id) {
        return users.containsKey(id);
    }
}
