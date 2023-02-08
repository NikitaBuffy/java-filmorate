package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;
    @NotBlank
    private final String name;
    @Size(max=200)
    private final String description;
    private final LocalDate releaseDate;
    @PositiveOrZero
    private final int duration;
    private Set<Integer> likes = new HashSet<>();

    public static final Comparator<Film> COMPARE_BY_LIKES = new Comparator<Film>() {
        @Override
        public int compare(Film o1, Film o2) {
            return o1.getLikes().size() - o2.getLikes().size();
        }
    };
}
