package com.techelevator.tenmo.model;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Objects;

public class Transfer {

    private int transferId;
    @NotNull (message = " TypeID may not be empty ")
    @Range (min = 1)
    private int transferTypeId;
    @NotNull (message = " StatusID may not be empty ")
    @Range (min = 1)
    private int transferStatusId;
    @NotNull (message = " AccountTo may not be empty ")
    @Range (min = 1)
    private int accountTo;
    @NotNull (message = " AccountFrom may not be empty ")
    @Range (min = 1)
    private int accountFrom;
    @NotNull (message = " AccountFrom may not be empty ")
    @Positive (message = " Cannot transfer negative amounts ")
    private double amount;

    public Transfer() {
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(int transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public int getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(int accountTo) {
        this.accountTo = accountTo;
    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer transfer = (Transfer) o;
        return getTransferId() == transfer.getTransferId() &&
                getTransferTypeId() == transfer.getTransferTypeId() &&
                getTransferStatusId() == transfer.getTransferStatusId() &&
                getAccountTo() == transfer.getAccountTo() &&
                getAccountFrom() == transfer.getAccountFrom() &&
                Double.compare(transfer.getAmount(), getAmount()) == 0;
    }

}
