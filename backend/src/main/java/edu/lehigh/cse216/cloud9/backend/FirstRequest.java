package edu.lehigh.cse216.cloud9.backend;

/**
 * FirstRequest provides a format for clients to present title and message
 * strings to the server.
 * 
 * NB: since this will be created from JSON, all fields must be public, and we
 * do not need a constructor.
 */
public class FirstRequest {
    /**
     * The title being provided by the client.
     */
    public String username;
    // session key
    public String password;

}