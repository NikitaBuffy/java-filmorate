package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidationTests {

    private FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmService);
    }

    @Test
    public void shouldNotValidateIfNameIsNull() {
        Film nullFilm = new Film(null, "Описание", LocalDate.of(2019, 8, 12), 120);
        assertThrows(ValidationException.class, () -> { filmController.addFilm(nullFilm); });
        Film noFilm = new Film("", "Описание", LocalDate.of(2019, 8, 12), 120);
        assertThrows(ValidationException.class, () -> { filmController.addFilm(noFilm); });
        Film gapFilm = new Film(" ", "Описание", LocalDate.of(2019, 8, 12), 120);
        assertThrows(ValidationException.class, () -> { filmController.addFilm(gapFilm); });
    }

    @Test
    public void shouldNotValidateIfLongDescription() {
        Film film = new Film("Звездные войны. эпизод IV: Новая Надежда",
                "Идет гражданская война. Космические корабли" +
                "повстанцев, наносящие удар с тайной базы," +
                "одержали первую победу, в схватке" +
                "со зловещей Галактической Империей." +
                "Во время сражения, разведчикам повстанцев" +
                "удалось похитить секретные планы," +
                "связанные с главным оружием Империи -" +
                "Звездой Смерти, бронированной космической" +
                "станцией, способной уничтожить целую планету." +
                "Преследуемая имперскими агентами принцесса" +
                "Лея спешит домой на своем звездном корабле." +
                "При ней находятся похищенные планы," +
                "которые могут спасти ее народ" +
                "и вернуть свободу галактике.", LocalDate.of(1978, 7, 21), 120);
        ValidationException thrown = assertThrows(ValidationException.class, () -> { filmController.addFilm(film); });
        assertEquals("Описание превышает 200 символов.", thrown.getMessage());
    }

    @Test
    public void shouldNotValidateOldReleaseDate() {
        Film film = new Film("Выход рабочих с фабрики «Люмьер»", "La sortie de l'usine Lumière à Lyon",
                LocalDate.of(1895, 3, 22), 1);
        ValidationException thrown = assertThrows(ValidationException.class, () -> { filmController.addFilm(film); });
        assertEquals("Дата релиза фильма должна быть после 1895-12-28", thrown.getMessage());

        Film film2 = new Film("Пограничный фильм", "",
                LocalDate.of(1895, 12, 28), 1);
        assertDoesNotThrow(() -> {filmController.addFilm(film2); });
    }

    @Test
    public void shouldNotValidateIfDurationIsNegative() {
        Film film = new Film("Фильм с отрицательной длительностью", "такое возможно вообще?",
                LocalDate.of(2000, 1, 1), -1);
        ValidationException thrown = assertThrows(ValidationException.class, () -> { filmController.addFilm(film); });
        assertEquals("Продолжительность фильма не может быть отрицательным.", thrown.getMessage());

        Film film2 = new Film("Фильм с нулевой длительностью", "но не в секундах",
                LocalDate.of(2000, 1, 1), 0);
        assertDoesNotThrow(() -> {filmController.addFilm(film2); });
    }

}
