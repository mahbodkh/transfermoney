package app.ebrahim.service;


import app.ebrahim.connection.RepositoryFactory;
import app.ebrahim.domain.Party;
import app.ebrahim.error.CustomException;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/party")
@Produces(MediaType.APPLICATION_JSON)
public class PartyService {
    private static Logger log = Logger.getLogger(PartyService.class);
    private final RepositoryFactory repositoryFactory = RepositoryFactory.getRepository(RepositoryFactory.H2);


    @GET
    @Path("/{username}")
    public Party getUserByName(@PathParam("username") String username) throws CustomException {
        if (log.isDebugEnabled())
            log.debug("Request Received for get User by Name " + username);
        final Party party = repositoryFactory.getPartyRepository().getPartyByName(username);
        if (party == null) {
            throw new WebApplicationException("User Not Found", Response.Status.NOT_FOUND);
        }
        return party;
    }


    @GET
    @Path("/all")
    public List<Party> getAllParties() throws CustomException {
        return repositoryFactory.getPartyRepository().getAllParties();
    }


    @POST
    @Path("/create")
    public Party createParty(Party party) throws CustomException {
        if (repositoryFactory.getPartyRepository().getPartyByName(party.getUsername()) != null) {
            throw new WebApplicationException("Party already exist", Response.Status.BAD_REQUEST);
        }
        final long uId = repositoryFactory.getPartyRepository().insertParty(party);
        return repositoryFactory.getPartyRepository().getPartyById(uId);
    }


    @PUT
    @Path("/{partyId}")
    public Response updateUser(@PathParam("partyId") long partyId, Party party) throws CustomException {
        final int updateCount = repositoryFactory.getPartyRepository().updateParty(partyId, party);
        if (updateCount == 1) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{partyId}")
    public Response deleteUser(@PathParam("partyId") long partyId) throws CustomException {
        int deleteCount = repositoryFactory.getPartyRepository().deleteParty(partyId);
        if (deleteCount == 1) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
