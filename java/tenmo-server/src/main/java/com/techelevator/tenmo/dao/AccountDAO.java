package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

public interface AccountDAO {

    Account findAccountByUserId(int userId);

//    double accountBalance(int accountId);

    boolean updateBalance(int accountId, Account changedAccount);

}

