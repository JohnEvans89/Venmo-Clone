package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransferSqlDAOIntegrationTest {

    private static SingleConnectionDataSource dataSource;
    private TransferSqlDAO dao;
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
        dao = new TransferSqlDAO(jdbcTemplate);

        Transfer testApprovedTransfer = new Transfer();
        testApprovedTransfer.setTransferTypeId(2); // 2 is send
        testApprovedTransfer.setTransferStatusId(2); // 2 is approved
        testApprovedTransfer.setAccountFrom(1);
        testApprovedTransfer.setAccountTo(2);
        testApprovedTransfer.setAmount(100);
        dao.create(testApprovedTransfer);

        Transfer testPendingTransfer = new Transfer();
        testPendingTransfer.setTransferTypeId(1); // 1 is request
        testPendingTransfer.setTransferStatusId(1); // 1 is pending
        testPendingTransfer.setAccountFrom(1);
        testPendingTransfer.setAccountTo(2);
        testPendingTransfer.setAmount(100);
        dao.create(testPendingTransfer);

    }

    @After
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();
    }

    @Test
    public void testFindAllById() {
        List<Transfer> allUsersTransfers = new ArrayList<>();
        String sqlTransferList = "SELECT * FROM transfers WHERE account_from = ? OR account_to = ?";
        // 1 is ID of user 'user', we know there should be at least 2 transfers for this user based on our setup
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlTransferList, 1, 1);

        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            allUsersTransfers.add(transfer);
        }

        assertEquals(allUsersTransfers.size(), dao.findAllById(1).size());

    }

    @Test
    public void testFindPendingById(){
        List<Transfer> allUsersPendingTransfers = new ArrayList<>();
        String sql = "SELECT * FROM transfers WHERE (account_from = ? OR account_to = ?) AND transfer_status_id = 1"; // 1 is pending
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, 1, 1);

        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            allUsersPendingTransfers.add(transfer);
        }

        assertEquals(allUsersPendingTransfers.size(), dao.findPendingById(1, 1).size());

    }

    @Test
    public void testFindByTransferId(){
        List<Transfer> allUsersTransfers = new ArrayList<>();
        String sqlTransferList = "SELECT * FROM transfers WHERE account_from = ? OR account_to = ?";
        // 1 is ID of user 'user', we know there should be at least 2 transfers for this user based on our setup
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlTransferList, 1, 1);

        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            allUsersTransfers.add(transfer);
        }
        int lastTransferOnListId = allUsersTransfers.get(allUsersTransfers.size()-1).getTransferId();

        Transfer test = dao.findByTransferId(lastTransferOnListId);

        assertEquals(allUsersTransfers.get(allUsersTransfers.size()-1), test);
    }

    @Test
    public void testCreate() {

        List<Transfer> allTransfersBeforeTestTransferCreated = dao.findAllById(1);
        int sizeOfListBeforeCreation = allTransfersBeforeTestTransferCreated.size();

        Transfer testTransfer = new Transfer();
        testTransfer.setTransferTypeId(2);
        testTransfer.setTransferStatusId(2);
        testTransfer.setAccountFrom(1);
        testTransfer.setAccountTo(2);
        testTransfer.setAmount(50);
        dao.create(testTransfer);

        List<Transfer> allTransfersAfterTestTransferCreated = dao.findAllById(1);

        assertEquals(allTransfersAfterTestTransferCreated.size(), sizeOfListBeforeCreation + 1);

    }

    @Test
    public void testUpdate() {

        // we created a pending transfer in setup so we know there is at least pending transfer for 'user'
        List<Transfer> allPendingBeforeUpdate = dao.findPendingById(1, 1);
        System.out.println(allPendingBeforeUpdate.size());
        Transfer pending = allPendingBeforeUpdate.get(0);
        int allPendingSizeBeforeUpdate = allPendingBeforeUpdate.size();
        int pendingId = pending.getTransferId();
        pending.setTransferStatusId(3); // 1 is pending, 3 is rejected
        dao.update(pendingId, pending);

        List<Transfer> allPendingAfterUpdate = dao.findPendingById(1, 1);

        assertTrue(dao.update(pendingId, pending));
        // after setting transfer status from pending to rejected, size of allPendingBeforeUpdate should be 1 less
        assertEquals(allPendingSizeBeforeUpdate - 1, allPendingAfterUpdate.size());


    }

    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setTransferTypeId(rs.getInt("transfer_type_id"));
        transfer.setTransferStatusId(rs.getInt("transfer_status_id"));
        transfer.setAccountTo(rs.getInt("account_to"));
        transfer.setAccountFrom(rs.getInt("account_from"));
        transfer.setAmount(rs.getDouble("amount"));
        return transfer;
    }
}
