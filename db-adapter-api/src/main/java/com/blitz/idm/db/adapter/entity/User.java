package com.blitz.idm.db.adapter.entity;

import java.util.Date;

/**
 * This class represents the stored entity User.
 */
public interface User extends AbstractEntity {
    /**
     * This method return the login. It is assumed that login is unique.
     * @return user's login
     */
    public abstract String getLogin();

    /**
     * This method return the user's first name.
     * @return first name of the user
     */
    public abstract String getFirstName();

    /**
     * This method return the user's last name.
     * @return last name of the user
     */
    public abstract String getLastName();

    /**
     * This method return the user's middle name.
     * @return middle name of the user
     */
    public abstract String getMiddleName();

    /**
     * THis method return the user's birth date.
     * @return birth date of the user
     */
    public abstract Date getBirthDate();

    /**
     * This method set new first name to the user.
     * @param newFirstName user's new first name
     * @return link to the user itself
     */
    public abstract User setFirstName(final String newFirstName);

    /**
     * This method set new last name to the user.
     * @param newLastName user's new last name
     * @return link to the user itself
     */
    public abstract User setLastName(final String newLastName);

    /**
     * This method set new middle name to the user.
     * @param newMiddleName user's new middle name
     * @return link to the user itself
     */
    public abstract User setMiddleName(final String newMiddleName);


    /**
     * This method set new middle name to the user.
     * @param newBirthDate user's new birth date
     * @return link to the user itself
     */
    public abstract User setBirthDate(final Date newBirthDate);
}
