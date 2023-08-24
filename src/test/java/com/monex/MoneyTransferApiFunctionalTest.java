package com.monex;

import com.google.gson.JsonElement;
import com.monex.helper.TestResponse;
import com.monex.helper.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static java.lang.Thread.sleep;

//Final block in the end of every test to perform rollback manually since for the simplicity of in memory datastore
//I did not use real database where i could have used database rollback feature to declare tests to rollback after each test
public class MoneyTransferApiFunctionalTest {
    TestResponse res;
    @Before
    public  void beforeClass() throws InterruptedException {
        MoneyTransferAPI.main(null); //to spark the spark server
        sleep(3000);
    }

    @Test
    public void userAccountShouldBeCreated() {
        try {
            //WHEN
            res = Util.request("PUT", "/account/4?userId=1&balance=200&currencyCode=EUR");

            //THEN
            JsonElement json = res.jsonElement();
            Assert.assertEquals(200, res.status);
        }finally {
            Util.request("DELETE", "/account/4");
        }
    }


    @Test
    public void moneyIsSuccesfullyTransferredBetweenAccounts() {
        try {
            //WHEN
            res = Util.request("POST", "/moneytransfer?fromAccountId=1&toAccountId=2&amountToTransfer=20");

            //THEN
            JsonElement json = res.jsonElement();
            Assert.assertEquals(200, res.status);
        }finally {
            Util.request("POST", "/moneytransfer?fromAccountId=2&toAccountId=1&amountToTransfer=20");
        }
    }

    @Test
    public void moneyShouldBeSuccessfullyWIthdrawnFromAnAccount() {

        try {
            //WHEN
            res = Util.request("PUT", "/account/2/withdraw/20");

            //THEN
            JsonElement json = res.jsonElement();
            Assert.assertEquals(200, res.status);
        }finally {
            Util.request("PUT", "/account/2/deposit/20");
        }
    }

    @Test
    public void moneyShouldBeSuccessfullyDepositedToAnAccount() {
        try {
            //WHEN
            res = Util.request("PUT", "/account/2/deposit/20");

            //THEN
            JsonElement json = res.jsonElement();
            Assert.assertEquals(200, res.status);
        }finally {
            Util.request("PUT", "/account/2/withdraw/20");
        }
    }
}
