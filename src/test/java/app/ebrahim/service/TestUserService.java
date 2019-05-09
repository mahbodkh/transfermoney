package app.ebrahim.service;

import app.ebrahim.domain.Party;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TestUserService extends TestService {

    @Test
    public void testGetUser() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/party/Joe").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();

        assertEquals(200, statusCode);
        //check the content
        String jsonString = EntityUtils.toString(response.getEntity());
        Party party = mapper.readValue(jsonString, Party.class);
        assertEquals("Joe", party.getUsername());
        assertEquals("joe_k@gmail.com", party.getEmail());
    }

    @Test
    public void testGetAllParties() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/party/all").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
        //check the content
        String jsonString = EntityUtils.toString(response.getEntity());
        Party[] users = mapper.readValue(jsonString, Party[].class);
        assertTrue(users.length > 0);
    }


    @Test
    public void testCreateParty() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/party/create").build();
        Party party = new Party(1L, "liandre", "liandre@gmail.com", Instant.now().toString());
        String jsonInString = mapper.writeValueAsString(party);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
        String jsonString = EntityUtils.toString(response.getEntity());
        Party uAfterCreation = mapper.readValue(jsonString, Party.class);
        assertEquals("liandre", uAfterCreation.getUsername());
        assertEquals("liandre@gmail.com", uAfterCreation.getEmail());
    }


    @Test
    public void testCreateExistingUser() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/party/create").build();
        Party party = new Party(1L, "test1", "test1@gmail.com", Instant.now().toString());
        String jsonInString = mapper.writeValueAsString(party);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(400, statusCode);
    }


    @Test
    public void testUpdateUser() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/party/2").build();
        Party party = new Party(2L, "test1", "test1123@gmail.com", Instant.now().toString());
        String jsonInString = mapper.writeValueAsString(party);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
    }


    @Test
    public void testUpdateNonExistingUser() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/party/100").build();
        Party party = new Party(2L, "test1", "test1123@gmail.com", Instant.now().toString());
        String jsonInString = mapper.writeValueAsString(party);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);
    }


    @Test
    public void testDeleteUser() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/party/3").build();
        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
    }


    @Test
    public void testDeleteNonExistingUser() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/party/300").build();
        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 404);
    }


}
