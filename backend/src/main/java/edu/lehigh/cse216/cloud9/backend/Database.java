package edu.lehigh.cse216.cloud9.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

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

    // all prepared statment for User table
    private PreparedStatement uSelectAll;
    private PreparedStatement uSelectOne;
    private PreparedStatement uDeleteOne;
    private PreparedStatement uInsertOne;
    private PreparedStatement uUpdatePassword;
    private PreparedStatement uSelectPassword;
    private PreparedStatement uSelectSalt;
    private PreparedStatement uCreateTable;
    private PreparedStatement uDropTable;
    private PreparedStatement uUpdateProfile;
    private PreparedStatement uGetuId;

    // all prepared statment for Comment table
    private PreparedStatement cSelectAll;
    private PreparedStatement cSelectOne;
    private PreparedStatement cSelectMessage;
    private PreparedStatement cDeleteOne;
    private PreparedStatement cInsertOne;
    private PreparedStatement cCreateTable;
    private PreparedStatement cDropTable;

    // all prepared statment for session key table
    private PreparedStatement sSelectAll;
    private PreparedStatement sSelectOne;
    private PreparedStatement sDeleteOne;
    private PreparedStatement sInsertOne;
    private PreparedStatement sCreateTable;
    private PreparedStatement sDropTable;
    private PreparedStatement sGetKey;
    // all prepared statment for Vote table
    private PreparedStatement vSelectAll;
    private PreparedStatement vSelectOne;
    private PreparedStatement vDeleteOne;
    private PreparedStatement vInsertOne;
    private PreparedStatement vUpdateOne;
    private PreparedStatement vCreateTable;
    private PreparedStatement vDropTable;

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

        public user_RowData(int uid, String username, String realname, String profile, String email, String salt,
                String password) {
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
        int cId;
        /**
         * The ID of the user that comment
         */
        int uId;
        // the message id that commented on
        int mId;
        // the comment
        String cComment;

        /**
         * Construct a RowData object by providing values for its fields
         */

        public comment_RowData(int cid, int uid, int id, String comment) {
            cId = cid;
            uId = uid;
            mId = id;
            cComment = comment;
        }
    }

    /* data structure for vote table */
    public static class vote_RowData {
        // the id of the comment
        int uId;
        // message
        int mId;
        // the message id that commented on
        int vote;

        /**
         * Construct a RowData object by providing values for its fields
         */

        public vote_RowData(int uid, int id, int newvote) {
            uId = uid;
            mId = id;
            vote = newvote;
        }
    }

    /* data structure for session table */
    public static class session_RowData {
        // the key of the session
        int key;
        /**
         * The ID of the user that comment
         */
        int uId;

        /**
         * Construct a RowData object by providing values for its fields
         */

        public session_RowData(int sessionkey, int uid) {
            key = sessionkey;
            uId = uid;
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

            //////////////////// ALL table creation ////////////////////
            // create message_table
            db.mCreateTable = db.mConnection.prepareStatement("CREATE TABLE tblMessage (" + "id SERIAL PRIMARY KEY, "
                    + "message VARCHAR(500) NOT NULL, " + "likeCount INT NOT NULL, " + "dislikeCount INT NOT NULL, "
                    + "FOREIGN KEY (uid) REFERENCES tblUser (uid))");
            // create user_table
            db.uCreateTable = db.mConnection.prepareStatement(
                    "CREATE TABLE tblUser (" + "uid SERIAL PRIMARY KEY, " + "username VARCHAR(100) NOT NULL, "
                            + "realname VARCHAR(100) NOT NULL, " + "profile VARCHAR(200) NOT NULL, "
                            + "email VARCHAR(50), " + "salt VARCHAR(200), " + "password VARCHAR(400)");
            // create comment_table
            db.cCreateTable = db.mConnection.prepareStatement("CREATE TABLE tblComment (" + "cid SERIAL PRIMARY KEY, "
                    + "FOREIGN KEY (uid) REFERENCES tblUser (uid), " + "FOREIGN KEY (id) REFERENCES tblMessage (id), "
                    + "comment VARCHAR(200) NOT NULL)");
            // create session_table
            db.sCreateTable = db.mConnection.prepareStatement("CREATE TABLE tblSession (" + "key SERIAL PRIMARY KEY, "
                    + "FOREIGN KEY (uid) REFERENCES tblUser (uid))");
            // create vote_table
            db.vCreateTable = db.mConnection
                    .prepareStatement("CREATE TABLE tblVote (" + "FOREIGN KEY (uid) REFERENCES tblUser (uid), "
                            + "FOREIGN KEY (id) REFERENCES tblMessage (id), vote INT NOT NULL");

            //////////////////// All table deleetion ////////////////////
            db.mDropTable = db.mConnection.prepareStatement("DROP TABLE tblMessage");
            db.uDropTable = db.mConnection.prepareStatement("DROP TABLE tblUser");
            db.cDropTable = db.mConnection.prepareStatement("DROP TABLE tblComment");
            db.sDropTable = db.mConnection.prepareStatement("DROP TABLE tblSession");
            db.vDropTable = db.mConnection.prepareStatement("DROP TABLE tblVote");

            // Standard CRUD operations for message_table
            db.mDeleteOne = db.mConnection.prepareStatement("DELETE FROM tblMessage WHERE id = ?");
            db.mInsertOne = db.mConnection.prepareStatement("INSERT INTO tblMessage VALUES (default, ?, 0, 0, ?)");
            db.mSelectAll = db.mConnection
                    .prepareStatement("SELECT id , message, likeCount, dislikeCount, uid FROM tblMessage");
            db.mSelectOne = db.mConnection.prepareStatement("SELECT * from tblMessage WHERE id=?");
            db.mUpdateOne = db.mConnection.prepareStatement("UPDATE tblMessage SET message = ? WHERE id = ?");
            db.mAddLike = db.mConnection.prepareStatement("UPDATE tblMessage SET likeCount = ? WHERE id = ?");
            db.mAddDislike = db.mConnection.prepareStatement("UPDATE tblMessage SET dislikeCount = ? WHERE id = ?");

            // Standard CRUD operations for user_table
            db.uDeleteOne = db.mConnection.prepareStatement("DELETE FROM tblUser WHERE uid = ?");
            db.uInsertOne = db.mConnection.prepareStatement("INSERT INTO tblUser VALUES (default, ?, ?, ?, ?, ?, ?)");
            db.uSelectPassword = db.mConnection.prepareStatement("SELECT password FROM tblUser WHERE uid=?");
            db.uSelectSalt = db.mConnection.prepareStatement("SELECT salt FROM tblUser WHERE uid=?");
            db.uSelectAll = db.mConnection.prepareStatement("SELECT uid , username, realname, email FROM tblUser");
            db.uSelectOne = db.mConnection.prepareStatement("SELECT * from tblUser WHERE uid=?");
            db.uUpdateProfile = db.mConnection.prepareStatement("UPDATE tblUser SET profile = ? WHERE uid = ?");
            db.uUpdatePassword = db.mConnection
                    .prepareStatement("UPDATE tblUser SET password = ?, salt = ? WHERE uid = ?");
            db.uGetuId = db.mConnection.prepareStatement("SELECT uid from tblUser WHERE username=?");

            // Standard CRUD operations for comment_table
            db.cDeleteOne = db.mConnection.prepareStatement("DELETE FROM tblComment WHERE cid = ?");
            db.cInsertOne = db.mConnection.prepareStatement("INSERT INTO tblComment VALUES (default, ?, ?, ?)");
            db.cSelectAll = db.mConnection.prepareStatement("SELECT cid , uid, id, comment FROM tblComment");
            db.cSelectOne = db.mConnection.prepareStatement("SELECT * from tblComment WHERE cid=?");
            db.cSelectMessage = db.mConnection.prepareStatement("SELECT * from tblComment WHERE id=?");
            // db.cUpdateOne = db.mConnection.prepareStatement("UPDATE tblComment SET
            // comment = ? WHERE cid = ?");

            // Standard CRUD operations for session_table
            db.sDeleteOne = db.mConnection.prepareStatement("DELETE FROM tblSession WHERE uid = ?");
            db.sInsertOne = db.mConnection.prepareStatement("INSERT INTO tblSession VALUES (default, ?)");
            db.sSelectAll = db.mConnection.prepareStatement("SELECT key , uid FROM tblSession");
            db.sSelectOne = db.mConnection.prepareStatement("SELECT * from tblSession WHERE uid=?");
            db.sGetKey = db.mConnection.prepareStatement("SELECT key FROM tblSession WHERE uid = ?");

            // Standard CRUD operations for Vote table
            db.vDeleteOne = db.mConnection.prepareStatement("DELETE FROM tblVote WHERE uid = ?");
            db.vInsertOne = db.mConnection.prepareStatement("INSERT INTO tblVote VALUES (?, ?, ?)");
            db.vSelectAll = db.mConnection.prepareStatement("SELECT uid , id, vote, FROM tblVote");
            db.vSelectOne = db.mConnection.prepareStatement("SELECT * from tblVote WHERE uid=?, id=?");
            db.vUpdateOne = db.mConnection.prepareStatement("UPDATE tblVote SET vote = ? WHERE uid = ?, id = ?");
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

    ////////////////////// START message opertion //////////////////////
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
            mInsertOne.setInt(2, uid);
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
    ArrayList<message_RowData> select_messageAll() {
        ArrayList<message_RowData> res = new ArrayList<message_RowData>();
        try {
            ResultSet rs = mSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new message_RowData(rs.getInt("id"), rs.getString("message"), rs.getInt("likeCount"),
                        rs.getInt("dislikeCount"), rs.getInt("uid")));
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
    message_RowData select_messageOne(int id) {
        message_RowData res = null;
        try {
            mSelectOne.setInt(1, id);
            ResultSet rs = mSelectOne.executeQuery();
            if (rs.next()) {
                res = new message_RowData(rs.getInt("id"), rs.getString("message"), rs.getInt("likeCount"),
                        rs.getInt("dislikeCount"), rs.getInt("uid"));
            }
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
    int addLike(int id, int num) {
        int res = -1;
        message_RowData data = select_messageOne(id);
        int newCount = data.mlikeCount + num;
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
    int addDislike(int id, int num) {
        int res = -1;
        message_RowData data = select_messageOne(id);
        int newCount = data.mdislikeCount + num;
        try {
            mAddDislike.setInt(1, newCount);
            mAddDislike.setInt(2, id);
            res = mAddDislike.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
    ////////////////////// END message opertion //////////////////////

    ////////////////////// START user opertion //////////////////////
    /**
     * Create tblUser. If it already exists, this will print an error
     */
    void create_userTable() {
        try {
            uCreateTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove tblUser from the database. If it does not exist, this will print an
     * error.
     */
    void drop_userTable() {
        try {
            uDropTable.execute();
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
        try {
            byte[] salt = getSalt();
            uInsertOne.setString(1, username);
            uInsertOne.setString(2, realname);
            uInsertOne.setString(3, " ");
            uInsertOne.setString(4, email);
            uInsertOne.setString(5, salt.toString());
            String Hash_password = get_SecurePassword(password, salt);
            uInsertOne.setString(6, Hash_password);
            count += uInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return count;
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException {
        // Always use a SecureRandom generator
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        // Create array for salt
        byte[] salt = new byte[16];
        // Get a random salt
        sr.nextBytes(salt);
        // return salt
        return salt;
    }

    private static String get_SecurePassword(String passwordToHash, byte[] salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    /**
     * Delete a row by ID
     * 
     * @param uid The id of the row to delete
     * 
     * @return The number of rows that were deleted. -1 indicates an error.
     */
    int delete_userRow(int uid) {
        int res = -1;
        try {
            uDeleteOne.setInt(1, uid);
            res = uDeleteOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Get all data for a specific row, by ID
     * 
     * @param username
     * 
     * @return The data for the requested row, or null if the ID was invalid
     */
    int get_userId(String username) {
        int res = -1;
        try {
            uGetuId.setString(1, username);
            ResultSet rs = uGetuId.executeQuery();
            res = rs.getInt("uid");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Update the profile for a row in the database
     * 
     * @param uid     The uid of the row to update
     * @param profile The new profile contents
     * 
     * @return The number of rows that were updated. -1 indicates an error.
     */
    int update_userProfile(int uid, String profile) {
        int res = -1;
        try {
            uUpdateProfile.setString(1, profile);
            uUpdateProfile.setInt(2, uid);
            res = uUpdateProfile.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Update the profile for a row in the database
     * 
     * @param uid      The uid of the row to update
     * @param password The new password
     * 
     * @return The number of rows that were updated. -1 indicates an error.
     */
    int update_userPassword(int uid, String password) {
        int res = -1;
        try {
            byte[] salt = getSalt();
            String Hash_password = get_SecurePassword(password, salt);
            uUpdatePassword.setString(1, Hash_password);
            uUpdatePassword.setString(2, salt.toString());
            uUpdatePassword.setInt(3, uid);
            res = uUpdatePassword.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * @uid
     * 
     * @return password
     */
    String get_Password(int uid) {
        String password = null;
        try {
            uSelectPassword.setInt(1, uid);
            ResultSet rs = uSelectPassword.executeQuery();
            password = rs.getString("password");
            return password;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @uid
     * 
     * @return password
     */
    String generate_Password(int uid, String password) {
        try {
            uSelectSalt.setInt(1, uid);
            ResultSet rs = uSelectSalt.executeQuery();
            String salt = rs.getString("salt");
            return get_SecurePassword(password, salt.getBytes());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Query the database for a list of all subjects and their IDs
     * 
     * @return All rows, as an ArrayList
     */
    ArrayList<user_RowData> select_userAll() {
        ArrayList<user_RowData> res = new ArrayList<user_RowData>();
        try {
            ResultSet rs = uSelectAll.executeQuery();
            while (rs.next()) {
                // need edit
                res.add(new user_RowData(rs.getInt("uid"), rs.getString("username"), rs.getString("realname"),
                        rs.getString("profile"), rs.getString("email"), rs.getString("password"),
                        rs.getString("salt")));
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
     * @param uid The id of the row being requested
     * 
     * @return The data for the requested row, or null if the ID was invalid
     */
    user_RowData select_userOne(int uid) {
        user_RowData res = null;
        try {
            uSelectOne.setInt(1, uid);
            ResultSet rs = uSelectOne.executeQuery();
            if (rs.next()) {
                res = new user_RowData(rs.getInt("uid"), rs.getString("username"), rs.getString("realname"),
                        rs.getString("profile"), rs.getString("email"), rs.getString("password"), rs.getString("salt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    ////////////////////// END user opertion //////////////////////

    ////////////////////// START comment opertion //////////////////////
    /**
     * Create tblComment. If it already exists, this will print an error
     */
    void create_commentTable() {
        try {
            cCreateTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove tblComment from the database. If it does not exist, this will print an
     * error.
     */
    void drop_commentTable() {
        try {
            cDropTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert a row into the database
     * 
     * @param comment The message body for this new row
     * 
     * @return The number of rows that were inserted
     */
    int insert_commentRow(String comment, int uid, int id) {
        int count = 0;
        try {
            cInsertOne.setInt(1, uid);
            cInsertOne.setInt(2, id);
            cInsertOne.setString(3, comment);
            count += cInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Delete a row by cid
     * 
     * @param cid The id of the row to delete
     * 
     * @return The number of rows that were deleted. -1 indicates an error.
     */
    int delete_commentRow(int cid) {
        int res = -1;
        try {
            cDeleteOne.setInt(1, cid);
            res = cDeleteOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Query the database for a list of all subjects and their IDs
     * 
     * @return All rows, as an ArrayList
     */
    ArrayList<comment_RowData> select_commentAll() {
        ArrayList<comment_RowData> res = new ArrayList<comment_RowData>();
        try {
            ResultSet rs = cSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new comment_RowData(rs.getInt("cid"), rs.getInt("uid"), rs.getInt("id"),
                        rs.getString("comment")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Query the database for a list of all subjects and their IDs
     * 
     * @return All rows, as an ArrayList
     */
    ArrayList<comment_RowData> select_messageComment(int id) {
        ArrayList<comment_RowData> res = new ArrayList<comment_RowData>();
        try {
            cSelectMessage.setInt(1, id);
            ResultSet rs = cSelectMessage.executeQuery();
            while (rs.next()) {
                res.add(new comment_RowData(rs.getInt("cid"), rs.getInt("uid"), rs.getInt("id"),
                        rs.getString("comment")));
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
     * @param cid The id of the row being requested
     * 
     * @return The data for the requested row, or null if the ID was invalid
     */
    comment_RowData select_commentOne(int cid) {
        comment_RowData res = null;
        try {
            cSelectOne.setInt(1, cid);
            ResultSet rs = cSelectOne.executeQuery();
            if (rs.next()) {
                res = new comment_RowData(rs.getInt("cid"), rs.getInt("uid"), rs.getInt("id"), rs.getString("comment"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    ////////////////////// END comment opertion //////////////////////

    ////////////////////// START session opertion //////////////////////
    /**
     * Create tblSession. If it already exists, this will print an error
     */
    void create_sessionTable() {
        try {
            sCreateTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove tblSession from the database. If it does not exist, this will print an
     * error.
     */
    void drop_sessionTable() {
        try {
            sDropTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert a row into the database
     * 
     * @param session The message body for this new row
     * 
     * @return The number of rows that were inserted
     */
    int insert_sessionRow(int uid) {
        int count = 0;
        try {
            sInsertOne.setInt(1, uid);
            count += sInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    int get_sessionKey(int uid) {
        int key = 0;
        try {
            sGetKey.setInt(1, uid);
            ResultSet rs = sGetKey.executeQuery();
            key = rs.getInt("key");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return key;
    }

    /**
     * Delete a row by cid
     * 
     * @param uid The id of the row to delete
     * 
     * @return The number of rows that were deleted. -1 indicates an error.
     */
    int delete_sessionRow(int uid) {
        int res = -1;
        try {
            sDeleteOne.setInt(1, uid);
            res = sDeleteOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Query the database for a list of all subjects and their IDs
     * 
     * @return All rows, as an ArrayList
     */
    ArrayList<session_RowData> select_sessionAll() {
        ArrayList<session_RowData> res = new ArrayList<session_RowData>();
        try {
            ResultSet rs = sSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new session_RowData(rs.getInt("key"), rs.getInt("uid")));
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
     * @param uid The id of the row being requested
     * 
     * @return The data for the requested row, or null if the ID was invalid
     */
    session_RowData select_sessionOne(int uid) {
        session_RowData res = null;
        try {
            sSelectOne.setInt(1, uid);
            ResultSet rs = sSelectOne.executeQuery();
            if (rs.next()) {
                res = new session_RowData(rs.getInt("key"), rs.getInt("uid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    ////////////////////// EMD session opertion //////////////////////

    ////////////////////// START vote opertion //////////////////////

    /**
     * Insert a row into the database
     * 
     * @param vote The message body for this new row
     * 
     * @return The number of rows that were inserted
     */
    int insert_voteRow(int vote, int uid, int id) {
        int count = 0;
        try {
            vInsertOne.setInt(1, uid);
            vInsertOne.setInt(2, id);
            vInsertOne.setInt(3, vote);
            count += vInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Delete a row by cid
     * 
     * @param uid The id of the row to delete
     * 
     * @return The number of rows that were deleted. -1 indicates an error.
     */
    int delete_voteRow(int uid, int id) {
        int res = -1;
        try {
            vDeleteOne.setInt(1, uid);
            vDeleteOne.setInt(2, id);
            res = vDeleteOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Query the database for a list of all subjects and their IDs
     * 
     * @return All rows, as an ArrayList
     */
    ArrayList<vote_RowData> select_voteAll() {
        ArrayList<vote_RowData> res = new ArrayList<vote_RowData>();
        try {
            ResultSet rs = vSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new vote_RowData(rs.getInt("uid"), rs.getInt("id"), rs.getInt("vote")));
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
     * @param uid The id of the row being requested
     * 
     * @return The data for the requested row, or null if the ID was invalid
     */
    vote_RowData select_voteOne(int uid, int id) {
        vote_RowData res = null;
        try {
            vSelectOne.setInt(1, uid);
            vSelectOne.setInt(2, id);
            ResultSet rs = vSelectOne.executeQuery();
            if (rs.next()) {
                res = new vote_RowData(rs.getInt("uid"), rs.getInt("id"), rs.getInt("vote"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Get all data for a specific row, by ID
     * 
     * @param uid The id of the row being requested
     * 
     * @return The data for the requested row, or null if the ID was invalid
     */
    int update_voteOne(int vote, int uid, int id) {
        int res = -1;
        try {
            vUpdateOne.setInt(1, vote);
            vUpdateOne.setInt(2, uid);
            vUpdateOne.setInt(3, id);
            res = vUpdateOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Create tblVote. If it already exists, this will print an error
     */
    void create_voteTable() {
        try {
            vCreateTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove tblVote from the database. If it does not exist, this will print an
     * error.
     */
    void drop_voteTable() {
        try {
            vDropTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}