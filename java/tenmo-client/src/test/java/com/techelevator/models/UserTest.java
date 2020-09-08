package com.techelevator.models;

import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest {

    private User testUser;

    @Before
    public void setup() {
        testUser = new User();
    }

    @Test
    public void testGetAndSetId(){
        testUser.setId(100);
        Integer controlVar = 100;

        assertEquals(controlVar, testUser.getId());
    }

    @Test
    public void testGetAndSetUsername(){
        testUser.setUsername("Jesse");
        String controlVar = "Jesse";

        assertEquals(controlVar, testUser.getUsername());

    }
}
