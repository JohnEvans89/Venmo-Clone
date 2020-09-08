package com.techelevator.tenmo.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TransferTest {

    private Transfer testTransfer;

    @Before
    public void setup() {
        testTransfer = new Transfer();
    }

    @Test
    public void testGetAndSetTransferId() {
        testTransfer.setTransferId(100);
        int controlVar = 100;

        assertEquals(controlVar, testTransfer.getTransferId());

    }

    @Test
    public void testGetAndSetTransferTypeId(){
        testTransfer.setTransferTypeId(100);
        int controlVar = 100;

        assertEquals(controlVar, testTransfer.getTransferTypeId());

    }

    @Test
    public void testGetAndSetTransferStatusId(){
        testTransfer.setTransferStatusId(100);
        int controlVar = 100;

        assertEquals(controlVar, testTransfer.getTransferStatusId());

    }

    @Test
    public void testGetAndSetAccountTo(){
        testTransfer.setAccountTo(100);
        int controlVar = 100;

        assertEquals(controlVar, testTransfer.getAccountTo());

    }

    @Test
    public void testGetAndSetAccountFrom(){
        testTransfer.setAccountFrom(100);
        int controlVar = 100;

        assertEquals(controlVar, testTransfer.getAccountFrom());

    }

    @Test
    public void testGetAndSetAmount(){
        testTransfer.setAmount(1000.0);
        double controlVar = 1000.0;

        assertEquals(controlVar, testTransfer.getAmount(), 0);

    }
}
