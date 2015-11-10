/**
 * Created by Philip A Senger on November 10, 2015
 */
package com.sample.dao;

import com.sample.domain.EntityNotFoundException;
import com.sample.domain.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class UserDao {

    private static Map users;
    private static Long lastId;

    public UserDao() {
        users = new HashMap<>();
        lastId = 0L;
    }

    public Collection<User> getUsers() {
        return new HashSet<User>(users.values());
    }

    public User getUser(Long id) throws EntityNotFoundException {
        System.out.println("id = " + id);
        System.out.println("users = " + users.keySet());
        if (users.containsKey(id)) {
            return (User) users.get(id);
        }
        throw new EntityNotFoundException("User Not Found");
    }

    public User getUser(String username) throws EntityNotFoundException {
        if (users.containsKey(username)) {
            return (User) users.get(username);
        }
        throw new EntityNotFoundException("User Not Found");
    }

    public User addUser(User user) {
        if (users.containsKey(user.getUsername())) {
            throw new IllegalArgumentException("The user already exists");
        }
        user.setId(++lastId);
        user.setVersion(1);
        users.put(user.getId(), user);
        users.put(user.getUsername(), user);
        return user;
    }

    public User update(User user) {
        if (!users.containsKey(user.getUsername())) {
            throw new IllegalArgumentException("The user name does not exists");
        }
        if (!users.containsKey(user.getId())) {
            throw new IllegalArgumentException("The user id does not exists");
        }
        int version = user.getVersion() + 1;
        user.setVersion(version);
        users.put(user.getUsername(), user);
        users.put(user.getId(), user);
        return user;
    }
}
