package com.monex;

import com.monex.dependencyinjection.daggercomponents.AccountServiceComponent;
import com.monex.dependencyinjection.daggercomponents.DaggerAccountServiceComponent;
import com.monex.exception.*;
import com.monex.model.Account;
import com.monex.service.AccountService;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;

//Final block in the end of every test to perform rollback manually since for the simplicity of in memory datastore
//I did not use real database where i could have used database rollback feature to declare tests to rollback after each test
public class AccountServiceIntegrationTest {
    AccountServiceComponent accountServiceComponent = DaggerAccountServiceComponent.create();
    AccountService accountService = accountServiceComponent.buildAccountService();

    @Test
    public void getAllAccounts(){
        Collection<Account> accounts = accountService.getAllAccounts();
        Assert.assertEquals(accounts.size(),2);
    }

    @Test
    public void createNewAccount() throws AlreadyExistException, NotFoundException {
        try {
            Account account = Account.builder().withAccountID("3").withUserId("2")
                    .withCurrency(Currency.getInstance("EUR")).withBalance(BigDecimal.valueOf(50)).build();
            accountService.createAccount(account);
            Collection<Account> accounts = accountService.getAllAccounts();
            Assert.assertEquals(accounts.size(), 3);
        }finally {
            accountService.deleteAccount("3");
        }
    }

    @Test
    public void deleteAccount() throws NotFoundException, AlreadyExistException {
        try {
            accountService.deleteAccount("2");
            Collection<Account> accounts = accountService.getAllAccounts();
            Assert.assertEquals(1, accounts.size());
        }finally {
            Account account = Account.builder().withAccountID("2").withUserId("2")
                    .withBalance(BigDecimal.valueOf(1000)).withCurrency(Currency.getInstance("EUR")).build();
            accountService.createAccount(account);
        }
    }

    @Test
    public void withdrawMoneyFromAccount() throws NotSufficientBalanceException, NotFoundException {
        try {
            BigDecimal amountBeforeWithdrawl = accountService.getAccountBalance("2");
            accountService.withdrawMoney("2","10");
            Collection<Account> accounts = accountService.getAllAccounts();
            Assert.assertEquals(amountBeforeWithdrawl.subtract(BigDecimal.valueOf(10)), accountService.getAccountBalance("2"));
        }finally {
            accountService.depositMoney("2","10");
        }
    }

    @Test
    public void depositMoneyFromAccount() throws NotSufficientBalanceException, NotFoundException {
        try {
            BigDecimal amountBeforeDeposit = accountService.getAccountBalance("2");
            accountService.depositMoney("2","10");
            Assert.assertEquals(amountBeforeDeposit.add(BigDecimal.valueOf(10)), accountService.getAccountBalance("2"));
        }finally {
            accountService.withdrawMoney("2","10");
        }
    }

    @Test
    public void transferMoneyBetweenAccount() throws NotSufficientBalanceException, NotFoundException, SameAccountException, InvalidAmountException {
        try {
            BigDecimal amountFromAccountBeforeDeposit = accountService.getAccountBalance("1");
            BigDecimal amountToAccountBeforeDeposit = accountService.getAccountBalance("2");
            accountService.makePayment("1","2","10");
            Assert.assertEquals(amountFromAccountBeforeDeposit.subtract(BigDecimal.valueOf(10)), accountService.getAccountBalance("1"));
            Assert.assertEquals(amountToAccountBeforeDeposit.add(BigDecimal.valueOf(10)), accountService.getAccountBalance("2"));
        }finally {
            accountService.makePayment("2","1","10");
        }
    }
}
