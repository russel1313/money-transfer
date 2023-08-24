package com.monex.dependencyinjection.daggermodules;

import com.monex.dao.UserDao;
import com.monex.dao.UserDaoImpl;
import com.monex.service.UserServiceImpl;
import dagger.Module;
import dagger.Provides;

@Module
public class UserServiceModule {
    @Provides
    public UserDao provideUserDao(){
        return UserDaoImpl.getInstance();
    }
    @Provides
    public UserServiceImpl provideUserService(){
        return new UserServiceImpl(this.provideUserDao());
    }
}
