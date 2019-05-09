package app.ebrahim.repository;

import app.ebrahim.connection.H2Manager;
import app.ebrahim.connection.RepositoryFactory;
import app.ebrahim.domain.Party;
import app.ebrahim.error.CustomException;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.List;

import static junit.framework.TestCase.*;

public class TestPartyRepository {

    private static Logger log = Logger.getLogger(TestPartyRepository.class);
    private static final RepositoryFactory repository = RepositoryFactory.getRepository(H2Manager.H2);

    @BeforeClass
    public static void setup() {
        // prepare test database and test data by executing sql script demo.sql
        log.debug("setting up test database and sample data....");
        repository.populateTestData();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testGetAllUsers() throws CustomException {
        List<Party> allUsers = repository.getPartyRepository().getAllParties();
        assertTrue(allUsers.size() > 1);
    }

    @Test
    public void testGetUserById() throws CustomException {
        Party party = repository.getPartyRepository().getPartyById(2L);
        assertEquals("Khosravani", party.getUsername());
    }

    @Test
    public void testGetNonExistingUserById() throws CustomException {
        Party party = repository.getPartyRepository().getPartyById(500L);
        assertNull(party);
    }

    @Test
    public void testGetNonExistingUserByName() throws CustomException {
        Party party = repository.getPartyRepository().getPartyByName("abcdeftg");
        assertNull(party);
    }

    @Test
    public void testCreateUser() throws CustomException {
        Party party = new Party(1L, "liandre", "liandre@gmail.com", Instant.now().toString());
        long id = repository.getPartyRepository().insertParty(party);
        Party uAfterInsert = repository.getPartyRepository().getPartyById(id);
        assertEquals("liandre", uAfterInsert.getUsername());
        assertEquals("liandre@gmail.com", party.getEmail());
    }

    @Test
    public void testUpdateUser() throws CustomException {
        Party party = new Party(1L, "test2", "test2@gmail.com", Instant.now().toString());
        int rowCount = repository.getPartyRepository().updateParty(1L, party);
        // assert one row(user) updated
        assertEquals(1, rowCount);
        assertEquals("test2@gmail.com", repository.getPartyRepository().getPartyById(1L).getEmail());
    }

    @Test
    public void testUpdateNonExistingUser() throws CustomException {
        Party party = new Party(500L, "test2", "test2@gmail.com", Instant.now().toString());
        int rowCount = repository.getPartyRepository().updateParty(500L, party);
        // assert one row(user) updated
        assertEquals(0, rowCount);
    }

    @Test
    public void testDeleteUser() throws CustomException {
        int rowCount = repository.getPartyRepository().deleteParty(1L);
        // assert one row(user) deleted
        assertEquals(1, rowCount);
        // assert user no longer there
        assertNull(repository.getPartyRepository().getPartyById(1L));
    }

    @Test
    public void testDeleteNonExistingUser() throws CustomException {
        int rowCount = repository.getPartyRepository().deleteParty(500L);
        // assert no row(user) deleted
        assertEquals(0, rowCount);
    }

}
