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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FriendDaoTest {

    private final FriendDao friendDao;
    private final UserDbStorage userDbStorage;

    @BeforeAll
    public void beforeAll() {
        userDbStorage.createUser(User.builder()
                .email("first@first.ru")
                .login("first")
                .name("first")
                .birthday(LocalDate.of(2000, 10, 10))
                .build());
        userDbStorage.createUser(User.builder()
                .email("second@second.ru")
                .login("second")
                .name("second")
                .birthday(LocalDate.of(1998, 10, 10))
                .build());
        userDbStorage.createUser(User.builder()
                .email("common@common.ru")
                .login("common")
                .name("common")
                .birthday(LocalDate.of(1980, 10, 10))
                .build());
        userDbStorage.createUser(User.builder()
                .email("forth@forth.ru")
                .login("forth")
                .name("forth")
                .birthday(LocalDate.of(1980, 10, 10))
                .build());
    }

    @Test
    public void shouldAddFriend() {
        friendDao.addFriend(1, 2);
        List<User> userList = friendDao.getFriends(1);
        User actualFriend = userList.get(0);

        assertThat(actualFriend).hasFieldOrPropertyWithValue("login", "second");
    }

    @Test
    public void shouldDeleteFriend() {
        friendDao.addFriend(1, 2);
        friendDao.addFriend(1, 4);
        List<User> userListBefore = friendDao.getFriends(1);
        assertThat(userListBefore).hasSize(3);

        friendDao.deleteFriend(1, 4);
        List<User> userListAfter = friendDao.getFriends(1);
        assertThat(userListAfter).hasSize(2);
    }

    @Test
    public void shouldReturnCommonFriends() {
        friendDao.addFriend(1, 3);
        friendDao.addFriend(2, 3);

        List<User> commonList = friendDao.findCommonFriends(1, 2);
        User commonFriend = commonList.get(0);

        assertThat(commonList).hasSize(1);
        assertThat(commonFriend).hasFieldOrPropertyWithValue("login", "common");
    }
}
