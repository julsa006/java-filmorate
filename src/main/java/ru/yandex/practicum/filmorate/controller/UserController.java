package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int counter = 0;

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        User validatedUsed = validateAndSetDefaults(user, false);
        validatedUsed = validatedUsed.toBuilder().id(++counter).build();
        users.put(validatedUsed.getId(), validatedUsed);
        log.info("User created: {}", validatedUsed);
        return validatedUsed;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        User validatedUsed = validateAndSetDefaults(user, true);
        if (!users.containsKey(validatedUsed.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User %s not found", validatedUsed.getId()));
        }
        users.put(validatedUsed.getId(), validatedUsed);
        log.info("User updated: {}", validatedUsed);
        return validatedUsed;
    }

    private User validateAndSetDefaults(User user, boolean requireId) {
        if (user.getLogin().contains(" ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Login cannot contain spaces");
        }
        if (requireId && (user.getId() == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id cannot be null");
        }
        User.UserBuilder userBuilder = user.toBuilder();
        if ((user.getName() == null) || (user.getName().isBlank())) {
            userBuilder.name(user.getLogin());
        }
        return userBuilder.build();
    }

}
