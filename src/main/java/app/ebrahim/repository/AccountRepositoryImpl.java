package app.ebrahim.repository;


import app.ebrahim.connection.H2Manager;
import app.ebrahim.domain.Account;
import app.ebrahim.domain.TransactionPayment;
import app.ebrahim.error.CustomException;
import app.ebrahim.util.RandomGenerator;
import app.ebrahim.util.Validation;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AccountRepositoryImpl implements AccountRepository {
    private static Logger log = Logger.getLogger(AccountRepositoryImpl.class);

    private final static String SQL_GET_ACC_BY_ID =
            " SELECT * FROM Account WHERE Id = ? ";
    private final static String SQL_LOCK_ACC_BY_ID =
            " SELECT * FROM Account WHERE Id = ? FOR UPDATE ";
    private final static String SQL_UPDATE_ACC_BALANCE =
            " UPDATE Account SET Balance = ?, UpdateAccountDate = ? WHERE Id = ? ";
    private final static String SQL_GET_ALL_ACC =
            " SELECT * FROM Account ";
    private final static String SQL_DELETE_ACC_BY_ID =
            " DELETE FROM Account WHERE Id = ? ";
    private final static String SQL_CREATE_ACC =
            " INSERT INTO Account (PartyId, Balance, Iban, CurrencyCodeType, AccountStatusType, CreateAccountDate, UpdateAccountDate) " +
                    "VALUES ( ?, ?, ?, ?, ?, ?, ?) ";
    private final static String SQL_CREATE_TRN_PAY =
            " INSERT INTO TransactionPayment (CurrencyCodeType, Amount, Stan, SourceAccountId, DestinationAccountId, PersistenceTime) VALUES (? ,? ,? ,? , ?, ?) ";


    public List<Account> getAllAccounts() throws CustomException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Account> allAccounts = new ArrayList<Account>();
        try {
            conn = H2Manager.getConnection();
            stmt = conn.prepareStatement(SQL_GET_ALL_ACC);
            rs = stmt.executeQuery();
            while (rs.next()) {

                Account acc = new Account(
                        rs.getLong("Id")
                        , rs.getString("PartyId")
                        , rs.getBigDecimal("Balance")
                        , rs.getString("Iban")
                        , rs.getString("CurrencyCodeType")
                        , rs.getString("AccountStatusType")
                        , rs.getString("CreateAccountDate")
                        , rs.getString("UpdateAccountDate"));


                if (log.isDebugEnabled())
                    log.debug("getAllAccounts(): Get  Account " + acc);
                allAccounts.add(acc);
            }
            return allAccounts;
        } catch (SQLException e) {
            throw new CustomException("getAccountById(): Error reading account data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }


    public Account getAccountById(Long accountId) throws CustomException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Account acc = null;
        try {
            conn = H2Manager.getConnection();
            stmt = conn.prepareStatement(SQL_GET_ACC_BY_ID);
            stmt.setLong(1, accountId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                acc = new Account(rs.getLong("Id")
                        , rs.getString("PartyId")
                        , rs.getBigDecimal("Balance")
                        , rs.getString("Iban")
                        , rs.getString("CurrencyCodeType")
                        , rs.getString("AccountStatusType")
                        , rs.getString("CreateAccountDate")
                        , rs.getString("UpdateAccountDate"));

                if (log.isDebugEnabled())
                    log.debug("Retrieve Account By Id: " + acc);
            }
            return acc;
        } catch (SQLException e) {
            throw new CustomException("getAccountById(): Error reading account data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }

    }

    public long createAccount(Account account) throws CustomException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        if (account.getBalance().intValue() <= 0) {
            log.error("createAccount(): Creating account failed, Balance is not acceptable, your balance must more than = '0' ");
            throw new CustomException("Your balance must more than >= '0' ");
        }

        try {
            conn = H2Manager.getConnection();
            stmt = conn.prepareStatement(SQL_CREATE_ACC, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, account.getPartyId());
            stmt.setBigDecimal(2, account.getBalance());
            stmt.setString(3, RandomGenerator.iban());
            stmt.setString(4, account.getCurrencyCodeType());
            stmt.setString(5, account.getAccountStatusType().toString());
            stmt.setString(6, Instant.now().toString());
            stmt.setString(7, Instant.now().toString());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                log.error("createAccount(): Creating account failed, no rows affected.");
                throw new CustomException("Account Cannot be created");
            }
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            } else {
                log.error("Creating account failed, no ID obtained.");
                throw new CustomException("Account Cannot be created");
            }
        } catch (SQLException e) {
            log.error("Error Inserting Account " + account);
            throw new CustomException("createAccount(): Error creating user account " + account, e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, generatedKeys);
        }
    }

    public int deleteAccountById(Long accountId) throws CustomException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = H2Manager.getConnection();
            stmt = conn.prepareStatement(SQL_DELETE_ACC_BY_ID);
            stmt.setLong(1, accountId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new CustomException("deleteAccountById(): Error deleting user account Id " + accountId, e);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(stmt);
        }
    }


    public int updateAccountBalance(Long accountId, BigDecimal amount) throws CustomException {
        Connection conn = null;
        PreparedStatement lockStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;
        Account targetAccount = null;
        int updateCount = -1;
        try {
            conn = H2Manager.getConnection();
            conn.setAutoCommit(false);
            // lock account for writing:
            lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
            lockStmt.setLong(1, accountId);
            rs = lockStmt.executeQuery();
            if (rs.next()) {
                targetAccount = new Account(rs.getLong("Id")
                        , rs.getString("PartyId")
                        , rs.getBigDecimal("Balance")
                        , rs.getString("Iban")
                        , rs.getString("CurrencyCodeType")
                        , rs.getString("AccountStatusType")
                        , rs.getString("CreateAccountDate")
                        , rs.getString("UpdateAccountDate"));

                if (log.isDebugEnabled())
                    log.debug("updateAccountBalance from Account: " + targetAccount);
            }

            if (targetAccount == null) {
                throw new CustomException("updateAccountBalance(): fail to lock account : " + accountId);
            }
            // update account upon success locking
            BigDecimal balance = targetAccount.getBalance().add(amount);
            if (balance.compareTo(Validation.zeroAmount) < 0) {
                throw new CustomException("Not sufficient Fund for account: " + accountId);
            }

            updateStmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
            updateStmt.setBigDecimal(1, balance);
            updateStmt.setString(2, Instant.now().toString());
            updateStmt.setLong(3, accountId);

            updateCount = updateStmt.executeUpdate();
            conn.commit();

            if (log.isDebugEnabled())
                log.debug("New Balance after Update: " + targetAccount);
            return updateCount;
        } catch (SQLException se) {
            // rollback transaction if exception occurs
            log.error("updateAccountBalance(): User TransactionPayment Failed, rollback initiated for: " + accountId, se);
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException re) {
                throw new CustomException("Fail to rollback transaction", re);
            }
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(lockStmt);
            DbUtils.closeQuietly(updateStmt);
        }
        return updateCount;
    }

    public int transferAccountBalance(TransactionPayment transactionPayment) throws CustomException {
        int result = -1;
        Connection conn = null;
        PreparedStatement lockStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;
        Account fromAccount = null;
        Account toAccount = null;

        try {
            conn = H2Manager.getConnection();
            conn.setAutoCommit(false);
            // lock the credit and debit account for writing:
            lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
            lockStmt.setLong(1, transactionPayment.getSourceAccountId());
            rs = lockStmt.executeQuery();
            if (rs.next()) {
                fromAccount = new Account(rs.getLong("Id")
                        , rs.getString("PartyId")
                        , rs.getBigDecimal("Balance")
                        , rs.getString("Iban")
                        , rs.getString("CurrencyCodeType")
                        , rs.getString("AccountStatusType")
                        , rs.getString("CreateAccountDate")
                        , rs.getString("UpdateAccountDate"));

                if (log.isDebugEnabled())
                    log.debug("transferAccountBalance from Account: " + fromAccount);
            }
            lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
            lockStmt.setLong(1, transactionPayment.getDestinationAccountId());
            rs = lockStmt.executeQuery();
            if (rs.next()) {
                toAccount = new Account(rs.getLong("Id")
                        , rs.getString("PartyId")
                        , rs.getBigDecimal("Balance")
                        , rs.getString("Iban")
                        , rs.getString("CurrencyCodeType")
                        , rs.getString("AccountStatusType")
                        , rs.getString("CreateAccountDate")
                        , rs.getString("UpdateAccountDate"));
                if (log.isDebugEnabled())
                    log.debug("transferAccountBalance to Account: " + toAccount);
            }

            // check locking status
            if (fromAccount == null || toAccount == null) {
                throw new CustomException("Fail to lock both accounts for write");
            }

            // check transactionPayment currency
            if (!fromAccount.getCurrencyCodeType().equals(transactionPayment.getCurrencyCode())) {
                throw new CustomException(
                        "Fail to transfer, transactionPayment currency code are different from source/destination");
            }

            // check currency code is the same for both accounts
            if (!fromAccount.getCurrencyCodeType().equals(toAccount.getCurrencyCodeType())) {
                throw new CustomException(
                        "Fail to transfer, the source and destination account are in different currency");
            }

            // check enough fund in source account
            BigDecimal fromAccountLeftOver = fromAccount.getBalance().subtract(transactionPayment.getAmount());
            if (fromAccountLeftOver.compareTo(Validation.zeroAmount) < 0) {
                throw new CustomException("Not enough Fund from source Account ");
            }
            // proceed with update
            updateStmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
            updateStmt.setBigDecimal(1, fromAccountLeftOver);
            updateStmt.setString(2, Instant.now().toString());
            updateStmt.setLong(3, transactionPayment.getSourceAccountId());
            updateStmt.addBatch();
            updateStmt.setBigDecimal(1, toAccount.getBalance().add(transactionPayment.getAmount()));
            updateStmt.setString(2, Instant.now().toString());
            updateStmt.setLong(3, transactionPayment.getDestinationAccountId());
            updateStmt.addBatch();
            int[] rowsUpdated = updateStmt.executeBatch();
            result = rowsUpdated[0] + rowsUpdated[1];
            if (log.isDebugEnabled()) {
                log.debug("Number of rows updated for the transfer : " + result);
            }


            // If there is no error, commit the transactionPayment
            conn.commit();
        } catch (SQLException se) {
            // rollback transactionPayment if exception occurs
            log.error("transferAccountBalance(): User TransactionPayment Failed, rollback initiated for: "
                    + transactionPayment, se);
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException re) {
                throw new CustomException("Fail to rollback transactionPayment", re);
            }
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(lockStmt);
            DbUtils.closeQuietly(updateStmt);
        }
        return result;
    }


}
