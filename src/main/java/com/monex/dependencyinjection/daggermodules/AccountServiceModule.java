package com.monex.dependencyinjection.daggermodules;

import com.monex.dao.AccountDao;
import com.monex.dao.AccountDaoImpl;
import com.monex.service.AccountServiceImpl;
import dagger.Module;
import dagger.Provides;

@Module
public class AccountServiceModule {
    @Provides
    public AccountDao provideAccountDao(){
        return AccountDaoImpl.getInstance();
    }
    @Provides
    public AccountServiceImpl provideAccountService(){
        return new AccountServiceImpl(this.provideAccountDao());
    }
}
