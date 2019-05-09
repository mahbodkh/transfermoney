package app.ebrahim.repository;

import app.ebrahim.domain.Account;
import app.ebrahim.domain.TransactionPayment;
import app.ebrahim.error.CustomException;

import java.math.BigDecimal;
import java.util.List;

public interface AccountRepository {

    List<Account> getAllAccounts() throws CustomException;

    Account getAccountById(Long accountId) throws CustomException;

    long createAccount(Account account) throws CustomException;

    int deleteAccountById(Long accountId) throws CustomException;

    int updateAccountBalance(Long accountId, BigDecimal deltaAmount) throws CustomException;

    int transferAccountBalance(TransactionPayment transactionPayment) throws CustomException;

}
