package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int maxId = 0;

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validate(user, false);
        user = checkAndSetDefaultName(user);
        user = user.toBuilder().id(++maxId).build();
        users.put(user.getId(), user);
        log.info("User created: {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        validate(user, true);
        user = checkAndSetDefaultName(user);
        if (!users.containsKey(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User %s not found", user.getId()));
        }
        users.put(user.getId(), user);
        log.info("User updated: {}", user);
        return user;
    }

    private void validate(User user, boolean requireId) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Login cannot contain spaces");
        }
        if (requireId && (user.getId() == null)) {
            throw new ValidationException("Id cannot be null");
        }
    }

    private User checkAndSetDefaultName(User user) {
        User.UserBuilder userBuilder = user.toBuilder();
        if ((user.getName() == null) || (user.getName().isBlank())) {
            userBuilder.name(user.getLogin());
        }
        return userBuilder.build();
    }

}
