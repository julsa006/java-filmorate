package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.MPAStorage;

import java.util.ArrayList;
import java.util.List;

@Primary
@Component
@AllArgsConstructor
public class MPADbStorage implements MPAStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<MPA> findAll() {
        String sql = "SELECT * FROM mpas ORDER BY id";
        List<MPA> result = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
        while (rs.next()) {
            result.add(rsToMpa(rs));
        }
        return result;
    }

    @Override
    public MPA get(Integer id) {
        String sql = "SELECT * FROM mpas WHERE id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);
        if (!rs.next()) {
            throw new NotFoundException(String.format("MPA %s not found", id));
        }
        return rsToMpa(rs);
    }

    public static MPA rsToMpa(SqlRowSet row) {
        return new MPA(
                row.getInt("id"),
                row.getString("name")
        );
    }
}
