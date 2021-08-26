package com.techelevator.tenmo.controller;

import java.math.BigDecimal; 
import java.util.List;

import com.techelevator.tenmo.dao.TransfersDAO;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.model.User;

@RestController
@PreAuthorize("isAuthenticated()")
public class TenmoController {

	@Autowired
	private AccountDAO accountDAO;
	@Autowired
	private UserDao userDAO;
	@Autowired
	private TransfersDAO transfersDAO;

	
	public TenmoController(AccountDAO accountDAO, UserDao userDAO, TransfersDAO transfersDAO) {
		this.accountDAO = accountDAO;
		this.userDAO = userDAO;
		this.transfersDAO = transfersDAO;

	}

	@RequestMapping(path = "balance/{id}", method = RequestMethod.GET)
	public BigDecimal getBalance(@PathVariable int id) {
		BigDecimal balance = accountDAO.getBalance(id);
		return balance;
	}
	
	@RequestMapping(path = "listusers", method = RequestMethod.GET)
	public List <User> listUsers() {
		List <User> users = userDAO.findAll();
		return users;
	}

	@RequestMapping(path = "accounts/transfers/{id}", method = RequestMethod.GET)
	public List<Transfers> getAllMyTransfers(@PathVariable int id) {
		List<Transfers> output = transfersDAO.getAllTransfers(id);
		return output;
	}

	@RequestMapping(path = "transfers/{id}", method = RequestMethod.GET)
	public Transfers getSelectedTransfer(@PathVariable int id) {
		Transfers transfer = transfersDAO.getTransferById(id);
		return transfer;
	}

	@RequestMapping(path = "transfer", method = RequestMethod.POST)
	public String sendTransferRequest(@RequestBody Transfers transfer) {
		String results = transfersDAO.sendTransfer(transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
		return results;
	}

//	@RequestMapping(path = "request", method = RequestMethod.POST)
//	public String requestTransferRequest(@RequestBody Transfers transfer) {
//		String results = transfersDAO.requestTransfer(transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
//		return results;
//	}
//
//	@RequestMapping(value = "request/{id}", method = RequestMethod.GET)
//	public List<Transfers> getAllTransferRequests(@PathVariable int id) {
//		List<Transfers> output = transfersDAO.getPendingRequests(id);
//		return output;
//	}
//
//	@RequestMapping(path = "transfer/status/{statusId}", method = RequestMethod.PUT)
//	public String updateRequest(@RequestBody Transfers transfer, @PathVariable int statusId) {
//		String output = transfersDAO.updateTransferRequest(transfer, statusId);
//		return output;
//	}
	
}
