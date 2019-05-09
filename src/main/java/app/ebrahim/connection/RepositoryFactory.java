package app.ebrahim.connection;

import app.ebrahim.repository.AccountRepository;
import app.ebrahim.repository.PartyRepository;

public abstract class RepositoryFactory {

    public static final int H2 = 1;

    public abstract PartyRepository getPartyRepository();

    public abstract AccountRepository getAccountRepository();

    public abstract void populateTestData();

    public static RepositoryFactory getRepository(int factoryCode) {

        switch (factoryCode) {
            case H2:
                return new H2Manager();
            default:
                // by default using H2 in memory database
                return new H2Manager();
        }
    }
}
