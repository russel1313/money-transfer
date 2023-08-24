package com.monex;

import com.monex.dao.UserDao;
import com.monex.dao.UserDaoImpl;
import com.monex.exception.AlreadyExistException;
import com.monex.exception.NotFoundException;
import com.monex.model.User;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Collection;

//In case of real database we would have to roll back the transactions after every test so that we leave database
// in the same state as it was before the tests as we do in case of exceptions at Dao Layer
@FixMethodOrder(MethodSorters.DEFAULT)
public class UserDaoUnitTest {
    private UserDao userDao = UserDaoImpl.getInstance();

    @Test
    public void getAllUsers(){
        Collection<User> users = userDao.getAllUsers();
        Assert.assertEquals(users.size(),2);
    }
    @Test
    public void createNewUser() throws AlreadyExistException, NotFoundException {
        try {
            User user = User.builder().withId("3").withFirstName("Russell").withLastName("Hessampour").withEmail("r.hessampour@gmail.com").build();
            userDao.createUser(user);
            Collection<User> users = userDao.getAllUsers();
            Assert.assertEquals(users.size(), 3);
        }finally {
            userDao.deleteUser("3");
        }
    }


    @Test
    public void updateExistingUser() throws NotFoundException {
        User user = User.builder().withId("2").withFirstName("Russell").withLastName("Hessampour").withEmail("r.hessampour@gmail.com").build();
        userDao.updateUser(user);
        User updatedUser = userDao.getUser("2");
        Assert.assertEquals(updatedUser.getFirstName(),"Russell");
        Assert.assertEquals(updatedUser.getLastName(),"Hessampour");
        Assert.assertEquals(updatedUser.getEmail(),"r.hessampour@gmail.com");
    }

    @Test
    public void userExists(){
        Assert.assertEquals(true,userDao.userExist("1"));
    }

    @Test(expected = NotFoundException.class)
    public void deleteExistingUserThrowsExceptionIfInvalid() throws NotFoundException {
        userDao.deleteUser("4");
    }

}
