package edu.lehigh.cse216.cloud9.backend;

/**
 * FirstRequest provides a format for clients to present username/password
 * 
 * NB: since this will be created from JSON, all fields must be public, and we
 * do not need a constructor.
 */
public class FirstRequest {
    /**
     * username
     */
    public String username;
    // password
    public String password;

}