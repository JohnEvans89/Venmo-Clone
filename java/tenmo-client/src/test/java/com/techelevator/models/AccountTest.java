package com.techelevator.models;

import com.techelevator.tenmo.models.Account;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AccountTest {

    private Account testAccount;

    @Before
    public void setup() {
        testAccount = new Account();
    }

    @Test
    public void testGetAndSetAccountId(){

        testAccount.setAccountId(100);
        int controlVar = 100;

        assertEquals(controlVar, testAccount.getAccountId());

    }

    @Test
    public void testGetAndSetUserId() {

        testAccount.setUserId(100);
        int controlVar = 100;

        assertEquals(controlVar, testAccount.getUserId());

    }

    @Test
    public void testGetAndSetBalance() {

        testAccount.setBalance(1000.0);
        double controlVar = 1000.0;

        assertEquals(controlVar, testAccount.getBalance(), 0);

    }

}
