package ru.yandex.practicum.filmorate.dao;

public interface FilmGenreDao {

    void addGenre(int filmId, int genreId);
    void deleteGenre(int filmId);
}
