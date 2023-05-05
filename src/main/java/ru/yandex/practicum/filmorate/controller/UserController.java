package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<User> findAll() {
        return service.getAllUsers();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validate(user, false);
        user = user.toBuilder().name(getDefaultName(user)).build();
        User newUser = service.createUser(user);
        log.info("User created: {}", newUser);
        return newUser;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        validate(user, true);
        user = user.toBuilder().name(getDefaultName(user)).build();
        service.updateUser(user);
        log.info("User updated: {}", user);
        return user;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Integer id) {
        return service.getUser(id);
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        service.addFriend(id, friendId);
        log.info("User {} and {} became friends", id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        service.removeFriend(id, friendId);
        log.info("User {} and {} stopped being friends", id, friendId);
    }

    @GetMapping("{id}/friends")
    public Collection<User> getFriends(@PathVariable Integer id) {
        return service.getFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public Collection<User> getMutual(@PathVariable Integer id, @PathVariable Integer otherId) {
        return service.getMutualFriends(id, otherId);
    }


    private void validate(User user, boolean requireId) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Login cannot contain spaces");
        }
        if (requireId && (user.getId() == null)) {
            throw new ValidationException("Id cannot be null");
        }
    }

    private String getDefaultName(User user) {
        if ((user.getName() == null) || (user.getName().isBlank())) {
            return user.getLogin();
        }
        return user.getName();
    }

}
