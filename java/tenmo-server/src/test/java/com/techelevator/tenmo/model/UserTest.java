package com.techelevator.tenmo.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserTest {

    private User testUser;

    @Before
    public void setup() {
        testUser = new User();
    }

    @Test
    public void testGetAndSetId(){
        testUser.setId(100);
        int controlVar = 100;

        assertEquals(controlVar, testUser.getId());
    }

    @Test
    public void testGetAndSetUsername(){
        testUser.setUsername("Jesse");
        String controlVar = "Jesse";

        assertEquals(controlVar, testUser.getUsername());

    }

    @Test
    public void testGetAndSetPassword(){
        testUser.setPassword("Heller");
        String controlVar = "Heller";

        assertEquals(controlVar, testUser.getPassword());

    }

    @Test
    public void testActivated(){
        testUser.setActivated(true);
        assertTrue(testUser.isActivated());
    }
}
