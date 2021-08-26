package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfers;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class JDBCTransfersDAOTest extends TenmoDaoTest {
    BigDecimal bigDecimal = new BigDecimal(500);



    private JDBCAccountDAO act;

    private Account testAccount;

    @Before
    public void setup() {
        act = new JDBCAccountDAO(dataSource);
    }


    @Test
    public void getAllTransfers() {
        Transfers transfers
    }

    @Test
    public void getTransferById() {
    }

    @Test
    public void sendTransfer() {
    }
}