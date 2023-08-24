package com.monex.dao;

import com.monex.exception.AlreadyExistException;
import com.monex.exception.NotFoundException;
import com.monex.exception.NotSufficientBalanceException;
import com.monex.model.Account;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AccountDaoImpl implements AccountDao {
    Account account;
    Map<String, Account> accountsDatabase = new ConcurrentHashMap<>();
    private  ReadWriteLock accountDatabaseLock = new ReentrantReadWriteLock();
    //in memory database
    public AccountDaoImpl(){
        account = Account.builder().withAccountID("1").withUserId("1")
                .withBalance(BigDecimal.valueOf(2000)).withCurrency(Currency.getInstance("EUR")).build();
        accountsDatabase.put(account.getAccountID(), account);
        account = Account.builder().withAccountID("2").withUserId("2")
                .withBalance(BigDecimal.valueOf(1000)).withCurrency(Currency.getInstance("EUR")).build();
        accountsDatabase.put(account.getAccountID(), account);
    }

    private static AccountDaoImpl accountDaoImpl = null;
    //we can place synchronized inside method as well with double lock checking mechanism
    // to increase performance but i have placed it at method level for sake of simplicity of this task.
    public static synchronized AccountDaoImpl getInstance(){
        if(accountDaoImpl==null){
            accountDaoImpl = new AccountDaoImpl();
        }
        return accountDaoImpl;
    }

    @Override
    public Account getAccount(String accountId) throws NotFoundException {
        return accountsDatabase.values().stream().filter(account -> account.getAccountID().equals(accountId)).findFirst()
                .orElseThrow(() -> new NotFoundException("Account number not found"));
    }

    @Override
    public BigDecimal getAccountBalance(String accountId) throws NotFoundException {
        accountDatabaseLock.readLock().lock();
        try {
        Optional<Account> account = accountsDatabase.values().stream().filter(acc -> acc.getAccountID().equals(accountId)).findFirst();
        if(account.isPresent()){
            return account.get().getBalance();
        }
        else {
           throw new NotFoundException("Account number not found");
        }
        }finally {
            accountDatabaseLock.readLock().unlock();
        }
    }

    @Override
    public void createAccount(final Account account) throws AlreadyExistException {
        if(accountsDatabase.containsKey(account.getAccountID())){
            throw new AlreadyExistException("Account number already exist");
        }
        else{
            accountsDatabase.put(account.getAccountID(),account);
        }
    }

    @Override
    public void deleteAccount(String accountId) throws NotFoundException {
        if(accountsDatabase.containsKey(accountId)){
            accountsDatabase.remove(accountId);
        }
        else{
            throw new NotFoundException("Account id not found");
        }

    }

    @Override
    public Collection<Account> getAllAccounts() {
        return accountsDatabase.values();
    }

    @Override
    public void withdrawMoney(final String accountId, final BigDecimal amountToWithdraw) throws NotFoundException, NotSufficientBalanceException {
        accountDatabaseLock.writeLock().lock();
        try {
        Optional<Account> account = accountsDatabase.values().stream().filter(acc -> acc.getAccountID().equals(accountId)).findFirst();
        if(account.isPresent()){
            if(account.get().getBalance().compareTo(amountToWithdraw) < 0){
                throw new NotSufficientBalanceException("Balance is not sufficient for withdrawl");
            }
            account.get().setBalance(account.get().getBalance().subtract(amountToWithdraw));
            accountsDatabase.put(accountId, account.get());
        }
        else {
            throw new NotFoundException("Account number not found");
        }
        }finally {
            accountDatabaseLock.writeLock().unlock();
        }
    }

    @Override
    public void depositMoney(final String accountId, final BigDecimal amountToDeposit) throws NotFoundException {
        accountDatabaseLock.writeLock().lock();
        try {
            Optional<Account> account = accountsDatabase.values().stream().filter(acc -> acc.getAccountID().equals(accountId)).findFirst();
            if (account.isPresent()) {
                account.get().setBalance(account.get().getBalance().add(amountToDeposit));
                accountsDatabase.put(accountId, account.get());
            } else {
                throw new NotFoundException("Account number not found");
            }
        }finally {
            accountDatabaseLock.writeLock().unlock();
        }
    }

    @Override
    public void makePayment(String transferFrom, String transferTo, BigDecimal amountToTransfer) throws NotSufficientBalanceException, NotFoundException {
        accountDatabaseLock.writeLock().lock();
        try {
            Account transferFromAccount = this.getAccount(transferFrom);
            Account transferToAccount = this.getAccount(transferTo);
            if (transferFromAccount.getBalance().compareTo(amountToTransfer) < 0) {
                throw new NotSufficientBalanceException("Balance is not sufficient to make transaction");
            } else {
                transferFromAccount.setBalance(transferFromAccount.getBalance().subtract(amountToTransfer));
                transferToAccount.setBalance(transferToAccount.getBalance().add(amountToTransfer));
            }
        }finally {
            accountDatabaseLock.writeLock().unlock();
        }
    }

}
