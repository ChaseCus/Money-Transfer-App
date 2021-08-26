package com.techelevator.tenmo.dao;

import java.math.BigDecimal; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.techelevator.tenmo.model.Account;

import javax.sql.DataSource;
//implents AccountDAO Class
@Service 
public class JDBCAccountDAO implements AccountDAO {
	
	@Autowired
	private final JdbcTemplate jdbcTemplate;

	
	public JDBCAccountDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}


	// Returns Get Balance From Accounts based on User ID
	@Override
	public BigDecimal getBalance(int userId) {
		String sqlString = "SELECT balance FROM accounts WHERE user_id = ?";
		SqlRowSet results = null;
		BigDecimal balance = null;
	try {
		results = jdbcTemplate.queryForRowSet(sqlString, userId);
		if (results.next()) {
		balance = results.getBigDecimal("balance");
		}
	} catch (DataAccessException e) {
		System.out.println("Error accessing data");
	}
	return balance;
	}
	// Adds Balance using paramaters Account ID, add user imput balance
	@Override
	public BigDecimal addToBalance(BigDecimal amountToAdd, int id) {
		Account account = findAccountById(id);
		BigDecimal newBalance = account.getBalance().add(amountToAdd);
		System.out.println(newBalance);
		String sqlString = "UPDATE accounts SET balance = ? WHERE account_id = ?";
		try {
			jdbcTemplate.update(sqlString, newBalance, id);
		} catch (DataAccessException e) {
			System.out.println("Error accessing data");
		}
		return newBalance;
	}

	// Subtracts from Balance where Balance is inputed and account ID is inputed
	@Override
	public BigDecimal subtractFromBalance(BigDecimal amountToSubtract, int id) {
		Account account = findAccountById(id);
		BigDecimal newBalance = account.getBalance().subtract(amountToSubtract);
		String sqlString = "UPDATE accounts SET balance = ? WHERE account_id = ?";
		try {
			jdbcTemplate.update(sqlString, newBalance, id);
		} catch (DataAccessException e) {
			System.out.println("Error accessing data");
		}
		System.out.println(newBalance);
		return newBalance;
	}
	// Find user through SQL where user_id is inputed returns all Account info
	@Override
	public Account findUserById(int userId) {
		String sqlString = "SELECT * FROM accounts WHERE user_id = ?";
		Account account = null;
		try {
			SqlRowSet result = jdbcTemplate.queryForRowSet(sqlString, userId);
			account = mapRowToAccount(result);
		} catch (DataAccessException e) {
			System.out.println("Error accessing data");
		}
		return account;
	}
	//Returns All account by account ID
	@Override
	public Account findAccountById(int id) {
		Account account = null;
		String sql = "SELECT * FROM accounts WHERE account_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
		if (results.next()) {
			account = mapRowToAccount(results);
		}
		return account;
	}
	//Returns Account by User ID
	public Account findAccountByUserId(int id) {
		Account account = null;
		System.out.println(id);
		String sql = "SELECT * FROM accounts WHERE user_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
		if (results.next()) {
			account = mapRowToAccount(results);
		}
		return account;
	}
	//mapRow for account, using setbalance method/set account method/set user ID method
	private Account mapRowToAccount(SqlRowSet result) {
		Account account = new Account();
		account.setBalance(result.getBigDecimal("balance"));
		account.setAccountId(result.getInt("account_id"));
		account.setUserId(result.getInt("user_id"));
		return account;
	}

}