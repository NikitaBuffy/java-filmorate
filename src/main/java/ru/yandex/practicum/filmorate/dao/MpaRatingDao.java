package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public interface MpaRatingDao {

    List<MpaRating> getAllMpa();
    MpaRating getMpaById(int id);
}
