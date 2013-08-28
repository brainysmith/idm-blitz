package com.blitz.idm.db.adapter.entity;

import com.blitz.idm.db.adapter.connection.Connection;

/**
 * This interface represents common things across all entities in
 * the system.
 */
public interface AbstractEntity {
    public abstract void save(final Connection connection);
}
