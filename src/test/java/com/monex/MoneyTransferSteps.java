package com.monex;

import com.google.gson.JsonElement;
import com.monex.helper.TestResponse;
import com.monex.helper.Util;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;

public class MoneyTransferSteps {

    private TestResponse testResponse;
    private String fromUserAccountId;
    private BigDecimal balanceToTransfer;
    private String toUserAccountId;

    @Before
    public static void beforeClass() throws InterruptedException {
        MoneyTransferAPI.main(null); //to spark the spark server
        sleep(3000);
    }

    @Given("^that the user with account id (.*) has to transfer (.*) Euro to another user having account id (.*)")
    public void givenUserHasBalanceInAccount(String fromUserAccountId, BigDecimal balanceToTransfer, String toUserAccountId) throws Throwable {
        this.fromUserAccountId = fromUserAccountId;
        this.balanceToTransfer = balanceToTransfer;
        this.toUserAccountId = toUserAccountId;
    }

    @When("^the transfer is requested$")
    public void whenUserMakesATransfer() throws Throwable {
        testResponse = Util.request("POST", "/moneytransfer?fromAccountId="+this.fromUserAccountId+
                "&toAccountId="+this.toUserAccountId+"&amountToTransfer="+this.balanceToTransfer);
    }

    @Then("^amount is deducted from sender's account$")
    public void andAmountShouldBeDeductedFromSender() throws Throwable {
        testResponse = Util.request("GET", "/account/"+this.fromUserAccountId);
        assertEquals(200, testResponse.status);
        JsonElement jsonElement = testResponse.jsonElement();
        assertEquals(BigDecimal.valueOf(1800),jsonElement.getAsJsonObject().get("data").getAsJsonObject().get("balance").getAsBigDecimal());
    }

    @And("^receiver receives the amount$")
    public void thenMoneyShouldBeTransferredToReceiver() throws Throwable {
        testResponse = Util.request("GET", "/account/"+this.toUserAccountId);
        assertEquals(200, testResponse.status);
        JsonElement jsonElement = testResponse.jsonElement();
        assertEquals(BigDecimal.valueOf(1200), jsonElement.getAsJsonObject().get("data").getAsJsonObject().get("balance").getAsBigDecimal());
    }
    //Performing rollback manually since for the simplicity of in memory datastore
    //I did not use real database where i could have used database rollback feature to declare tests to rollback after each test
    @After
    public void rollback(){
        Util.request("POST", "/moneytransfer?fromAccountId="+this.toUserAccountId+
                "&toAccountId="+this.fromUserAccountId+"&amountToTransfer="+this.balanceToTransfer);
    }
}
