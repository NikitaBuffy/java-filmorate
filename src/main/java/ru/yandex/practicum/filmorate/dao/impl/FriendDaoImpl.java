package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FriendDao;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class FriendDaoImpl implements FriendDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(int id, int friendId) {
        String sqlQueryForFriendsFind = "SELECT status FROM friends WHERE user_id1 = ? AND user_id2 = ? OR user_id2 = ? AND user_id1 = ?";
        List<Boolean> statusList = jdbcTemplate.queryForList(sqlQueryForFriendsFind, Boolean.class, id, friendId, id, friendId);

        if (!statusList.isEmpty()) {
            Boolean status = statusList.get(0);

            if (status != null && !status) {
                String sqlQueryUpdate = "UPDATE friends SET status = true WHERE user_id1 = ? AND user_id2 = ? OR user_id2 =? AND user_id1 = ?";
                jdbcTemplate.update(sqlQueryUpdate, id, friendId, id, friendId);
                log.info("Пользователь с ID {} подтвердил дружбу с пользователем с ID {}", friendId, id);
            } else {
                String sqlQueryInsert = "INSERT INTO friends (user_id1, user_id2, status) VALUES (?, ?, false)";
                jdbcTemplate.update(sqlQueryInsert, id, friendId);
                log.info("Пользователь с ID {} добавил в друзья пользователя с ID {}", id, friendId);
            }
        } else {
            String sqlQueryInsert = "INSERT INTO friends (user_id1, user_id2, status) VALUES (?, ?, false)";
            jdbcTemplate.update(sqlQueryInsert, id, friendId);
            log.info("Пользователь с ID {} добавил в друзья пользователя с ID {}", id, friendId);
        }
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        String sqlQueryFind = "SELECT status FROM friends WHERE user_id1 = ? AND user_id2 = ? OR user_id2 = ? AND user_id1 = ?";
        List<Boolean> statusList = jdbcTemplate.queryForList(sqlQueryFind, Boolean.class, id, friendId, id, friendId);

        if(!statusList.isEmpty()) {
            Boolean status = statusList.get(0);

            if (!status) {
                String sqlQuery = "DELETE FROM friends WHERE user_id1 = ? AND user_id2 = ?";
                log.info("Удалена строка о дружбе с ID1 {} и ID2 {}", id, friendId);
                jdbcTemplate.update(sqlQuery, id, friendId);
            } else {
                String sqlQuery = "UPDATE friends SET status = false WHERE user_id1 = ? AND user_id2 = ? OR user_id2 = ? AND user_id1 = ?";
                log.info("Установлен статус false в строке дружбы между ID1 {} и ID2 {}", id, friendId);
                jdbcTemplate.update(sqlQuery, id, friendId, id, friendId);
            }

        } else {
            log.warn("Пользователи с ID {} и {} не являются друзьями", id, friendId);
            throw new UserNotFoundException("Неверный запрос поиска дружбы между пользователями");
        }
    }

    @Override
    public List<User> getFriends(int id) {
        String sqlQuery = "SELECT * FROM users u JOIN friends f on u.user_id = f.user_id2 WHERE f.user_id1 = ?" +
                "UNION SELECT * FROM users u JOIN friends f on u.user_id = f.user_id2 WHERE f.user_id2 = ? AND status = true";
        log.info("Запрошен список друзей пользователя с ID {} из БД", id);
        return jdbcTemplate.query(sqlQuery, this::mapToRowUser, id, id);
    }

    @Override
    public List<User> findCommonFriends(int id, int friendId) {
        String sqlQuery = "SELECT * FROM users WHERE user_id IN (SELECT CASE " +
                "WHEN (user_id1 = ? AND user_id2 != ?) THEN user_id2 WHEN (user_id1 != ? AND user_id2 = ?) THEN user_id1 " +
                "END FROM friends INTERSECT SELECT CASE WHEN (user_id1 = ? AND user_id2 != ?) THEN user_id2 " +
                "WHEN (user_id1 != ? AND user_id2 = ?) THEN user_id1 END FROM friends)";
        log.info("Запрошен список общих друзей ID {} и ID {} из БД", id, friendId);
        return jdbcTemplate.query(sqlQuery, this::mapToRowUser, id, friendId, friendId, id, friendId, id, id, friendId);
    }

    private Friend mapToRowFriend(ResultSet rs, int rowNum) throws SQLException {
        return new Friend(rs.getInt("user_id1"),
                        rs.getInt("user_id2"),
                        rs.getBoolean("status"));
    }

    private User mapToRowUser (ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getInt("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }
}
