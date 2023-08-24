package com.monex.service;

import com.monex.dao.UserDao;
import com.monex.exception.AlreadyExistException;
import com.monex.exception.NotFoundException;
import com.monex.model.User;

import javax.inject.Inject;
import java.util.Collection;

public class UserServiceImpl implements UserService {
    private UserDao userDao;
    @Inject
    public UserServiceImpl(final UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Collection<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public User getUser(String id) throws NotFoundException {
        return userDao.getUser(id);
    }

    @Override
    public void createUser(User user) throws AlreadyExistException {
        userDao.createUser(user);
    }
    @Override
    public User updateUser(User user) throws NotFoundException {
        return userDao.updateUser(user);
    }

    @Override
    public boolean userExist(final String id){
        return userDao.userExist(id);
    }

    @Override
    public void deleteUser(final String userId) throws NotFoundException {
        userDao.deleteUser(userId);
    }
}
