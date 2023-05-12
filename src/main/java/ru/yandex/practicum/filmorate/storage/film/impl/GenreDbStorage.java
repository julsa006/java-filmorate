package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.util.ArrayList;
import java.util.List;

@Primary
@Component
@AllArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> findAll() {
        String sql = "SELECT * FROM genres ORDER BY id";
        List<Genre> result = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
        while (rs.next()) {
            result.add(rsToGenre(rs));
        }
        return result;
    }

    @Override
    public Genre get(Integer id) {
        String sql = "SELECT * FROM genres WHERE id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);
        if (!rs.next()) {
            throw new NotFoundException(String.format("Genre %s not found", id));
        }
        return rsToGenre(rs);
    }

    public Genre rsToGenre(SqlRowSet row) {
        return new Genre(
                row.getInt("id"),
                row.getString("name")
        );
    }
}
