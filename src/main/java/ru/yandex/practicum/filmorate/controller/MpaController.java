package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.impl.MpaRatingDaoImpl;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final MpaRatingDaoImpl mpa;

    public MpaController(MpaRatingDaoImpl mpa) {
        this.mpa = mpa;
    }

    @GetMapping
    public List<MpaRating> getMpa() {
        return mpa.getAllMpa();
    }

    @GetMapping("/{id}")
    public MpaRating getMpaById(@PathVariable int id) {
        return mpa.getMpaById(id);
    }
}
