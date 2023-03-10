package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaRatingDao;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class MpaRatingDaoImpl implements MpaRatingDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaRatingDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MpaRating> getAllMpa() {
        String sqlQuery = "SELECT * FROM mpa_rating ORDER BY mpa_rating_id";
        log.info("Запрошен список рейтингов MPA из БД");
        return jdbcTemplate.query(sqlQuery, this::mapToRowMpa);
    }

    @Override
    public MpaRating getMpaById(int id) {
        String sqlQuery = "SELECT * FROM mpa_rating WHERE mpa_rating_id = ?";
        try {
            log.info("Запрошен рейтинг MPA с ID {} из БД", id);
            return jdbcTemplate.queryForObject(sqlQuery, this::mapToRowMpa, id);
        } catch (DataAccessException e) {
            log.warn("Не найден MPA рейтинг с ID " + id);
            throw new MpaNotFoundException("MPA рейтинг с ID " + id + " не найден!");
        }
    }

    private MpaRating mapToRowMpa(ResultSet rs, int rowNum) throws SQLException {
        return new MpaRating(rs.getInt("mpa_rating_id"), rs.getString("name"));
    }
}
