package com.monex.service;

import com.monex.exception.AlreadyExistException;
import com.monex.exception.NotFoundException;
import com.monex.model.User;

import java.util.Collection;

public interface UserService {
    Collection<User> getAllUsers();

    User getUser(String id) throws NotFoundException;

    void createUser(User user) throws AlreadyExistException;

    User updateUser(User user) throws NotFoundException;

    boolean userExist(String userId);

    void deleteUser(String userId) throws NotFoundException;
}
