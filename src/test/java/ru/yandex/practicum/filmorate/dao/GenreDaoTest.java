package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GenreDaoTest {

    public final GenreDao genreDao;
    public final FilmDbStorage filmDbStorage;
    public final MpaRatingDao mpaRatingDao;
    public final FilmGenreDao filmGenreDao;

    @BeforeAll
    public void beforeAll() {
        filmDbStorage.addFilm(Film.builder()
                .name("Звездные войны")
                .description("Давным давно")
                .releaseDate(LocalDate.of(1974, 3, 9))
                .duration(120)
                .mpa(mpaRatingDao.getMpaById(2))
                .build());
        filmGenreDao.addGenre(1, 3);
        filmGenreDao.addGenre(1, 5);
    }

    @Test
    public void shouldReturnAllGenres() {
        List<Genre> genreList = genreDao.getAllGenres();

        assertThat(genreList.size()).isEqualTo(6);
    }

    @Test
    public void shouldReturnGenreById() {
        Genre genre = genreDao.getGenreById(1);

        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    public void shouldNotFoundGenre() {
        assertThatExceptionOfType(GenreNotFoundException.class).isThrownBy(() -> genreDao.getGenreById(111));
    }

    @Test
    public void shouldReturnAllGenresByFilm() {
        List<Genre> genreList = genreDao.getGenresByFilm(1);

        assertThat(genreList.size()).isEqualTo(2);
        assertThat(genreList.get(0)).hasFieldOrPropertyWithValue("name", "Мультфильм");
    }

    @Test
    public void shouldDeleteGenreFromFilm() {
        filmGenreDao.deleteGenre(1);

        List<Genre> genreList = genreDao.getGenresByFilm(1);

        assertThat(genreList.size()).isEqualTo(0);
    }

}
