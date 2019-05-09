package app.ebrahim.repository;

import app.ebrahim.connection.H2Manager;
import app.ebrahim.domain.Party;
import app.ebrahim.error.CustomException;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PartyRepositoryImpl implements PartyRepository {
    private static Logger log = Logger.getLogger(PartyRepositoryImpl.class);

    private final static String SQL_GET_PARTY_BY_ID = "SELECT * FROM Party WHERE Id = ? ";
    private final static String SQL_GET_ALL_PARTIES = "SELECT * FROM Party";
    private final static String SQL_GET_PARTY_BY_NAME = "SELECT * FROM Party WHERE Username = ? ";
    private final static String SQL_INSERT_PARTY = "INSERT INTO Party (Username, Email, CreatePartyDate) VALUES (?, ?, ?)";
    private final static String SQL_UPDATE_PARTY = "UPDATE Party SET Username = ?, Email = ? WHERE Id = ? ";
    private final static String SQL_DELETE_PARTY_BY_ID = "DELETE FROM Party WHERE Id = ? ";


    public List<Party> getAllParties() throws CustomException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Party> users = new ArrayList<Party>();
        try {
            conn = H2Manager.getConnection();
            stmt = conn.prepareStatement(SQL_GET_ALL_PARTIES);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Party u = new Party(rs.getLong("Id")
                        , rs.getString("Username")
                        , rs.getString("Email")
                        , rs.getString("CreatePartyDate")
                );
                users.add(u);
                if (log.isDebugEnabled())
                    log.debug("getAllParties() Retrieve Party: " + u);
            }
            return users;
        } catch (SQLException e) {
            throw new CustomException("Error reading Party data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }


    public Party getPartyById(Long PartyId) throws CustomException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Party party = null;
        try {
            conn = H2Manager.getConnection();
            stmt = conn.prepareStatement(SQL_GET_PARTY_BY_ID);
            stmt.setLong(1, PartyId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                party = new Party(rs.getLong("Id")
                        , rs.getString("Username")
                        , rs.getString("Email")
                        , rs.getString("CreatePartyDate")
                );
                if (log.isDebugEnabled())
                    log.debug("getPartyById(): Retrieve Party: " + party);
            }
            return party;
        } catch (SQLException e) {
            throw new CustomException("Error reading Party data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }


    public Party getPartyByName(String userName) throws CustomException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Party party = null;
        try {
            conn = H2Manager.getConnection();
            stmt = conn.prepareStatement(SQL_GET_PARTY_BY_NAME);
            stmt.setString(1, userName);
            rs = stmt.executeQuery();
            if (rs.next()) {
                party = new Party(rs.getLong("Id")
                        , rs.getString("Username")
                        , rs.getString("Email")
                        , rs.getString("CreatePartyDate")
                );
                if (log.isDebugEnabled())
                    log.debug("Retrieve Party: " + party);
            }
            return party;
        } catch (SQLException e) {
            throw new CustomException("Error reading Party data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }


    public long insertParty(Party party) throws CustomException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        try {
            conn = H2Manager.getConnection();
            stmt = conn.prepareStatement(SQL_INSERT_PARTY, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, party.getUsername());
            stmt.setString(2, party.getEmail());
            stmt.setString(3, Instant.now().toString());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                log.error("insertParty(): Creating Party failed, no rows affected." + party);
                throw new CustomException("Parties Cannot be created");
            }
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            } else {
                log.error("insertPart():  Creating Party failed, no ID obtained." + party);
                throw new CustomException("Parties Cannot be created");
            }
        } catch (SQLException e) {
            log.error("Error Inserting Party :" + party);
            throw new CustomException("Error creating Party data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, generatedKeys);
        }

    }

    public int updateParty(Long PartyId, Party party) throws CustomException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = H2Manager.getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE_PARTY);
            stmt.setString(1, party.getUsername());
            stmt.setString(2, party.getEmail());
            stmt.setLong(3, PartyId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error Updating Party :" + party);
            throw new CustomException("Error update Party data", e);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(stmt);
        }
    }

    public int deleteParty(Long PartyId) throws CustomException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = H2Manager.getConnection();
            stmt = conn.prepareStatement(SQL_DELETE_PARTY_BY_ID);
            stmt.setLong(1, PartyId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error Deleting Party :" + PartyId);
            throw new CustomException("Error Deleting Party ID:" + PartyId, e);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(stmt);
        }
    }
}
