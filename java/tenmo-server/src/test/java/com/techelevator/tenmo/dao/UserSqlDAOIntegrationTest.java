package com.techelevator.tenmo.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.techelevator.tenmo.model.User;
import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.SQLException;
import java.util.List;

public class UserSqlDAOIntegrationTest {

    private static SingleConnectionDataSource dataSource;
    private UserSqlDAO dao;
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
        dao = new UserSqlDAO(jdbcTemplate);
        boolean testCreate = dao.create("jesseheller", "isthebest");
    }

    @After
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();
    }

    @Test
    public void testFindUsername(){
        User test = new User();
        String sqlGetNameById = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetNameById, 2);
        if (results.next()) {
            test = mapRowToUser(results);
        }
        String actual = dao.findUsername(2);

        assertEquals(actual, test.getUsername());

    }

    @Test
    public void testFindIdByUsername() {
        User test = new User();
        String sqlGetNameById = "SELECT * FROM users WHERE username = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetNameById, "jesseheller");
        if (results.next()) {
            test = mapRowToUser(results);
        }

        int actual = dao.findIdByUsername("jesseheller");

        assertEquals(actual, test.getId());
    }

    @Test
    public void testFindAll() {
        List<User> expected = dao.findAll();
        // when the .sql files create the database it automatically inserts two users
        // 'user' (id 1) and 'admin' (id 2), and we also created a user
        // 'jesseheller' in our setup (should have last id in list)
        assertEquals(expected.get(0).getUsername(), "user");
        assertEquals(expected.get(1).getUsername(), "admin");
        assertEquals(expected.get(expected.size()-1).getUsername(), "jesseheller");

    }

    @Test
    public void testCreate() {
        assertTrue(dao.create("juicyj", "isnumber1"));
    }

    @Test
    public void testFindByUsername(){
        User test = dao.findByUsername("jesseheller");
        List<User> list = dao.findAll();
        int lastUserId = list.get(list.size() - 1).getId();
        assertEquals(lastUserId, test.getId());
    }

    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setActivated(true);
        user.setAuthorities("ROLE_USER");
        return user;
    }
}
