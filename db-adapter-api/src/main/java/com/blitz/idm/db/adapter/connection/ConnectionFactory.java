package com.blitz.idm.db.adapter.connection;

/**
 * This class represents the factory, which should be used for obtaining
 * connections by clients.
 */
public interface ConnectionFactory {
    /**
     * This method allows clients to obtain connection object.
     * @return connection instance
     */
    public abstract Connection getConnection();
}
