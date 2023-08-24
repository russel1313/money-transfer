package com.monex;

import com.monex.dependencyinjection.daggercomponents.DaggerUserServiceComponent;
import com.monex.dependencyinjection.daggercomponents.UserServiceComponent;
import com.monex.exception.AlreadyExistException;
import com.monex.exception.NotFoundException;
import com.monex.model.User;
import com.monex.service.UserService;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

//Final block in the end of every test to perform rollback manually since for the simplicity of in memory datastore
//I did not use real database where i could have used database rollback feature to declare tests to rollback after each test
public class UserServiceIntegrationTest {
    UserServiceComponent userServiceComponent = DaggerUserServiceComponent.create();
    UserService userService = userServiceComponent.buildUserService();

    @Test
    public void getAllAccounts(){
        Collection<User> users = userService.getAllUsers();
        Assert.assertEquals(users.size(),2);
    }

    @Test
    public void createNewAccount() throws AlreadyExistException, NotFoundException {
        try {
            User user = User.builder().withId("3").withFirstName("Russell3").withLastName("Hessampour3")
                    .withEmail("r.hessampour3@gmail.com").build();
            userService.createUser(user);
            Collection<User> users = userService.getAllUsers();
            Assert.assertEquals(users.size(), 3);
        }finally {
            userService.deleteUser("3");
        }
    }

    @Test
    public void updateNewAccount() throws NotFoundException {
        try {
            User user = User.builder().withId("2").withFirstName("Russell2").withLastName("Hessampour2")
                    .withEmail("r.hessampour2@gmail.com").build();
            User updatedUser = userService.updateUser(user);
            Assert.assertEquals("Russell2", updatedUser.getFirstName());
            Assert.assertEquals("Hessampour2", updatedUser.getLastName());
            Assert.assertEquals("r.hessampour2@gmail.com", updatedUser.getEmail());
        }finally {
            User user = User.builder().withId("2").withFirstName("Russell2")
                    .withLastName("Hessampour2").withEmail("r.hessampour2@gmail.com").build();
            userService.updateUser(user);
        }
    }

    @Test
    public void deleteUser() throws NotFoundException, AlreadyExistException {
        try {
            userService.deleteUser("2");
            Collection<User> accounts = userService.getAllUsers();
            Assert.assertEquals(accounts.size(), 1);
        }finally {
            User user = User.builder().withId("2").withFirstName("Russell2")
                    .withLastName("Hessampour2").withEmail("r.hessampour2@gmail.com").build();
            userService.createUser(user);
        }
    }
}
