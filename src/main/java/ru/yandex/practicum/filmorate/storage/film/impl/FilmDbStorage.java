package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Component
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private static final String SELECT_FILMS = "SELECT " +
            "m.name mpa_name, " +
            "f.id id, " +
            "f.name name, " +
            "f.description description, " +
            "f.release_date release_date, " +
            "f.duration duration, " +
            "f.mpa_id mpa_id " +
            "FROM films f JOIN mpas m ON f.mpa_id = m.id";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAll() {
        String sql = SELECT_FILMS;

        List<Film> films = new ArrayList<>();

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
        while (rs.next()) {
            films.add(rsToFilm(rs));
        }

        sql = "SELECT g.id genre_id, g.name genre_name, fg.film_id film_id FROM films_genres fg JOIN genres g ON fg.genre_id = g.id";
        rs = jdbcTemplate.queryForRowSet(sql);
        Map<Integer, List<Genre>> filmGenres = new HashMap<>();
        while (rs.next()) {
            int filmId = rs.getInt("FILM_ID");
            Genre genre = new Genre(rs.getInt("GENRE_ID"), rs.getString("GENRE_NAME"));
            if (!filmGenres.containsKey(filmId)) {
                filmGenres.put(filmId, new ArrayList<>());
            }
            List<Genre> genres = filmGenres.get(filmId);
            genres.add(genre);
        }

        for (Film film : films) {
            List<Genre> genres = filmGenres.get(film.getId());
            if (genres == null) {
                continue;
            }
            film.getGenres().addAll(genres);
        }

        return films;
    }

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, film.getName());
            pst.setString(2, film.getDescription());
            pst.setDate(3, Date.valueOf(film.getReleaseDate()));
            pst.setInt(4, film.getDuration());
            pst.setInt(5, film.getMpa().getId());
            return pst;
        }, keyHolder);
        int id = keyHolder.getKey().intValue();
        Film filmWithId = film.toBuilder().id(id).build();
        resetGenres(filmWithId);
        return get(id);
    }

    @Override
    public void update(Film film) {
        String sql = "UPDATE films SET " +
                "   name = ?, " +
                "   description = ?, " +
                "   release_date = ?, " +
                "   duration = ?, " +
                "   mpa_id = ? " +
                "WHERE id = ?";

        int rowCount = jdbcTemplate.update(
                sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        if (rowCount == 0) {
            throw new NotFoundException(String.format("Film %s not found on update", film.getId()));
        }
        resetGenres(film);
    }

    private void resetGenres(Film film) {
        String clearSql = "DELETE FROM films_genres WHERE film_id = ?";
        jdbcTemplate.update(clearSql, film.getId());

        String insertSql = "MERGE INTO films_genres (film_id, genre_id) VALUES (?, ?)";

        if (film.getGenres() == null) {
            return;
        }
        List<Genre> genres = film.getGenres();

        jdbcTemplate.batchUpdate(
                insertSql,
                new BatchPreparedStatementSetter() {

                    public void setValues(PreparedStatement ps, int i)
                            throws SQLException {
                        ps.setInt(1, film.getId());
                        ps.setInt(2, genres.get(i).getId());
                    }

                    public int getBatchSize() {
                        return genres.size();
                    }

                });
    }

    @Override
    public Film get(Integer id) {
        String sql = String.format("%s WHERE f.id = ?", SELECT_FILMS);
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);
        if (!rs.next()) {
            throw new NotFoundException(String.format("Film %s not found", id));
        }
        Film film = rsToFilm(rs);

        sql = "SELECT g.id id, g.name name FROM genres g JOIN films_genres fg ON fg.genre_id = g.id WHERE fg.film_id = ?";
        rs = jdbcTemplate.queryForRowSet(sql, film.getId());

        List<Genre> genres = new ArrayList<>();
        while (rs.next()) {
            genres.add(new Genre(rs.getInt("id"), rs.getString("name")));
        }

        return film.toBuilder().genres(genres).build();
    }

    @Override
    public List<Film> getPopular(int count) {
        String sql = String.format("%s LEFT JOIN likes l ON f.id = l.film_id "
                + "GROUP BY f.id ORDER BY count(l.user_id) DESC LIMIT ?", SELECT_FILMS);

        List<Film> films = new ArrayList<>();

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, count);
        while (rs.next()) {
            films.add(rsToFilm(rs));
        }

        sql = "SELECT g.id genre_id, g.name genre_name, fg.film_id film_id FROM films_genres fg JOIN genres g ON fg.genre_id = g.id";
        rs = jdbcTemplate.queryForRowSet(sql);
        Map<Integer, List<Genre>> filmGenres = new HashMap<>();
        while (rs.next()) {
            int filmId = rs.getInt("FILM_ID");
            Genre genre = new Genre(rs.getInt("GENRE_ID"), rs.getString("GENRE_NAME"));
            if (!filmGenres.containsKey(filmId)) {
                filmGenres.put(filmId, new ArrayList<>());
            }
            List<Genre> genres = filmGenres.get(filmId);
            genres.add(genre);
        }

        for (Film film : films) {
            List<Genre> genres = filmGenres.get(film.getId());
            if (genres == null) {
                continue;
            }
            film.getGenres().addAll(genres);
        }

        return films;
    }

    private Film rsToFilm(SqlRowSet row) {
        return new Film(
                row.getInt("ID"),
                row.getString("NAME"),
                row.getString("DESCRIPTION"),
                row.getDate("RELEASE_DATE").toLocalDate(),
                row.getInt("DURATION"),
                new ArrayList<>(),
                new MPA(
                        row.getInt("MPA_ID"),
                        row.getString("MPA_NAME")
                )
        );
    }
}
