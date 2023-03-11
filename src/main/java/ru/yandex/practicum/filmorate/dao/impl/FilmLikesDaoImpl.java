package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmLikesDao;

import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class FilmLikesDaoImpl implements FilmLikesDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmLikesDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO film_likes(film_id, user_id) VALUES (?, ?)";
        log.info("Пользователь с ID {} поставил лайк фильму с ID {}", userId, filmId);
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        String sqlQuery = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        log.info("Пользователь с ID {} убрал лайк с фильма с ID {}", userId, filmId);
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public Set<Integer> findLikesOfFilm(int filmId) {
        String sqlQuery = "SELECT user_id FROM film_likes WHERE film_id = ?";
        log.info("Запрошен список лайков из БД для фильма с ID {}", filmId);
        return new HashSet<>(jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getInt("user_id"), filmId));
    }
}
