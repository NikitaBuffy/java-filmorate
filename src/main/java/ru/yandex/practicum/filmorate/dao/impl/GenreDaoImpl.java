package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenreById(int genreId) {
        String sqlQuery = "SELECT * FROM genre WHERE genre_id = ?";
        try {
            log.info("Запрошен жанр с ID {} из БД", genreId);
            return jdbcTemplate.queryForObject(sqlQuery, this::mapToRowGenre, genreId);
        } catch (DataAccessException e) {
            log.warn("Не найден жанр с ID " + genreId);
            throw new GenreNotFoundException("Жанр с ID " + genreId + " не найден!");
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM genre ORDER BY genre_id";
        log.info("Запрошен список всех жанров из БД");
        return jdbcTemplate.query(sqlQuery, this::mapToRowGenre);
    }

    @Override
    public List<Genre> getGenresByFilm(int filmId) {
        String sqlQuery = "SELECT * FROM genre g JOIN film_genre fg ON g.genre_id = fg.genre_id WHERE film_id = ?";
        log.info("Запрошен список жанров, относящихся к фильму с ID {}", filmId);
        return jdbcTemplate.query(sqlQuery, this::mapToRowGenre, filmId);
    }

    private Genre mapToRowGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("name"));
    }
}
