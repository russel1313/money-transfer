package com.monex;

import com.google.gson.JsonElement;
import com.monex.helper.TestResponse;
import com.monex.helper.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;

//Final block in the end of every test to perform rollback manually since for the simplicity of in memory datastore
//I did not use real database where i could have used database rollback feature to declare tests to rollback after each test
public class UserControllerIntegrationTest {
    @Before
    public  void beforeClass() throws InterruptedException {
        MoneyTransferAPI.main(null); //to spark the spark server
        sleep(3000);
    }
    @Test
    public void getListOfAllUsers() {
        TestResponse res = Util.request("GET", "/user/all");
        JsonElement json = res.jsonElement();
        Assert.assertEquals(200, res.status);
        assertEquals(2, json.getAsJsonObject().size());
    }

    @Test
    public void aNewUserShouldBeCreated() {
            TestResponse res = Util.request("POST", "/user/3?firstName=Russell&lastName=Hessampour&email=r.hessampour@gmail.com");
            JsonElement json = res.jsonElement();
            Assert.assertEquals(200, res.status);
        }

    @Test
    public void anExistingUserShouldBeUpdated() {
        try {
            TestResponse res = Util.request("PUT", "/user/3?firstName=Russell&lastName=Hessampour&email=r.hessampour@gmail.com");
            JsonElement json = res.jsonElement();
            Assert.assertEquals(200, res.status);
            assertEquals("Hessampour", json.getAsJsonObject().get("data").getAsJsonObject().get("lastName").getAsString());
            assertEquals("r.hessampour@gmail.com", json.getAsJsonObject().get("data").getAsJsonObject().get("email").getAsString());
        }finally {
            Util.request("DELETE", "/user/3");
        }
    }

    @Test
    public void anExistingUserShouldBeDeleted() {
        try {
            TestResponse res = Util.request("DELETE", "/user/2");
            JsonElement json = res.jsonElement();
            Assert.assertEquals(200, res.status);
        }finally {
            Util.request("POST", "/user/2?firstName=Russell&lastName=Hessampour&email=r.hessampour@gmail.com");
        }
    }
}
