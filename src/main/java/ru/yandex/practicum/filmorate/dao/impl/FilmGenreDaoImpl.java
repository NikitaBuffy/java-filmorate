package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmGenreDao;

@Component
@Slf4j
public class FilmGenreDaoImpl implements FilmGenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmGenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void addGenre(int filmId, int genreId) {
        String sqlQuery = "INSERT INTO film_genre(film_id, genre_id) VALUES(?, ?)";
        log.info("К фильму с ID {} добавлен жанр с ID {}", filmId, genreId);
        jdbcTemplate.update(sqlQuery, filmId, genreId);
    }

    @Override
    public void deleteGenre(int filmId) {
        String sqlQuery = "DELETE FROM film_genre WHERE film_id = ?";
        log.info("Удалены жанры у фильма с ID {}", filmId);
        jdbcTemplate.update(sqlQuery, filmId);
    }
}
