package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    public List<User> findAll() {
        return service.getAllUsers();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        User newUser = service.createUser(user);
        log.info("User created: {}", newUser);
        return newUser;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
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
    public List<User> getFriends(@PathVariable Integer id) {
        return service.getFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getMutual(@PathVariable Integer id, @PathVariable Integer otherId) {
        return service.getMutualFriends(id, otherId);
    }

}
