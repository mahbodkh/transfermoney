package app.ebrahim.service;


import app.ebrahim.connection.RepositoryFactory;
import app.ebrahim.domain.Account;
import app.ebrahim.error.CustomException;
import app.ebrahim.util.Validation;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountService {
    private static Logger log = Logger.getLogger(AccountService.class);
    private final RepositoryFactory repositoryFactory = RepositoryFactory.getRepository(RepositoryFactory.H2);


    @GET
    @Path("/all")
    public List<Account> getAllAccounts() throws CustomException {
        return repositoryFactory.getAccountRepository().getAllAccounts();
    }

    @GET
    @Path("/{accountId}")
    public Account getAccount(@PathParam("accountId") long accountId) throws CustomException {
        return repositoryFactory.getAccountRepository().getAccountById(accountId);
    }

    @GET
    @Path("/{accountId}/balance")
    public BigDecimal getBalance(@PathParam("accountId") long accountId) throws CustomException {
        final Account account = repositoryFactory.getAccountRepository().getAccountById(accountId);

        if (account == null) {
            throw new WebApplicationException("Account not found", Response.Status.NOT_FOUND);
        }
        return account.getBalance();
    }

    @POST
    @Path("/create")
    public Account createAccount(Account account) throws CustomException {
        final long accountId = repositoryFactory.getAccountRepository().createAccount(account);
        return repositoryFactory.getAccountRepository().getAccountById(accountId);
    }

    @PUT
    @Path("/{accountId}/deposit/{amount}")
    public Account deposit(@PathParam("accountId") long accountId, @PathParam("amount") BigDecimal amount) throws CustomException {

        if (amount.compareTo(Validation.zeroAmount) <= 0) {
            throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
        }

        repositoryFactory.getAccountRepository().updateAccountBalance(accountId, amount.setScale(4, RoundingMode.HALF_EVEN));
        return repositoryFactory.getAccountRepository().getAccountById(accountId);
    }


    @PUT
    @Path("/{accountId}/withdraw/{amount}")
    public Account withdraw(@PathParam("accountId") long accountId, @PathParam("amount") BigDecimal amount) throws CustomException {

        if (amount.compareTo(Validation.zeroAmount) <= 0) {
            throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
        }
        BigDecimal delta = amount.negate();
        if (log.isDebugEnabled())
            log.debug("Withdraw service: delta change to account  " + delta + " Account ID = " + accountId);
        repositoryFactory.getAccountRepository().updateAccountBalance(accountId, delta.setScale(4, RoundingMode.HALF_EVEN));
        return repositoryFactory.getAccountRepository().getAccountById(accountId);
    }


    @DELETE
    @Path("/{accountId}")
    public Response deleteAccount(@PathParam("accountId") long accountId) throws CustomException {
        int deleteCount = repositoryFactory.getAccountRepository().deleteAccountById(accountId);
        if (deleteCount == 1) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}