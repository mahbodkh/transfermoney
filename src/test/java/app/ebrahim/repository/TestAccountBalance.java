package app.ebrahim.repository;

import app.ebrahim.connection.H2Manager;
import app.ebrahim.connection.RepositoryFactory;
import app.ebrahim.domain.Account;
import app.ebrahim.domain.TransactionPayment;
import app.ebrahim.error.CustomException;
import app.ebrahim.util.RandomGenerator;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;

import static junit.framework.TestCase.assertEquals;

public class TestAccountBalance {
    private static Logger log = Logger.getLogger(TestAccountRepository.class);

    private static final RepositoryFactory repository = RepositoryFactory.getRepository(H2Manager.H2);
    private static final int THREADS_COUNT = 100;


    @BeforeClass
    public static void setup() {
        // prepare test database and test data, Test data are initialised from
        // src/test/resources/demo.sql
        repository.populateTestData();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testAccountSingleThreadSameCcyTransfer() throws CustomException {
        final AccountRepository accountRepository = repository.getAccountRepository();
        BigDecimal transferAmount = new BigDecimal(50.01234).setScale(4, RoundingMode.HALF_EVEN);
        TransactionPayment transactionPayment = new TransactionPayment("EUR"
                , transferAmount
                , RandomGenerator.stan()
                , 3L
                , 4L
                , Instant.now().toString());


        long startTime = System.currentTimeMillis();

        accountRepository.transferAccountBalance(transactionPayment);
        long endTime = System.currentTimeMillis();
        log.info("TransferAccountBalance finished, time taken: " + (endTime - startTime) + "ms");

        Account accountFrom = accountRepository.getAccountById(3L);
        Account accountTo = accountRepository.getAccountById(4L);

        log.debug("Account From: " + accountFrom);
        log.debug("Account From: " + accountTo);

        assertEquals(0, accountFrom.getBalance().compareTo(new BigDecimal(449.9877).setScale(4, RoundingMode.HALF_EVEN)));
        assertEquals(accountTo.getBalance(), new BigDecimal(550.0123).setScale(4, RoundingMode.HALF_EVEN));

    }

    @Test
    public void testAccountMultiThreadedTransfer() throws InterruptedException, CustomException {
        final AccountRepository accountRepository = repository.getAccountRepository();
        // transfer a total of 200USD from 100USD balance in multi-threaded
        // mode, expect half of the transaction fail
        final CountDownLatch latch = new CountDownLatch(THREADS_COUNT);
        for (int i = 0; i < THREADS_COUNT; i++) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {

                        TransactionPayment transactionPayment = new TransactionPayment("USD"
                                , new BigDecimal(2).setScale(4, RoundingMode.HALF_EVEN)
                                , RandomGenerator.stan()
                                , 1L
                                , 2L
                                , Instant.now().toString());

                        accountRepository.transferAccountBalance(transactionPayment);
                    } catch (Exception e) {
                        log.error("Error occurred during transfer ", e);
                    } finally {
                        latch.countDown();
                    }
                }
            }).start();
        }
        latch.await();

        Account sourceAccount = accountRepository.getAccountById(1L);
        log.debug("Source Account : " + sourceAccount);
        Account destinationAccount = accountRepository.getAccountById(2L);
        log.debug("Destination Account : " + destinationAccount);

        assertEquals(sourceAccount.getBalance(), new BigDecimal(0).setScale(4, RoundingMode.HALF_EVEN));
        assertEquals(destinationAccount.getBalance(), new BigDecimal(300).setScale(4, RoundingMode.HALF_EVEN));

    }

    @Test
    public void testTransferFailOnDBLock() throws CustomException, SQLException {
        final String SQL_LOCK_ACC = "SELECT * FROM Account WHERE Id = 5 FOR UPDATE";
        Connection conn = null;
        PreparedStatement lockStmt = null;
        ResultSet rs = null;
        Account fromAccount = null;

        try {
            conn = H2Manager.getConnection();
            conn.setAutoCommit(false);
            // lock account for writing:
            lockStmt = conn.prepareStatement(SQL_LOCK_ACC);
            rs = lockStmt.executeQuery();
            if (rs.next()) {
                fromAccount = new Account(rs.getLong("AccountId")
                        , rs.getString("Party")
                        , rs.getBigDecimal("Balance")
                        , rs.getString("Iban")
                        , rs.getString("CurrencyCode")
                        , rs.getString("AccountStatusType")
                        , rs.getString("CreateAccountTime")
                        , rs.getString("UpdateAccountTime")
                );
                if (log.isDebugEnabled())
                    log.debug("Locked Account: " + fromAccount);
            }

            if (fromAccount == null) {
                throw new CustomException("Locking error during test, SQL = " + SQL_LOCK_ACC);
            }
            // after lock account 5, try to transfer from account 6 to 5
            // default h2 timeout for acquire lock is 1sec
            BigDecimal balance = new BigDecimal(100);
            BigDecimal transferAmount = new BigDecimal(50).setScale(4, RoundingMode.HALF_EVEN);
            TransactionPayment transactionPayment = new TransactionPayment(5L
                    , "GBP"
                    , transferAmount
                    , RandomGenerator.stan()
                    , 5L
                    , 6L
                    , Instant.now().toString());
            repository.getAccountRepository().transferAccountBalance(transactionPayment);
            conn.commit();
        } catch (Exception e) {
            log.error("Exception occurred, initiate a rollback");
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException re) {
                log.error("Fail to rollback transaction", re);
            }
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(lockStmt);
        }
        // now inspect account 3 and 4 to verify no transaction occurred
        BigDecimal originalBalance = new BigDecimal(500).setScale(4, RoundingMode.HALF_EVEN);
        assertEquals(repository.getAccountRepository().getAccountById(6L).getBalance(), originalBalance);
        assertEquals(repository.getAccountRepository().getAccountById(5L).getBalance(), originalBalance);
    }
}
