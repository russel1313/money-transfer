package com.monex.service;

import com.monex.exception.*;
import com.monex.model.Account;

import java.math.BigDecimal;
import java.util.Collection;

public interface AccountService {
    Collection<Account> getAllAccounts();
    Account getAccount(String accountId) throws NotFoundException;
    BigDecimal getAccountBalance(String accoundId) throws NotFoundException;
    void deleteAccount(String accoundId) throws NotFoundException;
    void createAccount(Account account) throws AlreadyExistException;
    void withdrawMoney(String accountId, String amountToWithdraw) throws NotFoundException, NotSufficientBalanceException;
    void depositMoney(String accountId, String amountToDepsit) throws NotFoundException, NotSufficientBalanceException;
    void makePayment(String transferFrom, String transferTo, String amountToTransfer) throws NotSufficientBalanceException, NotFoundException, SameAccountException, InvalidAmountException;
}
