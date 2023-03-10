package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmLikesDaoTest {

    private final FilmDbStorage filmDbStorage;
    private final MpaRatingDao mpaRatingDao;
    private final UserDbStorage userDbStorage;
    private final FilmLikesDao filmLikesDao;

    @BeforeAll
    public void beforeAll() {
        userDbStorage.createUser(User.builder()
                .email("test@test.ru")
                .login("tester")
                .name("Tester")
                .birthday(LocalDate.of(2000, 10, 10))
                .build());
        userDbStorage.createUser(User.builder()
                .email("sec@sec.ru")
                .login("second")
                .name("second")
                .birthday(LocalDate.of(1998, 10, 10))
                .build());
        filmDbStorage.addFilm(Film.builder()
                .name("Тестировщик")
                .description("Фильм о тестах")
                .releaseDate(LocalDate.of(2023, 3, 9))
                .duration(10)
                .mpa(mpaRatingDao.getMpaById(1))
                .build());
        filmDbStorage.addFilm(Film.builder()
                .name("Звездные войны")
                .description("Давным давно")
                .releaseDate(LocalDate.of(1974, 3, 9))
                .duration(120)
                .mpa(mpaRatingDao.getMpaById(2))
                .build());
    }

    @Test
    public void shouldAddLike() {
        filmLikesDao.addLike(1,1);
        assertThat(filmDbStorage.getFilmById(1).getLikes().size()).isEqualTo(1);
    }

    @Test
    public void shouldDeleteLike() {
        filmLikesDao.addLike(2,1);
        filmLikesDao.deleteLike(2, 1);

        assertThat(filmDbStorage.getFilmById(2).getLikes().isEmpty()).isTrue();
    }

    @Test
    public void shouldNotAddAnotherLikeToSameFilm() {
        filmLikesDao.addLike(1, 2);
        assertThatExceptionOfType(DuplicateKeyException.class).isThrownBy(() -> filmLikesDao.addLike(1, 2));
    }

    @Test
    public void shouldReturnUsersWhoLikedFilm() {
        filmLikesDao.addLike(2,1);
        filmLikesDao.addLike(2, 2);
        Set<Integer> userSet = filmLikesDao.findLikesOfFilm(2);

        assertThat(userSet.size()).isEqualTo(2);
    }
}
