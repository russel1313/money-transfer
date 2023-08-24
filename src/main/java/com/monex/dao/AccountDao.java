package com.monex.dao;

import com.monex.exception.AlreadyExistException;
import com.monex.exception.NotFoundException;
import com.monex.exception.NotSufficientBalanceException;
import com.monex.model.Account;

import java.math.BigDecimal;
import java.util.Collection;

public interface AccountDao {
    Collection<Account> getAllAccounts();
    Account getAccount(String accountId) throws NotFoundException;
    BigDecimal getAccountBalance(String accountId) throws NotFoundException;
    void createAccount(Account accountId) throws AlreadyExistException;
    void deleteAccount(String accountId) throws NotFoundException;
    void withdrawMoney(String accountId, BigDecimal amountToWithdraw) throws NotFoundException, NotSufficientBalanceException;
    void depositMoney(String accountId, BigDecimal amountToDeposit) throws NotFoundException;
    void makePayment(String transferFrom, String transferTo, BigDecimal amountToTransfer) throws NotSufficientBalanceException, NotFoundException;
}
