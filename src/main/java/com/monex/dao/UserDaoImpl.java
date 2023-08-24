package com.monex.dao;

import com.monex.exception.AlreadyExistException;
import com.monex.exception.NotFoundException;
import com.monex.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class UserDaoImpl implements UserDao {
    private static UserDaoImpl userDaoImpl = null;
    User user;
    static Map<String, User> usersDatabase = new ConcurrentHashMap<>();
    private UserDaoImpl(){
        //in memory database for the sake of simplicity of task
        user = User.builder().withId("1").withFirstName("Russell")
                .withLastName("Hessampour").withEmail("r.hessampour@gmail.com").build();
        usersDatabase.put(user.getUserId(), user);
        user = User.builder().withId("2").withFirstName("Russell2")
                .withLastName("Hessampour2").withEmail("r.hessampour2@gmail.com").build();
        usersDatabase.put(user.getUserId(), user);
    }

    //we can place synchronized inside method as well with double lock checking
    // to increase performance but i have placed it at method level for sake of simplicity of this task.
    public static synchronized UserDaoImpl getInstance(){
        if(userDaoImpl==null){
            userDaoImpl = new UserDaoImpl();
        }
        return userDaoImpl;
    }

    @Override
    public Collection<User> getAllUsers() {
        return usersDatabase.values();
    }

    @Override
    public User getUser(String id) throws NotFoundException {
        return usersDatabase.values().stream().filter(user -> user.getUserId().equals(id)).findFirst()
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public void createUser(User user) throws AlreadyExistException {
        if(this.userExist(user.getUserId())){
            throw new AlreadyExistException("User already exists in database");
        }else {
            Objects.requireNonNull(
                    user.getFirstName(), "First Name cannot be null");
            Objects.requireNonNull(
                    user.getLastName(), "Last Name cannot be null");
            Objects.requireNonNull(
                    user.getEmail(), "Email cannot be null");
            usersDatabase.put(user.getUserId(), user);
        }
    }

    @Override
    public User updateUser(User user) throws NotFoundException {
        if(this.userExist(user.getUserId())){
            Objects.requireNonNull(
                    user.getFirstName(), "First Name cannot be null");
            Objects.requireNonNull(
                    user.getLastName(), "Last Name cannot be null");
            Objects.requireNonNull(
                    user.getEmail(), "Email cannot be null");
            usersDatabase.put(user.getUserId(), user);
        }else {
            throw new NotFoundException("User with this id does not exists in database");
        }
        return user;
    }

    @Override
    public boolean userExist(String id) {
        boolean userExist = false;
        if(usersDatabase.values().stream().anyMatch(user -> user.getUserId().equals(id))){
            userExist=true;
        }
        return userExist;
    }

    @Override
    public void deleteUser(final String userId) throws NotFoundException {
        if(usersDatabase.containsKey(userId)) {
            usersDatabase.remove(userId);
        }else{
            throw new NotFoundException("User not found in database");
        }
    }
}
