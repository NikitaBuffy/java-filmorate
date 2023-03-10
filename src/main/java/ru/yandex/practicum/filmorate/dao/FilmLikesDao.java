package ru.yandex.practicum.filmorate.dao;

import java.util.Set;

public interface FilmLikesDao {

    void addLike(int filmId, int userId);
    void deleteLike(int filmId, int userId);
    Set<Integer> findLikesOfFilm(int filmId);
}
