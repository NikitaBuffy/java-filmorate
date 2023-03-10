package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserDbStorageTest {

    private final UserDbStorage userDbStorage;

    @BeforeAll
    public void beforeAll() {
        userDbStorage.createUser(User.builder()
                .email("test@test.ru")
                .login("tester")
                .name("Tester")
                .birthday(LocalDate.of(2000, 10, 10))
                .build());
    }

    @Test
    public void shouldFindUserById() {
        Optional<User> userOptional = Optional.ofNullable(userDbStorage.getUserById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void shouldUpdateUser() {
        User updatedUser = User.builder()
                .id(1)
                .email("nikita@mail.ru")
                .login("tester")
                .name("Tester")
                .birthday(LocalDate.of(2000, 10, 10))
                .build();

        Optional<User> userOptional = Optional.ofNullable(userDbStorage.updateUser(updatedUser));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "nikita@mail.ru")
                );
    }

    @Test
    public void shouldReturnUsers() {
        userDbStorage.createUser(User.builder()
                .email("sec@sec.ru")
                .login("second")
                .name("second")
                .birthday(LocalDate.of(1998, 10, 10))
                .build());
        List<User> userList = userDbStorage.getUsers();
        assertThat(userList.size()).isEqualTo(2);
    }

    @Test
    public void shouldThrowUserNotFoundException() {
        assertThatExceptionOfType(UserNotFoundException.class).isThrownBy(() -> userDbStorage.getUserById(5));
    }
}
