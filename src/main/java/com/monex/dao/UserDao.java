package com.monex.dao;

import com.monex.exception.AlreadyExistException;
import com.monex.exception.NotFoundException;
import com.monex.model.User;

import java.util.Collection;

public interface UserDao {
    Collection<User> getAllUsers();

    User getUser(String id) throws NotFoundException;

    void createUser(User user) throws AlreadyExistException;

    User updateUser(User user) throws NotFoundException;

    boolean userExist(String id);

    void deleteUser(String userId) throws NotFoundException;
}
