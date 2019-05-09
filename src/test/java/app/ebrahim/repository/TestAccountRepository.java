package app.ebrahim.repository;

import app.ebrahim.connection.H2Manager;
import app.ebrahim.connection.RepositoryFactory;
import app.ebrahim.domain.Account;
import app.ebrahim.domain.AccountStatusType;
import app.ebrahim.error.CustomException;
import app.ebrahim.util.RandomGenerator;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

import static junit.framework.TestCase.*;

public class TestAccountRepository {
    private static final RepositoryFactory repository = RepositoryFactory.getRepository(H2Manager.H2);

    @BeforeClass
    public static void setup() {
        // prepare test database and test data. Test data are initialised from
        // src/main/resources/demo.sql
        repository.populateTestData();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testGetAllAccounts() throws CustomException {
        List<Account> allAccounts = repository.getAccountRepository().getAllAccounts();
        assertTrue(allAccounts.size() > 1);
    }

    @Test
    public void testGetAccountById() throws CustomException {
        Account account = repository.getAccountRepository().getAccountById(1L);
        assertEquals("Ebrahim", account.getPartyId());
    }

    @Test
    public void testGetNonExistingAccById() throws CustomException {
        Account account = repository.getAccountRepository().getAccountById(100L);
        assertNull(account);
    }

    @Test
    public void testCreateAccount() throws CustomException {
        BigDecimal balance = new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN);
        Account a = new Account(
                "test2"
                , balance
                , RandomGenerator.iban()
                , "CNY"
                , AccountStatusType.ACTIVE.toString()
                , Instant.now().toString()
                , "");

        long aid = repository.getAccountRepository().createAccount(a);
        Account afterCreation = repository.getAccountRepository().getAccountById(aid);
        assertEquals("test2", afterCreation.getPartyId());
        assertEquals("CNY", afterCreation.getCurrencyCodeType());
        assertEquals(afterCreation.getBalance(), balance);
    }

    @Test
    public void testDeleteAccount() throws CustomException {
        int rowCount = repository.getAccountRepository().deleteAccountById(2L);
        // assert one row(user) deleted
        assertEquals(1, rowCount);
        // assert user no longer there
        assertNull(repository.getAccountRepository().getAccountById(2L));
    }

    @Test
    public void testDeleteNonExistingAccount() throws CustomException {
        int rowCount = repository.getAccountRepository().deleteAccountById(500L);
        // assert no row(user) deleted
        assertEquals(0, rowCount);
    }

    @Test
    public void testUpdateAccountBalanceSufficientFund() throws CustomException {
        BigDecimal deltaDeposit = new BigDecimal(50).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal afterDeposit = new BigDecimal(150).setScale(4, RoundingMode.HALF_EVEN);
        int rowsUpdated = repository.getAccountRepository().updateAccountBalance(1L, deltaDeposit);
        assertEquals(1, rowsUpdated);
        assertEquals(repository.getAccountRepository().getAccountById(1L).getBalance(), afterDeposit);
        BigDecimal deltaWithDraw = new BigDecimal(-50).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal afterWithDraw = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
        int rowsUpdatedW = repository.getAccountRepository().updateAccountBalance(1L, deltaWithDraw);
        assertEquals(1, rowsUpdatedW);
        assertEquals(repository.getAccountRepository().getAccountById(1L).getBalance(), afterWithDraw);
    }

    @Test(expected = CustomException.class)
    public void testUpdateAccountBalanceNotEnoughFund() throws CustomException {
        BigDecimal deltaWithDraw = new BigDecimal(-50000).setScale(4, RoundingMode.HALF_EVEN);
        int rowsUpdatedW = repository.getAccountRepository().updateAccountBalance(1L, deltaWithDraw);
        assertEquals(0, rowsUpdatedW);
    }

}
