package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class JDBCAccountDAOTest extends TenmoDaoTest{

    BigDecimal bigDecimal = new BigDecimal(500);

    private  Account ACCOUNT_1 = new Account(2001, 1001, bigDecimal.setScale(2));

    private JDBCAccountDAO act;

    private Account testAccount;

    @Before
    public void setup() {
        act = new JDBCAccountDAO(dataSource);

    }

    @Test
    public void getBalance_returns_correct_balance_for_id() {
        BigDecimal balance = act.getBalance(1001);
        assertNotNull("", balance);
        assertEquals("", ACCOUNT_1.getBalance(), balance);



    }

    @Test
    public void addToBalance() {
        BigDecimal balance = act.addToBalance(BigDecimal.valueOf(100),2001);
        assertNotNull("", balance);
        assertEquals("", ACCOUNT_1.getBalance().add(BigDecimal.valueOf(100)), balance);
    }

    @Test
    public void subtractFromBalance() {
        BigDecimal balance = act.subtractFromBalance(BigDecimal.valueOf(100),2001);
        assertNotNull("", balance);
        assertEquals("", ACCOUNT_1.getBalance().subtract(BigDecimal.valueOf(100)), balance);
    }

    @Test
    public void findUserById() {
        Account account = act.findAccountByUserId(1001);
        assertAccountMatch("", ACCOUNT_1, account);

    }

    @Test
    public void findAccountById() {
        Account account = act.findAccountById(2001);
        Assert.assertNotNull( account);
        assertAccountMatch("", ACCOUNT_1, account);

    }

    @Test
    public void findAccountByUserId() {
        Account account = act.findAccountByUserId(1001);
        assertAccountMatch("", ACCOUNT_1, account);


    }

    private void assertAccountMatch (String message, Account expected, Account actual) {
        Assert.assertEquals(message, expected.getBalance(), actual.getBalance());
        Assert.assertEquals(message, expected.getAccountId(), actual.getAccountId());
        Assert.assertEquals(message, expected.getUserId(), actual.getUserId());

    }
}