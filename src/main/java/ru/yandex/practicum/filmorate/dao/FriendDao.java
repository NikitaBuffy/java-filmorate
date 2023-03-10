package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendDao {

    void addFriend(int id, int friendId);
    void deleteFriend(int id, int friendId);
    List<User> getFriends(int id);
    List<User> findCommonFriends(int id, int friendId);
}
