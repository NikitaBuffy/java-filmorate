package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MpaRatingDaoTest {

    private final MpaRatingDao mpaRatingDao;

    @Test
    public void shouldReturnAllMpa() {
        List<MpaRating> mpaList = mpaRatingDao.getAllMpa();

        assertThat(mpaList.size()).isEqualTo(5);
    }

    @Test
    public void shouldReturnMpaById() {
        MpaRating mpa = mpaRatingDao.getMpaById(1);

        assertThat(mpa).hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    public void shouldNotFoundMpa() {
        assertThatExceptionOfType(MpaNotFoundException.class).isThrownBy(() -> mpaRatingDao.getMpaById(10));
    }

}
