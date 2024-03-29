package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendDao;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final FriendDao friendDao;
    int random = 0;

    @Autowired
    public UserService(UserStorage userStorage, FriendDao friendDao) {
        this.userStorage = userStorage;
        this.friendDao = friendDao;
    }

    public User createUser(User user) {
        user.setId(generateId());
        validate(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        validate(user);
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public void addFriend(int id, int friendId) {
        userStorage.getUserById(id);
        userStorage.getUserById(friendId);

        friendDao.addFriend(id, friendId);
    }

    public void deleteFriend(int id, int friendId) {
        userStorage.getUserById(id);
        userStorage.getUserById(friendId);

        friendDao.deleteFriend(id, friendId);
    }

    public List<User> getFriends(int id) {
        userStorage.getUserById(id);

        return friendDao.getFriends(id);
    }

    public List<User> findCommonFriends(int id, int friendId) {
        userStorage.getUserById(id);
        userStorage.getUserById(friendId);

        return friendDao.findCommonFriends(id, friendId);
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

    private int generateId() {
        return ++random;
    }
}
