package com.monex.service;

import com.monex.dao.AccountDao;
import com.monex.exception.*;
import com.monex.model.Account;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Collection;

public class AccountServiceImpl implements AccountService {
    private AccountDao accountDao;
    @Inject
    public AccountServiceImpl(final AccountDao accountDao) {
        this.accountDao = accountDao;
    }
    @Override
    public Account getAccount(final String accountId) throws NotFoundException {
        return accountDao.getAccount(accountId);
    }

    @Override
    public BigDecimal getAccountBalance(final String accountId) throws NotFoundException {
        return accountDao.getAccountBalance(accountId);
    }

    @Override
    public void deleteAccount(final String accountId) throws NotFoundException {
        accountDao.deleteAccount(accountId);
    }

    @Override
    public Collection<Account> getAllAccounts() {
        return accountDao.getAllAccounts();
    }

    @Override
    public void createAccount(final Account account) throws AlreadyExistException {
        accountDao.createAccount(account);
    }

    @Override
    public void withdrawMoney(final String accountId, final String amountToWithdraw) throws NotFoundException, NotSufficientBalanceException {
        BigDecimal amountToWithdrawInDecimal = BigDecimal.valueOf(Long.valueOf(amountToWithdraw));
        accountDao.withdrawMoney(accountId, amountToWithdrawInDecimal);
    }

    @Override
    public void depositMoney(final String accountId, final String amountToWithdraw) throws NotFoundException, NotSufficientBalanceException {
        BigDecimal amountToWithdrawInDecimal = BigDecimal.valueOf(Long.valueOf(amountToWithdraw));
        accountDao.depositMoney(accountId, amountToWithdrawInDecimal);
    }

    @Override
    public void makePayment(final String transferFrom, final String transferTo, final String amountToTransfer) throws NotSufficientBalanceException, NotFoundException, SameAccountException, InvalidAmountException {
        if(transferFrom.equals(transferTo)){
            throw new SameAccountException("Amount can not be transferred between same accounts");
        }
        BigDecimal amountToTransferInDecimal = BigDecimal.valueOf(Long.valueOf(amountToTransfer));
        if(amountToTransferInDecimal.compareTo(BigDecimal.ZERO) <= 0 ){ //if amountToTransfer is not a natural number
            throw new InvalidAmountException("Invalid amount entered");
        }
        accountDao.makePayment(transferFrom, transferTo, amountToTransferInDecimal);
    }
}
