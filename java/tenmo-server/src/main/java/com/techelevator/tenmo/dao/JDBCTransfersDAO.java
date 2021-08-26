package com.techelevator.tenmo.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.techelevator.tenmo.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import com.techelevator.tenmo.model.TransferNotFoundException;
import com.techelevator.tenmo.model.Transfers;

import javax.sql.DataSource;

@Component
public class JDBCTransfersDAO implements TransfersDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private AccountDAO accountDAO;

	public JDBCTransfersDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Transfers> getAllTransfers(int userId) {
		List<Transfers> list = new ArrayList<>();
		String sql = "SELECT t.*, u.username AS userFrom, v.username AS userTo FROM transfers t " + 
				"JOIN accounts a ON t.account_from = a.account_id " + 
				"JOIN accounts b ON t.account_to = b.account_id " + 
				"JOIN users u ON a.user_id = u.user_id " + 
				"JOIN users v ON b.user_id = v.user_id " + 
				"WHERE a.user_id = ? OR b.user_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
		while (results.next() ) {
			Transfers transfer = mapRowToTransfer(results);
			list.add(transfer);
		}
		return list;
	}

	@Override
	public Transfers getTransferById(int transferId) {


		Transfers transfer = new Transfers();
		String sql = "SELECT t.*, u.username AS userFrom, v.username AS userTo, ts.transfer_status_desc, tt.transfer_type_desc FROM transfers t " +
				"JOIN accounts a ON t.account_from = a.account_id " +
				"JOIN accounts b ON t.account_to = b.account_id " +
				"JOIN users u ON a.user_id = u.user_id " +
				"JOIN users v ON b.user_id = v.user_id " +
				"JOIN transfer_statuses ts ON t.transfer_status_id = ts.transfer_status_id " +
				"JOIN transfer_types tt ON t.transfer_type_id = tt.transfer_type_id " +
				"WHERE t.transfer_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);

		if (results.next()) {
			transfer = mapRowToTransfer(results);
		} else {
			throw new TransferNotFoundException();
		}

		return transfer;
	}

	@Override
	public String sendTransfer(int userFrom, int userTo, BigDecimal amount) {
		int accountFrom = accountDAO.findAccountByUserId(userFrom).getAccountId();
		int accountTo = accountDAO.findAccountByUserId(userTo).getAccountId();
		System.out.println(accountFrom + "jdbc");
		System.out.println(accountTo + "jdbc");

		if (userFrom == userTo) {
			return "You can not send money to your self.";

		}
		if (amount.compareTo(accountDAO.getBalance(userFrom)) < 0 && amount.compareTo(BigDecimal.ZERO) > 0) {
			String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
					"VALUES (2, 2, ?, ?, ?) ";
			jdbcTemplate.update(sql, accountFrom, accountTo, amount);
			accountDAO.addToBalance(amount, accountTo);
			accountDAO.subtractFromBalance(amount, accountFrom);

			return "Transfer complete";

		} else {
			return "Transfer failed due to a lack of funds or amount was less then or equal to 0 or not a valid user";
		}
	}

	
	private Transfers mapRowToTransfer(SqlRowSet results) {
		Transfers transfer = new Transfers();
		transfer.setTransferId(results.getInt("transfer_id"));
		transfer.setTransferTypeId(results.getInt("transfer_type_id"));
		transfer.setTransferStatusId(results.getInt("transfer_status_id"));
		transfer.setAccountFrom(results.getInt("account_From"));
		transfer.setAccountTo(results.getInt("account_to"));
		transfer.setAmount(results.getBigDecimal("amount"));
		try {
			transfer.setUserFrom(results.getString("userFrom"));
			transfer.setUserTo(results.getString("userTo"));			
		} catch (Exception e) {}
		try {
			transfer.setTransferType(results.getString("transfer_type_desc"));
			transfer.setTransferStatus(results.getString("transfer_status_desc"));			
		} catch (Exception e) {}
		return transfer;
	}



}
