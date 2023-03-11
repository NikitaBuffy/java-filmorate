package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.dao.FilmLikesDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaRatingDao;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {

    private final MpaRatingDao mpaRating;
    private final GenreDao genreDao;
    private final FilmGenreDao filmGenreDao;
    private final FilmLikesDao filmLikesDao;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "INSERT INTO films(name, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sqlQuery, new String[]{"film_id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setString(3, String.valueOf(film.getReleaseDate()));
            statement.setString(4, String.valueOf(film.getDuration()));
            statement.setString(5, String.valueOf(film.getMpa().getId()));
            return statement;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                filmGenreDao.addGenre(film.getId(), genre.getId());
            }
        }

        log.info("Добавлен фильм в базу данных: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE film_id = ?";
        int recordsAffected = jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (recordsAffected != 1) {
            log.warn("Не найден фильм с ID " + film.getId());
            throw new FilmNotFoundException("Фильм с ID " + film.getId() + " не найден!");
        }

        if(film.getGenres() == null || film.getGenres().isEmpty()) {
            filmGenreDao.deleteGenre(film.getId());
            log.debug("Обновлен фильм с id={}", film.getId());
            return film;
        }

        filmGenreDao.deleteGenre(film.getId());

        List<Genre> genreList = new ArrayList<>(film.getGenres());
        Set<Genre> genreSet = new LinkedHashSet<>(genreList);
        genreList.clear();
        genreList.addAll(genreSet);

        for (Genre genre : genreList) {
            filmGenreDao.addGenre(film.getId(), genre.getId());
        }

        log.info("Обновлен фильм в базе данных с ID {}", film.getId());
        return getFilmById(film.getId());
    }

    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT * FROM films";
        log.info("Запрошен список всех фильмов из БД");
        return jdbcTemplate.query(sqlQuery, this::mapToRowFilm);
    }

    @Override
    public Film getFilmById(int id) {
        String sqlQuery = "SELECT * FROM films WHERE film_id = ?";
        try {
            log.info("Запрошен фильм с ID {} из БД", id);
            return jdbcTemplate.queryForObject(sqlQuery, this::mapToRowFilm, id);
        } catch (DataAccessException e) {
            log.warn("Не найден фильм с ID " + id);
            throw new FilmNotFoundException("Фильм с ID " + id + " не найден!");
        }
    }

    @Override
    public List<Film> getTopRatedFilms(Integer count) {
        String sqlQuery = "SELECT * FROM films f LEFT JOIN film_likes fl ON f.film_id = fl.film_id GROUP BY f.film_id ORDER BY COUNT(fl.user_id) DESC LIMIT ?";
        log.info("Запрошен список популярных фильмов по количеству лайков с ограничением на выборку первых {} фильмов", count);
        return jdbcTemplate.query(sqlQuery, this::mapToRowFilm, count);
    }


    private Film mapToRowFilm (ResultSet rs, int rowNum) throws SQLException {
        MpaRating mpa = mpaRating.getMpaById(rs.getInt("mpa_rating_id"));
        List<Genre> genre = new ArrayList<>(genreDao.getGenresByFilm(rs.getInt("film_id")));
        Set<Integer> likes = filmLikesDao.findLikesOfFilm(rs.getInt("film_id"));
        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpa)
                .genres(genre)
                .likes(likes)
                .build();
    }
}
