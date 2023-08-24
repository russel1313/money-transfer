package com.monex;

import com.monex.dao.AccountDao;
import com.monex.dao.AccountDaoImpl;
import com.monex.exception.AlreadyExistException;
import com.monex.exception.NotFoundException;
import com.monex.exception.NotSufficientBalanceException;
import com.monex.model.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;

//In case of real database we would have to rollback the trasanctions after every test so that we leave database
// in the same state as it was before the tests as we do in case of exceptions at Dao Layer
@FixMethodOrder(MethodSorters.DEFAULT)
public class AccountDaoUnitTest {
    private AccountDao accountDao = AccountDaoImpl.getInstance();

    @Before
    public void depositMoneyIntoAccount() throws NotFoundException, NotSufficientBalanceException {
        try {
            accountDao.depositMoney("1", BigDecimal.valueOf(200));
        }finally {
            accountDao.withdrawMoney("1", BigDecimal.valueOf(200));
        }
    }

    @Test
    public void getAllAccounts(){
        Collection<Account> users = accountDao.getAllAccounts();
        Assert.assertEquals(users.size(),2);
    }
    @Test
    public void createNewAccount() throws AlreadyExistException, NotFoundException {
        try {
            Account account = Account.builder().withAccountID("3").
                    withBalance(BigDecimal.valueOf(20)).withCurrency(Currency.getInstance("EUR")).withUserId("1").build();
            accountDao.createAccount(account);
            Collection<Account> accounts = accountDao.getAllAccounts();
            Assert.assertEquals(3, accounts.size());
        }finally{ //rollback transaction
            accountDao.deleteAccount("3");
        }
    }


    @Test(expected = NotFoundException.class)
    public void deleteExistingAccountThrowsExceptionIfInvalid() throws NotFoundException {
        accountDao.deleteAccount("5");
    }

    @Test
    public void depositMoneyShouldBeSuccessfull() throws NotFoundException, NotSufficientBalanceException {
        try {
            BigDecimal moneyInAccountBefore = accountDao.getAccountBalance("1");
            accountDao.depositMoney("1", BigDecimal.valueOf(50));
            BigDecimal moneyInAccountAfterExpected = moneyInAccountBefore.add(BigDecimal.valueOf(50));
            Assert.assertEquals(moneyInAccountAfterExpected, accountDao.getAccountBalance("1"));
        }finally { //rollback transaction
            accountDao.withdrawMoney("1", BigDecimal.valueOf(50));
        }
    }

    @Test
    public void withdrawMoneyShouldBeSuccessfull() throws NotFoundException, NotSufficientBalanceException {
        try {
            BigDecimal moneyInAccountBefore = accountDao.getAccountBalance("1");
            accountDao.withdrawMoney("1", BigDecimal.valueOf(50));
            BigDecimal moneyInAccountAfterExpected = moneyInAccountBefore.subtract(BigDecimal.valueOf(50));
            Assert.assertEquals(moneyInAccountAfterExpected, accountDao.getAccountBalance("1"));
        }finally { //rollback transaction
            accountDao.depositMoney("1", BigDecimal.valueOf(50));
        }
    }

    @Test
    public void moneyTransferShouldBeSuccessfull() throws NotFoundException, NotSufficientBalanceException {
        try {
            BigDecimal moneyFromAccountBefore = accountDao.getAccountBalance("1");
            BigDecimal moneyToAccountBefore = accountDao.getAccountBalance("2");
            accountDao.makePayment("1", "2", BigDecimal.valueOf(5));
            BigDecimal moneyFromAccountExpected = moneyFromAccountBefore.subtract(BigDecimal.valueOf(5));
            Assert.assertEquals(moneyFromAccountExpected, accountDao.getAccountBalance("1"));
            BigDecimal moneyToAccountBeforeExpected = moneyToAccountBefore.add(BigDecimal.valueOf(5));
            Assert.assertEquals(moneyToAccountBeforeExpected, accountDao.getAccountBalance("2"));
        }finally { //rollback transaction
            accountDao.makePayment("2", "1", BigDecimal.valueOf(5));
        }
    }
}
