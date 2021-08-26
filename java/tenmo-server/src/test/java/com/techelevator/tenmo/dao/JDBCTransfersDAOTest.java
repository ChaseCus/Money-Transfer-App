package com.techelevator.tenmo.dao;

import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class JDBCTransfersDAOTest extends TenmoDaoTest {

    private final Transfers TRANSFER_1 = new Transfers(4001, 1, 1, 2001, 2002,BigDecimal.valueOf(300));
    private final Transfers TRANSFER_2 = new Transfers(4002, 1, 1, 2002, 2001,BigDecimal.valueOf(50));


    private JDBCTransfersDAO trans;
    private TransfersDAO dao;


    @Before
    public void setup() {
        trans = new JDBCTransfersDAO(dataSource);
    }


    @Test
    public void getAllTransfers() {
        List<Transfers> transfers = trans.getAllTransfers(1001);
        assertEquals(2, transfers.size());

        List<Transfers> transfers2 = trans.getAllTransfers(1002);
        assertEquals(2, transfers2.size());


    }

    @Test
    public void getTransferById() {
        Transfers transfer = trans.getTransferById(4001);
        assertNotNull(transfer);
        assertTransferMatch(TRANSFER_1, transfer);

        Transfers transfer2 = trans.getTransferById(4002);
        assertNotNull(transfer2);
        assertTransferMatch(TRANSFER_2, transfer2);
    }

    @Test
    public void sendTransfer() {
        Transfers transfer = trans.getTransferById(4001);
        assertNotNull(transfer);
        assertEquals(TRANSFER_1.getTransferId(), transfer.getTransferId());

        Transfers transfer2 = trans.getTransferById(4002);
        assertNotNull(transfer2);
        assertEquals(TRANSFER_2.getTransferId(), transfer2.getTransferId());
    }

    private void assertTransferMatch(Transfers expected, Transfers actual) {
        Assert.assertEquals(expected.getTransferId(), actual.getTransferId());
    }
}