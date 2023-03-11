package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MpaRating {

    private final int id;
    @NotBlank(message = "Название не должно быть пустым")
    private final String name;
}
