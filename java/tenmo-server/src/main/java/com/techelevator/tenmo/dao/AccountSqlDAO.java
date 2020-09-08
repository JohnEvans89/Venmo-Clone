package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

@Service
public class AccountSqlDAO implements AccountDAO {

    private JdbcTemplate jdbcTemplate;

    public AccountSqlDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account findAccountByUserId(int userId) {
        Account account = null;
        String sql = "SELECT * FROM accounts WHERE user_id = ?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        if (results.next()) {
            account = mapRowToAccount(results);
        }

        return account;
    }

//    @Override
//    public double accountBalance(int accountId) {
//        return jdbcTemplate.queryForObject("SELECT balance FROM accounts WHERE account_id = ?", Double.class, accountId);
//    }

    @Override
    public boolean updateBalance(int accountId, Account changedAccount) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";

        return jdbcTemplate.update(sql, changedAccount.getBalance(), accountId) == 1;

    }

    private Account mapRowToAccount(SqlRowSet rs) {
        Account acct = new Account();
        acct.setAccountId(rs.getInt("account_id"));
        acct.setUserId(rs.getInt("user_id"));
        acct.setBalance(rs.getDouble("balance"));
        return acct;
    }
}
