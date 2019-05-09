package app.ebrahim.repository;

import app.ebrahim.domain.Party;
import app.ebrahim.error.CustomException;

import java.util.List;

public interface PartyRepository {

    List<Party> getAllParties() throws CustomException;

    Party getPartyById(Long userId) throws CustomException;

    Party getPartyByName(String userName) throws CustomException;

    long insertParty(Party party) throws CustomException;

    int updateParty(Long userId, Party party) throws CustomException;

    int deleteParty(Long userId) throws CustomException;

}
