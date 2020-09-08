package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDAO {

    List<Transfer> findAllById(int id);

    List<Transfer>findPendingById(int id, int transferStatus);

    Transfer findByTransferId(int transferId);

    Transfer create(Transfer transfer);

    boolean update(int transferId, Transfer updatedTransfer);

}