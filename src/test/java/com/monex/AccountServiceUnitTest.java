package com.monex;

import com.monex.dao.AccountDao;
import com.monex.exception.*;
import com.monex.model.Account;
import com.monex.service.AccountServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceUnitTest {
    AccountDao accountDao = mock(AccountDao.class);
    Account account = mock(Account.class);
    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void callDaoGetUsersWhenServiceGetUsersIsCalled(){
        given(accountDao.getAllAccounts()).willReturn(null);
        accountServiceImpl.getAllAccounts();
        verify(accountDao,times(1)).getAllAccounts();
    }
    @Test
    public void callDaoGetUserByIdWhenServiceGetUserByIdIsCalled() throws NotFoundException {
        given(accountDao.getAccount("1")).willReturn(account);
        accountServiceImpl.getAccount("1");
        verify(accountDao,times(1)).getAccount("1");
    }
    @Test
    public void callDaoCreateUserWhenServiceCreateUserByIdIsCalled() throws AlreadyExistException {
        accountServiceImpl.createAccount(account);
        verify(accountDao,times(1)).createAccount(account);
    }

    @Test
    public void callDaoDepositMoneyWhenServiceDepositMoneyIsCalled() throws NotFoundException, NotSufficientBalanceException {
        accountServiceImpl.depositMoney("1", "10");
        verify(accountDao,times(1)).depositMoney("1", BigDecimal.valueOf(10));
    }

    @Test
    public void callDaoWithdrawMoneyWhenServiceWithdrawMoneyIsCalled() throws NotFoundException, NotSufficientBalanceException {
        accountServiceImpl.depositMoney("1", "10");
        verify(accountDao,times(1)).depositMoney("1",BigDecimal.valueOf(10));
    }

    @Test
    public void callDaoMakePaymentWhenServiceMakePaymentIsCalled() throws NotFoundException, NotSufficientBalanceException, SameAccountException, InvalidAmountException {
        accountServiceImpl.makePayment("1", "2","50");
        verify(accountDao,times(1)).makePayment("1","2", BigDecimal.valueOf(50));
    }
}
