package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final HashMap<Integer, User> userMap = new HashMap<>();
    int random = 0;

    private int generateId() {
        return ++random;
    }

    private void validate(User user) {
        if ((user.getEmail() == null || user.getEmail().isBlank()) || !user.getEmail().contains("@")) {
            log.warn("Валидация не пройдена: email либо пустой, либо не содержит @.");
            throw new ValidationException("Адрес электронной почты не может быть пустым и должен содержать символ @.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Валидация не пройдена: логин либо пустой, либо содержит пробелы.");
            throw new ValidationException("Логин не должен быть пустым и не должен содержать пробелы.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Валидация не пройдена: дата рождения в будущем.");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        validate(user);
        user.setId(generateId());
        userMap.put(user.getId(), user);
        log.info("Создан пользователь с ID {}: {}", user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        validate(user);
        if (userMap.containsKey(user.getId())) {
            userMap.put(user.getId(), user);
        } else {
            log.warn("Не найден пользователь при попытке обновления.");
            throw new UserNotFoundException("Пользователь с ID " + user.getId() + " не существует.");
        }
        log.info("Обновлен пользователь с ID {}", user.getId());
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        List<User> userList = new ArrayList<>();
        for (Map.Entry<Integer, User> entry : userMap.entrySet()) {
            userList.add(entry.getValue());
        }
        return userList;
    }
}
