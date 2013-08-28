package com.blitz.idm.db.adapter.connection;

import com.blitz.idm.db.adapter.entity.User;

/**
 * This class represents the connection to the data storage.
 * Client can use this class to execute basic operations.
 */
public interface Connection {
    /**
     * This method finds user in the database and returns it.
     * @param login user login
     * @return user instance
     */
    public abstract User findUser(final String login);

    /**
     * This method stores user in the database and returns it.
     * @param user instance of User class to store
     */
    public abstract void storeUser(final User user);
}
