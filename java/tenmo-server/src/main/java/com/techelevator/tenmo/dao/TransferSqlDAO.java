package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransferSqlDAO implements TransferDAO {

    private JdbcTemplate jdbcTemplate;

    public TransferSqlDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Transfer> findAllById(int id) {
        List<Transfer> transferList = new ArrayList<>();
        String sqlTransferList = "SELECT * FROM transfers t " +
                "INNER JOIN accounts a " +
                "ON t.account_from = a.account_id " +
                "INNER JOIN users u " +
                "ON a.user_id = u.user_id " +
                "WHERE t.account_from = ? OR t.account_to = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlTransferList, id, id);

        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transferList.add(transfer);
        }

        return transferList;
    }

    @Override
    public List<Transfer>findPendingById(int id, int transferStatus) {
        List<Transfer> pendingTransfers = new ArrayList<>();
        String sqlPendingTransferList = "SELECT * FROM transfers t " +
                "INNER JOIN accounts a " +
                "ON t.account_from = a.account_id  " +
                "INNER JOIN users u " +
                "ON a.user_id = u.user_id " +
                "WHERE (t.account_from = ? OR t.account_to = ?) AND transfer_status_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlPendingTransferList, id, id, transferStatus);
        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            pendingTransfers.add(transfer);
        }
        return pendingTransfers;
    }

    @Override
    public Transfer findByTransferId(int transferId) {
        Transfer transfer = null;
        String sql = "SELECT * FROM transfers WHERE transfer_id = ?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
        if (results.next()) {
            transfer = mapRowToTransfer(results);
        }
        return transfer;
    }

    @Override
    public Transfer create(Transfer transfer) {

        String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount)" +
                "VALUES (?, ?, ?, ?, ?)";

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String id_column = "transfer_id";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{id_column});
            ps.setInt(1, transfer.getTransferTypeId());
            ps.setInt(2, transfer.getTransferStatusId());
            ps.setInt(3, transfer.getAccountFrom());
            ps.setInt(4, transfer.getAccountTo());
            ps.setDouble(5, transfer.getAmount());
            return ps;
        }, keyHolder);

        return transfer;
    }

    @Override
    public boolean update(int transferId, Transfer updatedTransfer) {
        String sql = "UPDATE transfers SET transfer_status_id = ? WHERE transfer_id = ?";

        return jdbcTemplate.update(sql, updatedTransfer.getTransferStatusId(), transferId) == 1;
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
