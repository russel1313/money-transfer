package com.monex.dependencyinjection.daggercomponents;

import com.monex.dependencyinjection.daggermodules.AccountServiceModule;
import com.monex.service.AccountServiceImpl;
import dagger.Component;

@Component(modules = AccountServiceModule.class)
public interface AccountServiceComponent {
    AccountServiceImpl buildAccountService();
}
