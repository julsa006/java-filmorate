package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.user.FriendshipStorage;


@Primary
@Component
@AllArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(Integer firstId, Integer secondId) {
        String sql = "INSERT INTO users_relationships (follower_id, following_id) VALUES (?, ?)";

        try {
            jdbcTemplate.update(sql, firstId, secondId);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException(String.format("One of user#%s or user#%s not found", firstId, secondId));
        }
    }

    @Override
    public void remove(Integer firstId, Integer secondId) {
        String sql = "DELETE FROM users_relationships WHERE follower_id = ? AND following_id = ?";

        int rowCount = jdbcTemplate.update(sql, firstId, secondId);
        if (rowCount == 0) {
            throw new NotFoundException(String.format("No friendship found for user#%d and user#%d", firstId, secondId));
        }
    }
}
