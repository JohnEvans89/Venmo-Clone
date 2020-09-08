package com.techelevator.tenmo.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.techelevator.tenmo.model.Account;
import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.SQLException;

public class AccountSqlDaoIntegrationTest {

    private static SingleConnectionDataSource dataSource;
    private AccountSqlDAO dao;
    private JdbcTemplate jdbcTemplate;

    @BeforeClass
    public static void setupDataSource() {
        dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/tenmo");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");
        dataSource.setAutoCommit(false);
    }
    @AfterClass
    public static void destroyDataSource() {
        dataSource.destroy();
    }
    @Before
    public void setup() {

        jdbcTemplate = new JdbcTemplate(dataSource);
        dao = new AccountSqlDAO(jdbcTemplate);
    }

    @After
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();
    }

    @Test
    public void testGetAccountById() {

        Account test = new Account();
        String sqlGetAccountById = "SELECT * FROM accounts WHERE account_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetAccountById, 3);
        if (results.next()) {
            test = mapRowToAccount(results);
        }
        Account actual = dao.findAccountByUserId(3);
        assertEquals(test, actual);

    }

    @Test
    public void testUpdateBalance() {
        Account control = dao.findAccountByUserId(3);
        double balance = control.getBalance();
        control.setBalance(balance + balance);
        assertTrue(dao.updateBalance(control.getAccountId(), control));
    }

    private Account mapRowToAccount(SqlRowSet rs) {
        Account acct = new Account();
        acct.setAccountId(rs.getInt("account_id"));
        acct.setUserId(rs.getInt("user_id"));
        acct.setBalance(rs.getDouble("balance"));
        return acct;
    }
}

