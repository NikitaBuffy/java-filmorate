package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final HashMap<Integer, Film> filmMap = new HashMap<>();
    int random = 0;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.parse("1895-12-28");

    private int generateId() {
        return ++random;
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Валидация не пройдена: отсутствует название фильма.");
            throw new ValidationException("Название не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Валидация не пройдена: описание превышает 200 символов.");
            throw new ValidationException("Описание превышает 200 символов.");
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Валидация не пройдена: дата релиза раньше {}", MIN_RELEASE_DATE);
            throw new ValidationException("Дата релиза фильма должна быть после " + MIN_RELEASE_DATE);
        }
        if (film.getDuration() < 0) {
            log.warn("Валидация не пройдена: отрицательная продолжительность фильма.");
            throw new ValidationException("Продолжительность фильма не может быть отрицательным.");
        }
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        film.setId(generateId());
        validate(film);
        filmMap.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        validate(film);
        if (filmMap.containsKey(film.getId())) {
            filmMap.put(film.getId(), film);
        } else {
            log.warn("Не найден фильм при попытке обновления.");
            throw new UserNotFoundException("Фильм с ID " + film.getId() + " не существует.");
        }
        log.info("Обновлен фильм с ID {}", film.getId());
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        List<Film> filmList = new ArrayList<>();
        for (Map.Entry<Integer, Film> entry : filmMap.entrySet()) {
            filmList.add(entry.getValue());
        }
        return filmList;
    }

}
