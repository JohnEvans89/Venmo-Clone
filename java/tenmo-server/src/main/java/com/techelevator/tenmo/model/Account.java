package com.techelevator.tenmo.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class Account {

    private int accountId;
    @NotNull (message = " UserID cannot be blank ")
    private int userId;
    @Min (value = 0, message = " Account cannot be overdrawn " )
    private double balance;

    public Account() {
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return getAccountId() == account.getAccountId() &&
                getUserId() == account.getUserId() &&
                Double.compare(account.getBalance(), getBalance()) == 0;
    }

}
