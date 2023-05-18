package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Primary
@Component
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        List<User> result = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
        while (rs.next()) {
            result.add(rsToUser(rs));
        }
        return result;
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, user.getEmail());
            pst.setString(2, user.getLogin());
            pst.setString(3, user.getName());
            pst.setDate(4, Date.valueOf(user.getBirthday()));
            return pst;
        }, keyHolder);
        int id = keyHolder.getKey().intValue();
        return user.toBuilder().id(id).build();
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE users SET " +
                "   email = ?, " +
                "   login = ?, " +
                "   name = ?, " +
                "   birthday = ? " +
                "WHERE id = ?";

        int rowCount = jdbcTemplate.update(
                sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        if (rowCount == 0) {
            throw new NotFoundException(String.format("User %s not found on update", user.getId()));
        }
    }

    @Override
    public User get(Integer id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);
        if (!rs.next()) {
            throw new NotFoundException(String.format("User %s not found", id));
        }
        return rsToUser(rs);
    }

    @Override
    public List<User> getFriends(Integer id) {
        String sql = "SELECT * FROM users u JOIN users_relationships ur ON u.id = ur.following_id " +
                "WHERE ur.follower_id = ? ORDER BY u.id";
        List<User> result = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);
        while (rs.next()) {
            result.add(rsToUser(rs));
        }
        return result;
    }

    @Override
    public List<User> getMutualFriends(Integer firstId, Integer secondId) {
        String sql = "SELECT * " +
                "FROM USERS u " +
                "WHERE u.ID IN " +
                "(SELECT ur.FOLLOWING_ID " +
                "FROM USERS_RELATIONSHIPS ur " +
                "WHERE ur.FOLLOWER_ID = ?) " +
                "AND " +
                "u.ID IN " +
                "(SELECT ur.FOLLOWING_ID " +
                "FROM USERS_RELATIONSHIPS ur " +
                "WHERE ur.FOLLOWER_ID =?) " +
                "ORDER BY u.id";

        List<User> result = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, firstId, secondId);
        while (rs.next()) {
            result.add(rsToUser(rs));
        }
        return result;
    }

    private User rsToUser(SqlRowSet row) {
        return new User(
                row.getInt("id"),
                row.getString("email"),
                row.getString("login"),
                row.getString("name"),
                row.getDate("birthday").toLocalDate()
        );
    }
}
