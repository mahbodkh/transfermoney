package app.ebrahim.service;

import app.ebrahim.domain.Account;
import app.ebrahim.domain.TransactionPayment;
import app.ebrahim.util.RandomGenerator;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TestTransactionPaymentService extends TestService {
    //test transaction related operations in the account

    @Test
    public void testDeposit() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/1/deposit/100").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
        String jsonString = EntityUtils.toString(response.getEntity());
        Account afterDeposit = mapper.readValue(jsonString, Account.class);
        //check balance is increased from 100 to 200
        assertEquals(afterDeposit.getBalance(), new BigDecimal(200).setScale(4, RoundingMode.HALF_EVEN));
    }


    @Test
    public void testWithDrawSufficientFund() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/2/withdraw/100").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
        String jsonString = EntityUtils.toString(response.getEntity());
        Account afterDeposit = mapper.readValue(jsonString, Account.class);
        //check balance is decreased from 200 to 100
        assertEquals(afterDeposit.getBalance(), new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN));
    }


    @Test
    public void testWithDrawNonSufficientFund() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/2/withdraw/1000.23456").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        String responseBody = EntityUtils.toString(response.getEntity());
        assertEquals(500, statusCode);
        assertTrue(responseBody.contains("Not sufficient Fund"));
    }


    @Test
    public void testTransactionEnoughBalance() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/transaction").build();
        BigDecimal amount = new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN);

        TransactionPayment transactionPayment = new TransactionPayment(
                "EUR"
                , amount
                , RandomGenerator.stan()
                , 3L
                , 4L
                , Instant.now().toString());

        String jsonInString = mapper.writeValueAsString(transactionPayment);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
    }


    @Test
    public void testTransactionNotEnoughBalance() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/transaction").build();
        BigDecimal amount = new BigDecimal(100000).setScale(4, RoundingMode.HALF_EVEN);
        TransactionPayment transactionPayment = new TransactionPayment(
                "EUR"
                , amount
                , RandomGenerator.stan()
                , 1L
                , 2L
                , Instant.now().toString());

        String jsonInString = mapper.writeValueAsString(transactionPayment);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(500, statusCode);
    }


    @Test
    public void testTransactionDifferentCurrencyCode() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/transaction").build();
        BigDecimal amount = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
        TransactionPayment transactionPayment = new TransactionPayment(
                "USD"
                , amount
                , RandomGenerator.stan()
                , 3L
                , 4L
                , Instant.now().toString());

        String jsonInString = mapper.writeValueAsString(transactionPayment);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(500, statusCode);
    }


}
