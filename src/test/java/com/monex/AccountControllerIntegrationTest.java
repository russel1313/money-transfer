package com.monex;

import com.google.gson.JsonElement;
import com.monex.helper.TestResponse;
import com.monex.helper.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;

//Final block in the end of every test to perform rollback manually since for the simplicity of in memory datastore
//I did not use real database where i could have used database rollback feature to declare tests to rollback after each test
public class AccountControllerIntegrationTest {

    @Before
    public void beforeClass() throws InterruptedException {
        MoneyTransferAPI.main(null); //to spark the spark server
        sleep(3000);
    }

    @Test
    public void getListOfAllAccounts() {
        TestResponse res = Util.request("GET", "/account/all");
        JsonElement json = res.jsonElement();
        Assert.assertEquals(200, res.status);
        assertEquals(2, json.getAsJsonObject().size());
    }

    @Test
    public void aNewAccountShouldBeCreated() {
        try {
            TestResponse res = Util.request("PUT", "/account/3?userId=1&balance=200&currencyCode=EUR");
            Assert.assertEquals(200, res.status);
        }finally {
            Util.request("DELETE", "/account/3");
        }
    }

    @Test
    public void amountCanBeDepositedInAnAccount() {
        try {
            TestResponse res = Util.request("PUT", "/account/2/deposit/20");
            Assert.assertEquals(200, res.status);
        }finally {
            Util.request("PUT", "/account/2/withdraw/20");
        }
    }

    @Test
    public void amountCanBeWithdrawnFromAnAccount() {
        try {
            TestResponse res = Util.request("PUT", "/account/2/withdraw/20");
            Assert.assertEquals(200, res.status);
        }finally {
            Util.request("PUT", "/account/2/deposit/20");
        }
    }

    @Test
    public void balanceForAnAccountShouldBeCorrect() {
        TestResponse res = Util.request("GET", "/account/2/balance");
        JsonElement json = res.jsonElement();
        Assert.assertEquals(200, res.status);
        assertEquals(BigDecimal.valueOf(1000), json.getAsJsonObject().get("data").getAsBigDecimal());
    }


    @Test
    public void anExistingAccountShouldBeDeleted() {
        try {
            TestResponse res = Util.request("DELETE", "/account/2");
            JsonElement json = res.jsonElement();
            Assert.assertEquals(200, res.status);
        }finally {
            Util.request("PUT", "/account/2?userId=2&balance=1000&currencyCode=EUR");
        }
    }
}
