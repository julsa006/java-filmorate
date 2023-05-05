package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryLikeStorage implements LikeStorage {
    private final Map<Integer, Set<Integer>> likes = new HashMap<>();

    @Override
    public void add(Integer filmId, Integer userId) {
        if (!likes.containsKey(filmId)) {
            likes.put(filmId, new HashSet<>());
        }
        likes.get(filmId).add(userId);
    }

    @Override
    public void remove(Integer filmId, Integer userId) {
        if ((!likes.containsKey(filmId)) || (!likes.get(filmId).contains(userId))) {
            throw new NotFoundException("Like not found");
        }
        likes.get(filmId).remove(userId);
    }

    @Override
    public int getNumberOfLikes(Integer filmId) {
        if (!likes.containsKey(filmId)) {
            return 0;
        }
        return likes.get(filmId).size();
    }

}