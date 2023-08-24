package com.monex.controller;

import com.google.gson.Gson;
import com.monex.dependencyinjection.daggercomponents.AccountServiceComponent;
import com.monex.dependencyinjection.daggercomponents.DaggerAccountServiceComponent;
import com.monex.model.Account;
import com.monex.response.StandardResponse;
import com.monex.response.StatusResponse;
import com.monex.service.AccountService;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

import static spark.Spark.*;

public class AccountController {
    AccountServiceComponent accountServiceComponent = DaggerAccountServiceComponent.create();
    AccountService accountService = accountServiceComponent.buildAccountService();

    public void registerAccountApiRoutes() {

        //get all accounts
        get("/account/all", (req, res) -> {
            return new Gson().toJson(
                    new StandardResponse(StatusResponse.SUCCESS,new Gson()
                            .toJsonTree(accountService.getAllAccounts())));
        });

        //get account details by account id
        get("/account/:id", (req, res) -> {
            String accountId = Objects.requireNonNull(req.params(":id"));
            return new Gson().toJson(
                    new StandardResponse(StatusResponse.SUCCESS,new Gson()
                            .toJsonTree(accountService.getAccount(accountId))));
        });

        //get balance of account
        get("/account/:id/balance", (req, res) -> {
            String accountId = Objects.requireNonNull(req.params(":id"));
            return new Gson().toJson(
                    new StandardResponse(StatusResponse.SUCCESS,new Gson()
                            .toJsonTree(accountService.getAccountBalance(accountId))));
        });

        //create a new account
        put("/account/:id", (req, res) -> {
            String accountId = Objects.requireNonNull(req.params(":id"));
            String userId = Objects.requireNonNull(req.queryParams("userId"));
            String balance = Objects.requireNonNull(req.queryParams("balance"));
            String currencyCode = Objects.requireNonNull(req.queryParams("currencyCode"));
            Account account = Account.builder().withAccountID(accountId).withUserId(userId)
                    .withBalance(BigDecimal.valueOf(Long.valueOf(balance))).
                            withCurrency(Currency.getInstance(currencyCode)).build();
            accountService.createAccount(account);
            return new Gson().toJson(
                    new StandardResponse(StatusResponse.SUCCESS,"New account has been created"));
        });

        //delete a particular account
        delete("/account/:id", (req, res) -> {
            String accountId = Objects.requireNonNull(req.params(":id"));
            accountService.deleteAccount(accountId);
            return new Gson().toJson(
                    new StandardResponse(StatusResponse.SUCCESS,
                            "Account with %s id has been deleted", accountId));
        });

        //withdraw money from account
        put("/account/:id/withdraw/:amount", (req, res) -> {
            String accountId = Objects.requireNonNull(req.params(":id"));
            String amountToWithdraw = Objects.requireNonNull(req.params(":amount"));
            accountService.withdrawMoney(accountId, amountToWithdraw);
            return new Gson().toJson(
                    new StandardResponse(StatusResponse.SUCCESS,
                            "Amount has been withdrawn from account with id %s", accountId));
        });

        //deposit money to account
        put("/account/:id/deposit/:amount", (req, res) -> {
            String accountId = Objects.requireNonNull(req.params(":id"));
            String amountToDeposit = Objects.requireNonNull(req.params(":amount"));
            accountService.depositMoney(accountId, amountToDeposit);
            return new Gson().toJson(
                    new StandardResponse(StatusResponse.SUCCESS,
                            "Amount has been deposited to account with id %s", accountId));
        });

        post("/moneytransfer", (req, res) ->
        {
            String fromAccountId = Objects.requireNonNull(req.queryParams("fromAccountId"));
            String toAccountId = Objects.requireNonNull(req.queryParams("toAccountId"));
            String amountToTransfer = Objects.requireNonNull(req.queryParams("amountToTransfer"));
            accountService.makePayment(fromAccountId, toAccountId, amountToTransfer);
            return new Gson().toJson(
                    new StandardResponse(StatusResponse.SUCCESS,
                            "Money has been transferred successfully"));
        });
    }
}
