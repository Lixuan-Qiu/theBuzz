package edu.lehigh.cse216.cloud9.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Database {
    /**
     * The connection to the database. When there is no connection, it should be
     * null. Otherwise, there is a valid open connection
     */
    private Connection mConnection;
    /**
     * A prepared statement for getting all data in the database
     */
    private PreparedStatement mSelectAll;
    /**
     * A prepared statement for getting one row from the database
     */
    private PreparedStatement mSelectOne;
    /**
     * A prepared statement for deleting a row from the database
     */
    private PreparedStatement mDeleteOne;
    /**
     * A prepared statement for inserting into the database
     */
    private PreparedStatement mInsertOne;
    /**
     * A prepared statement for updating a single row in the database
     */
    private PreparedStatement mUpdateOne;
    /*
     * increase the amount of like/dislike by one
     */
    private PreparedStatement mAddLike;
    private PreparedStatement mAddDislike;
    /**
     * A prepared statement for creating the table in our database
     */
    private PreparedStatement mCreateTable;
    /**
     * A prepared statement for dropping the table in our database
     */
    private PreparedStatement mDropTable;

    //all prepared statment for  User table
    private PreparedStatement uSelectAll;
    private PreparedStatement uSelectOne;
    private PreparedStatement uDeleteOne;
    private PreparedStatement uInsertOne;
    private PreparedStatement uUpdatePassword;
    private PreparedStatement uCreateTable;
    private PreparedStatement uDropTable;
    private PreparedStatement uUpdateProfile;

    //all prepared statment for Comment table
    private PreparedStatement cSelectAll;
    private PreparedStatement cSelectOne;
    private PreparedStatement cDeleteOne;
    private PreparedStatement cInsertOne;
    private PreparedStatement cUpdateOne;
    private PreparedStatement cCreateTable;
    private PreparedStatement cDropTable;

    /* data structure for message */
    public static class message_RowData {
        /**
         * The ID of this message
         */
        int mId;

        /**
         * The message stored in this row
         */
        String mMessage;

        // number of like
        int mlikeCount;
        // number of dislike
        int mdislikeCount;

        // ID of the creator of this message
        int uId;
        /**
         * Construct a RowData object by providing values for its fields
         */

        public message_RowData(int id, String message, int likeCount, int dislikeCount, int uid) {
            mId = id;
            mMessage = message;
            mlikeCount = likeCount;
            mdislikeCount = dislikeCount;
            uId = uid;
        }
    }
    /* data structure for user table */
    public static class user_RowData {
        /**
         * The ID of this user
         */
        int uId;

        // the actual username 
        String uUsername;

        // the real name of the user
        String uRealname;
        // the comment on profile (user creates it themselves)
        String uProfile;
        // the email of the user
        String uEmail;
        // the salt
        String uSalt;
        // the password after hashing
        String uPassword;
        /**
         * Construct a RowData object by providing values for its fields
         */

        public user_RowData(int uid, String username, String realname, String profile, String email, String salt, String password) {
            uId = uid;
            uUsername = username;
            uRealname = realname;
            uProfile = profile;
            uEmail = email;
            uSalt = salt;
            uPassword = password;
        }
    }

    /* data structure for comment table */
    public static class comment_RowData {
        // the id of the comment
        int cId
        /**
         * The ID of the user that comment
         */
        int uId;
        // the message id that commented on
        int mId;
        //the comment
        String cComment;
        /**
         * Construct a RowData object by providing values for its fields
         */

        public comment_RowData(int cid, int uid, int id, int comment) {
            cId = cid
            uId = uid;
            mId = id;
            cComment = comment;
        }
    }

    /**
     * The Database constructor is private: we only create Database objects through
     * the getDatabase() method.
     */
    private Database() {
    }

    /**
     * Get a fully-configured connection to the database
     * 
     * @param ip   The IP address of the database server
     * @param port The port on the database server to which connection requests
     *             should be sent
     * @param user The user ID to use when connecting
     * @param pass The password to use when connecting
     * 
     * @return A Database object, or null if we cannot connect properly
     */
    static Database getDatabase(String db_url) {
        // Create an un-configured Database object
        Database db = new Database();

        try {
            Class.forName("org.postgresql.Driver");
            URI dbUri = new URI(db_url);
            System.out.println(dbUri);
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath()
                    + "?sslmode=require";
            Connection conn = DriverManager.getConnection(dbUrl, username, password);

            if (conn == null) {
                System.err.println("Error: DriverManager.getConnection() returned a null object");
                return null;
            }

            db.mConnection = conn;
        } catch (SQLException e) {
            System.err.println("Error: DriverManager.getConnection() threw a SQLException");
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Unable to find postgresql driver");
            return null;
        } catch (URISyntaxException s) {
            System.out.println("URI Syntax Error");
            return null;
        }

        // Attempt to create all of our prepared statements. If any of these
        // fail, the whole getDatabase() call should fail
        try {
            // NB: we can easily get ourselves in trouble here by typing the
            // SQL incorrectly. We really should have things like "tblMessage"
            // as constants, and then build the strings for the statements
            // from those constants.

            // Note: no "IF NOT EXISTS" or "IF EXISTS" checks on table
            // creation/deletion, so multiple executions will cause an exception
            db.mCreateTable = db.mConnection.prepareStatement("CREATE TABLE tblMessage (id SERIAL PRIMARY KEY, "
                    + "message VARCHAR(500) NOT NULL, likeCount INT NOT NULL, dislikeCount INT NOT NULL, uid INT NOT NULL)");

            db.uCreateTable = db.mConnection.prepareStatement("CREATE TABLE tblUser (uid SERIAL PRIMARY KEY, "
                    + "username VARCHAR(100) NOT NULL, realname VARCHAR(100) NOT NULL, profile VARCHAR(200) NOT NULL, "
                    + "email VARCHAR(50), salt VARCHAR(200), password VARCHAR(400)");
            db.cCreateTable = db.mConnection.prepareStatement("CREATE TABLE tblComment (cid SERIAL PRIMARY KEY, "
                    + "uid INT NOT NULL, mid INT NOT NULL, comment VARCHAR(200) NOT NULL)");

            db.mDropTable = db.mConnection.prepareStatement("DROP TABLE tblMessage");
            db.uDropTable = db.mConnection.prepareStatement("DROP TABLE tblUser");
            db.cDropTable = db.mConnection.prepareStatement("DROP TABLE tblComment");
            // Standard CRUD operations for Message table
            db.mDeleteOne = db.mConnection.prepareStatement("DELETE FROM tblMessage WHERE id = ?");
            db.mInsertOne = db.mConnection.prepareStatement("INSERT INTO tblMessage VALUES (default, ?, 0, 0, ?)");
            db.mSelectAll = db.mConnection.prepareStatement("SELECT id , message, likeCount, dislikeCount, uid FROM tblMessage");
            db.mSelectOne = db.mConnection.prepareStatement("SELECT * from tblMessage WHERE id=?");
            db.mUpdateOne = db.mConnection.prepareStatement("UPDATE tblMessage SET message = ? WHERE id = ?");
            db.mAddLike = db.mConnection.prepareStatement("UPDATE tblMessage SET likeCount = ? WHERE id = ?");
            db.mAddDislike = db.mConnection.prepareStatement("UPDATE tblMessage SET dislikeCount = ? WHERE id = ?");
            // Standard CRUD operations for User table
            db.uDeleteOne = db.mConnection.prepareStatement("DELETE FROM tblUser WHERE uid = ?");
            db.uInsertOne = db.mConnection.prepareStatement("INSERT INTO tblUser VALUES (default, ?, ?, "-", ?, ?, ?)");
            db.uSelectAll = db.mConnection.prepareStatement("SELECT uid , username, realname, profile, email FROM tblUser");
            db.uSelectOne = db.mConnection.prepareStatement("SELECT * from tblUser WHERE uid=?");
            db.uUpdateProfile = db.mConnection.prepareStatement("UPDATE tblUser SET profile = ? WHERE uid = ?");
            db.uUpdatePassword = db.mConnection.prepareStatement("UPDATE tblUser SET password = ? WHERE uid = ?");
            // Standard CRUD operations for Comment table
            db.cDeleteOne = db.mConnection.prepareStatement("DELETE FROM tblComment WHERE cid = ?");
            db.cInsertOne = db.mConnection.prepareStatement("INSERT INTO tblComment VALUES (default, ?, ?, ?)");
            db.cSelectAll = db.mConnection.prepareStatement("SELECT cid , uid, mid, comment FROM tblMessage");
            db.cSelectOne = db.mConnection.prepareStatement("SELECT * from tblComment WHERE cid=?");
            db.cUpdateOne = db.mConnection.prepareStatement("UPDATE tblComment SET comment = ? WHERE cid = ?");
        } catch (SQLException e) {
            System.err.println("Error creating prepared statement");
            e.printStackTrace();
            db.disconnect();
            return null;
        }
        return db;
    }

    /**
     * Close the current connection to the database, if one exists.
     * 
     * NB: The connection will always be null after this call, even if an error
     * occurred during the closing operation.
     * 
     * @return True if the connection was cleanly closed, false otherwise
     */
    boolean disconnect() {
        if (mConnection == null) {
            System.err.println("Unable to close connection: Connection was null");
            return false;
        }
        try {
            mConnection.close();
        } catch (SQLException e) {
            System.err.println("Error: Connection.close() threw a SQLException");
            e.printStackTrace();
            mConnection = null;
            return false;
        }
        mConnection = null;
        return true;
    }

    /**
     * Insert a row into the database
     * 
     * @param message The message body for this new row
     * 
     * @return The number of rows that were inserted
     */
    int insert_messageRow(String message, int uid) {
        int count = 0;
        try {
            mInsertOne.setString(1, message);
            mInsertOne.setInt(4, uid);
            count += mInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Query the database for a list of all subjects and their IDs
     * 
     * @return All rows, as an ArrayList
     */
    ArrayList<RowData> select_messageAll() {
        ArrayList<RowData> res = new ArrayList<RowData>();
        try {
            ResultSet rs = mSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new RowData(rs.getInt("id"), rs.getString("message"), rs.getInt("likeCount"), rs.getInt("dislikeCount"), rs.getInt("uid")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all data for a specific row, by ID
     * 
     * @param id The id of the row being requested
     * 
     * @return The data for the requested row, or null if the ID was invalid
     */
    RowData select_messageOne(int id) {
        RowData res = null;
        try {
            mSelectOne.setInt(1, id);
            ResultSet rs = mSelectOne.executeQuery();
            if (rs.next()) {
                res = new RowData(rs.getInt("id"), rs.getString("message"), rs.getInt("likeCount"), rs.getInt("dislikeCount"), rs.getInt("uid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Delete a row by ID
     * 
     * @param id The id of the row to delete
     * 
     * @return The number of rows that were deleted. -1 indicates an error.
     */
    int delete_messageRow(int id) {
        int res = -1;
        try {
            mDeleteOne.setInt(1, id);
            res = mDeleteOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Update the message for a row in the database
     * 
     * @param id      The id of the row to update
     * @param message The new message contents
     * 
     * @return The number of rows that were updated. -1 indicates an error.
     */
    int update_messageOne(int id, String message) {
        int res = -1;
        try {
            mUpdateOne.setString(1, message);
            mUpdateOne.setInt(2, id);
            res = mUpdateOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /* increase likeCount */
    int addLike(int id) {
        int res = -1;
        RowData data = selectOne(id);
        int newCount = data.mlikeCount + 1;
        try {
            mAddLike.setInt(1, newCount);
            mAddLike.setInt(2, id);
            res = mAddLike.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /* increase dislikeCount */
    int addDislike(int id) {
        int res = -1;
        RowData data = selectOne(id);
        int newCount = data.mdislikeCount + 1;
        try {
            mAddDislike.setInt(1, newCount);
            mAddDislike.setInt(2, id);
            res = mAddDislike.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Create tblMessage. If it already exists, this will print an error
     */
    void create_messageTable() {
        try {
            mCreateTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove tblMessage from the database. If it does not exist, this will print an
     * error.
     */
    void drop_messageTable() {
        try {
            mDropTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert a row into the user database
     * 
     * @return The number of rows that were inserted
     */
    int insert_userRow(int uid, String username, String realname, String email, String password) {
        int count = 0;
        String salt = "";//create salt
        try {
            uInsertOne.setString(1, username);
            uInsertOne.setString(2, realname);
            uInsertOne.setString(4, email);
            uInsertOne.setString(5, salt);
            uInsertOne.setString(6, password);
            count += uInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}