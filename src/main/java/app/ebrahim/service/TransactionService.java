package app.ebrahim.service;

import app.ebrahim.connection.RepositoryFactory;
import app.ebrahim.domain.TransactionPayment;
import app.ebrahim.error.CustomException;
import app.ebrahim.util.RandomGenerator;
import app.ebrahim.util.Validation;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;

@Path("/transaction")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionService {

    private final RepositoryFactory repositoryFactory = RepositoryFactory.getRepository(RepositoryFactory.H2);


    @POST
    public Response transfer(TransactionPayment transactionPayment) throws CustomException {

        String currency = transactionPayment.getCurrencyCode();
        if (Validation.currencyCode(currency)) {
            transactionPayment.setStan(RandomGenerator.stan());
            transactionPayment.setPersistenceTime(Instant.now().toString());
            int updateCount = repositoryFactory.getAccountRepository().transferAccountBalance(transactionPayment);
            if (updateCount == 2) {
                return Response.status(Response.Status.OK).build();
            } else {
                throw new WebApplicationException("Transaction failed", Response.Status.BAD_REQUEST);
            }
        } else {
            throw new WebApplicationException("Currency Code Type Invalid ", Response.Status.BAD_REQUEST);
        }

    }

}
