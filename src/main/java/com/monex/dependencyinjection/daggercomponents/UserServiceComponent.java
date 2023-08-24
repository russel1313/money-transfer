package com.monex.dependencyinjection.daggercomponents;

import com.monex.dependencyinjection.daggermodules.UserServiceModule;
import com.monex.service.UserServiceImpl;
import dagger.Component;

@Component(modules = UserServiceModule.class)
public interface UserServiceComponent {
    UserServiceImpl buildUserService();
}
