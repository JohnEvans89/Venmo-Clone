package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TenmoController {

    private AccountDAO accountDAO;
    private TransferDAO transferDAO;
    private UserDAO userDAO;

    public TenmoController(AccountDAO accountDAO, TransferDAO transferDAO, UserDAO userDAO) {
        this.accountDAO = accountDAO;
        this.transferDAO = transferDAO;
        this.userDAO = userDAO;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "users", method = RequestMethod.GET)
    public List<User> findAllUsers() {
        return userDAO.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "users/{id}", method = RequestMethod.GET)
    public String findUsernameById(@PathVariable int id) {
        return userDAO.findUsername(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "accounts/{id}", method = RequestMethod.GET)
    public Account getSingleAccount(@PathVariable int id) {
        return accountDAO.findAccountByUserId(id);
    }

//    @RequestMapping(path = "accounts/{id}", method = RequestMethod.GET)
//    public double accountBalance(@PathVariable int id) {
//        return accountDAO.accountBalance(id);
//    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(path = "accounts/{id}", method = RequestMethod.PUT)
    public void updateAccount(@Valid @RequestBody Account account, @PathVariable int id) {
        accountDAO.updateBalance(id, account);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(path = "transfers/{id}", method = RequestMethod.PUT)
    public void updateTransfer(@Valid @RequestBody Transfer transfer, @PathVariable int id) {
        transferDAO.update(id, transfer);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "transfers", method = RequestMethod.POST)
    public Transfer createNewTransfer(@Valid @RequestBody Transfer transfer) {
        return transferDAO.create(transfer);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "transfers/{id}", method = RequestMethod.GET)
    public Transfer findSingleTransferById(@PathVariable int id) {
        return transferDAO.findByTransferId(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "users/{id}/transfers", method = RequestMethod.GET)
    public List<Transfer> findAllTransfersById(@PathVariable int id, @RequestParam(required = false) Integer transferStatusId) {
        if (transferStatusId == null) {
            transferStatusId = 2;
        } else if (transferStatusId == 1) {
            return transferDAO.findPendingById(id, transferStatusId);
        }
        return transferDAO.findAllById(id);
    }
}
