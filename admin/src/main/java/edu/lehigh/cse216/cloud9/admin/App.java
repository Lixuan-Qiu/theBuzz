package edu.lehigh.cse216.cloud9.admin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

/**
 * App is our basic admin app. For now, it is a demonstration of the six key
 * operations on a database: connect, insert, update, query, delete, disconnect
 */
public class App {

    /**
     * Print the menu for our program
     */
    static void menu() {
        System.out.println("Main Menu");
        System.out.println("  [M] Access Message Table");
        System.out.println("  [U] Access User Table");
        System.out.println("  [C] Access Comment Table");
        System.out.println("  [S] Access Session Key Table");
        System.out.println("  [V] Acces Vote Table");
        System.out.println("  [q] Quit Program");
    }

    static void mMenu() {
        System.out.println("Message Menu");
        System.out.println("  [T] Create tblData");
        System.out.println("  [D] Drop tblData");
        System.out.println("  [1] Query for a specific Message");
        System.out.println("  [*] Query for all Messages");
        System.out.println("  [-] Delete a Message");
        System.out.println("  [+] Insert a new Message");
        System.out.println("  [~] Update a Message");
        System.out.println("  [^] Add Like");
        System.out.println("  [v] Add Dislike");
        System.out.println("  [q] Quit Program");
        System.out.println("  [?] Help (this message)");
    }

    static void uMenu() {
        System.out.println("User Menu");
        System.out.println("  [T] Create tblData");
        System.out.println("  [D] Drop tblData");
        System.out.println("  [1] Query for a specific User");
        System.out.println("  [*] Query for all Users");
        System.out.println("  [-] Delete a User");
        System.out.println("  [+] Insert a new User");
        System.out.println("  [~] Update a Profile");
        System.out.println("  [p] Query for a Password");
        System.out.println("  [r] Update  a Password");
        System.out.println("  [q] Quit Program");
        System.out.println("  [?] Help (this message)");
    }

    static void cMenu() {
        System.out.println("Comment Menu");
        System.out.println("  [T] Create tblData");
        System.out.println("  [D] Drop tblData");
        System.out.println("  [1] Query for a specific Comment");
        System.out.println("  [*] Query for all Comments");
        System.out.println("  [-] Delete a Comment");
        System.out.println("  [+] Insert a new Comment");
        System.out.println("  [s] Query all Comments for a Message");
        System.out.println("  [q] Quit Program");
        System.out.println("  [?] Help (this message)");
    }

    static void sMenu() {
        System.out.println("Session Menu");
        System.out.println("  [T] Create tblData");
        System.out.println("  [D] Drop tblData");
        System.out.println("  [1] Query for a specific users session");
        System.out.println("  [*] Query for all sessions");
        System.out.println("  [-] Delete a session");
        System.out.println("  [+] Insert a new row");
        System.out.println("  [q] Quit Program");
        System.out.println("  [?] Help (this message)");
    }

    static void vMenu() {
        System.out.println("Vote Menu");
        System.out.println("  [T] Create tblData");
        System.out.println("  [D] Drop tblData");
        System.out.println("  [1] Query for a specific row");
        System.out.println("  [*] Query for all rows");
        System.out.println("  [-] Delete a row");
        System.out.println("  [+] Insert a new row");
        System.out.println("  [~] Update a row");
        System.out.println("  [&] Add Like to a row");
        System.out.println("  [q] Quit Program");
        System.out.println("  [?] Help (this message)");
    }
    /**
     * Ask the user to enter a menu option; repeat until we get a valid option
     * 
     * @param in A BufferedReader, for reading from the keyboard
     * 
     * @return The character corresponding to the chosen menu option
     */
<<<<<<< HEAD:admin/src/main/java/edu/lehigh/cse216/sil320/admin/App.java
    static char prompt(BufferedReader in, String actions) {
        // We repeat until a valid single-character option is selected
=======
    static char prompt(BufferedReader in) {
        // The valid actions:
        String actions = "TD1*-+~q?&";

        // We repeat until a valid single-character option is selected        
>>>>>>> 3453b16da530ce7cb743fe583eb1f20d77b58b34:admin/src/main/java/edu/lehigh/cse216/cloud9/admin/App.java
        while (true) {
            System.out.print("[" + actions + "] :> ");
            String action;
            try {
                action = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            if (action.length() != 1)
                continue;
            if (actions.contains(action)) {
                return action.charAt(0);
            }
            System.out.println("Invalid Command");
        }
    }

    /**
     * Ask the user to enter a String message
     * 
     * @param in      A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * 
     * @return The string that the user provided. May be "".
     */
    static String getString(BufferedReader in, String message) {
        String s;
        try {
            System.out.print(message + " :> ");
            s = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return s;
    }

    /**
     * Ask the user to enter an integer
     * 
     * @param in      A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * 
     * @return The integer that the user provided. On error, it will be -1
     */
    static int getInt(BufferedReader in, String message) {
        int i = -1;
        try {
            System.out.print(message + " :> ");
            i = Integer.parseInt(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return i;
    }

<<<<<<< HEAD:admin/src/main/java/edu/lehigh/cse216/sil320/admin/App.java
    static void messageMenu(Database db, BufferedReader in){
        mMenu();
        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            // function call
            char action = prompt(in,"TD1*-+~q^v?" );
            if (action == '?') {
                mMenu();
            } else if (action == 'q') {
                break;
            } else if (action == 'T') {
                db.create_messageTable();
            } else if (action == 'D') {
                db.drop_messageTable();
            } else if (action == '1') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                Database.message_RowData res = db.select_messageOne(id);
                if (res != null) {
                    System.out.println("User Id: " + res.uId + "Message Id " + res.mId);
                    System.out.println("  --> " + res.mMessage + "Vote Count: " + (res.mlikeCount - res.mdislikeCount));
                }
            } else if (action == '*') {
                ArrayList<Database.message_RowData> res = db.select_messageAll();
                if (res == null)
                    continue;
                System.out.println("  Current Message Database Contents");
                System.out.println("  -------------------------");
                for (Database.message_RowData rd : res) {
                    System.out.println("User Id: " + rd.uId + "Message Id " + rd.mId);
                    System.out.println("  --> " + rd.mMessage + "Vote Count: " + (rd.mlikeCount - rd.mdislikeCount));
                }
            } else if (action == '-') {
                int id = getInt(in, "Enter the Message ID:");
                if (id == -1)
                    continue;
                int res = db.delete_messageRow(id);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows deleted");
            } else if (action == '+') {
                String message = getString(in, "Enter the message");
                int id = getInt(in, "Eneter the User ID");
                if (id == -1 || message.equals(""))
                    continue;
                int res = db.insert_messageRow(message, id);
                System.out.println(res + " rows added");
            } else if (action == '~') {
                int id = getInt(in, "Enter the Message ID: ");
                if (id == -1)
                    continue;
                String newMessage = getString(in, "Enter the new message");
                int res = db.update_messageOne(id, newMessage);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows updated");
            } else if (action == '^') {
                int id = getInt(in, "Enter the Message ID: ");
                if (id == -1)
                    continue;
                int res = db.addLike(id, 1);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows like added");
            } else if (action == '-') {
                int id = getInt(in, "Enter the Message ID: ");
                if (id == -1)
                    continue;
                int res = db.addDislike(id, -1);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows dislike added");
            }
        }
    }

    static void userMenu(Database db, BufferedReader in){
        uMenu();
=======
    /**
     * The main routine runs a loop that gets a request from the user and
     * processes it
     * 
     * @param argv Command-line options.  Ignored by this program.
     */
    public static void main(String[] argv) {
        // get the Postgres configuration from the environment
        Map<String, String> env = System.getenv();
        String db_url = env.get("DATABASE_URL");
        // Get a fully-configured connection to the database, or exit 
        // immediately
        Database db = Database.getDatabase(db_url);
        if (db == null)
            return;
        menu();
        // Start our basic command-line interpreter:
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
>>>>>>> 3453b16da530ce7cb743fe583eb1f20d77b58b34:admin/src/main/java/edu/lehigh/cse216/cloud9/admin/App.java
        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            // function call
            char action = prompt(in, "TD1*-+~prq?");
            if (action == '?') {
                uMenu();
            } else if (action == 'q') {
                break;
            } else if (action == 'T') {
                db.create_userTable();
            } else if (action == 'D') {
                db.drop_userTable();
            } else if (action == '1') {
                int id = getInt(in, "Enter the User ID: ");
                if (id == -1)
                    continue;
                Database.user_RowData res = db.select_userOne(id);
                if (res != null) {
                    System.out.println("User Id: " + res.uId + ", Real Name: " + res.uRealname);
                    System.out.println("Username: " + res.uUsername + ", Email: " + res.uEmail);
                    System.out.println("Bio: " + res.uProfile);
                }
            } else if (action == '*') {
                ArrayList<Database.user_RowData> res = db.select_userAll();
                if (res == null)
                    continue;
                System.out.println("  Current User Database Contents");
                System.out.println("  -------------------------");
                for (Database.user_RowData rd : res) {
                    System.out.println("User Id: " + rd.uId + ", Real Name: " + rd.uRealname);
                    System.out.println("Username: " + rd.uUsername + ", Email: " + rd.uEmail);
                    System.out.println("Bio: " + rd.uProfile);
                }
            } else if (action == '-') {
                int id = getInt(in, "Enter the User ID: ");
                if (id == -1)
                    continue;
                int res = db.delete_userRow(id);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows deleted");
            } /*else if (action == '+') {
                String username = getString(in, "Enter the username: ");
                String realname = getString(in, "Enter the real name: ");
                String email = getString(in, "Enter the email: ");
                String password = getString(in, "Enter the password: ");
                if (username.equals("") || realname.equals("") || email.equals("") || password.equals(""))
                    continue;
                int res = db.insert_userRow(username, realname, email, password);
                System.out.println(res + " rows added");
            } */else if (action == '~') {
                int id = getInt(in, "Enter the user ID: ");
                if (id == -1)
                    continue;
                String newProfile = getString(in, "Enter the new Profile");
                int res = db.update_userProfile(id, newProfile);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows updated");
            } else if (action == 'p') {
                int id = getInt(in, "Enter the User ID: ");
                if (id == -1)
                    continue;
                String res = db.get_Password(id);
                if (res.equals(""))
                    continue;
                System.out.println("User ID: "+ id +" Password: "+ res);
            } else if (action == 'r') {
                int id = getInt(in, "Enter the User ID: ");
                if (id == -1)
                    continue;
                String newPassword = getString(in, "Enter the new Password");
                int res = db.update_userPassword(id, newPassword);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows updated");
            }
        } 
    }

    static void commentMenu(Database db, BufferedReader in){
        mMenu();
        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            // function call
            char action = prompt(in, "TD1*-+~q?");
            if (action == '?') {
                menu();
            } else if (action == 'q') {
                break;
            } else if (action == 'T') {
                db.create_commentTable();
            } else if (action == 'D') {
                db.drop_commentTable();
            } else if (action == '1') {
                int id = getInt(in, "Enter the Comment ID: ");
                if (id == -1)
                    continue;
                Database.comment_RowData res = db.select_commentOne(id);
                if (res != null) {
                    System.out.println("User Id: " + res.uId + ", Message Id: " + res.mId + ", Comment Id: " + res.cId) ;
                    System.out.println("  --> " + res.cComment);
                }
            } else if (action == '*') {
                ArrayList<Database.comment_RowData> res = db.select_commentAll();
                if (res == null)
                    continue;
                System.out.println("  Current Comment Database Contents");
                System.out.println("  -------------------------");
                for (Database.comment_RowData rd : res) {
                    System.out.println("User Id: " + rd.uId + ", Message Id: " + rd.mId + ", Comment Id: " + rd.cId) ;
                    System.out.println("  --> " + rd.cComment);
                }
            } else if (action == 'm') {
                int id = getInt(in, "Enter the Message ID: ");
                if (id == -1)
                    continue;
                ArrayList<Database.comment_RowData> res = db.select_messageComment(id);
                if (res == null)
                    continue;
                System.out.println("  Current Comment Database For Message " +id);
                System.out.println("  -------------------------");
                for (Database.comment_RowData rd : res) {
                    System.out.println("User Id: " + rd.uId + ", Comment Id: " + rd.cId) ;
                    System.out.println("  --> " + rd.cComment);
                }
            } else if (action == '-') {
                int id = getInt(in, "Enter the comment ID: ");
                if (id == -1)
                    continue;
                int res = db.delete_commentRow(id);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows deleted");
            } else if (action == '+') {
                int uId = getInt(in, "Enter the User Id: ");
                int mId = getInt(in, "Enter the Message Id: ");
                String comment = getString(in, "Enter the Comment: ");
                if (comment.equals("") || uId == -1 || mId == -1)
                    continue;
                int res = db.insert_commentRow(comment, uId, mId);
                System.out.println(res + " rows added");
            } /*else if (action == '~') {
                int id = getInt(in, "Enter the comment ID: ");
                if (id == -1)
                    continue;
                String newMessage = getString(in, "Enter the new message");
                int res = db.mUpdateOne(id, newMessage);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows updated");
            } */
        }
    }

    static void sessionMenu(Database db, BufferedReader in){
       sMenu();
        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            // function call
            char action = prompt(in, "TD1*-+~q?");
            if (action == '?') {
                sMenu();
            } else if (action == 'q') {
                break;
            } else if (action == 'T') {
                db.create_sessionTable();
            } else if (action == 'D') {
                db.drop_sessionTable();
            } /**else if (action == '1') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                Database.vote_RowData res = db.mSelectOne(id);
                if (res != null) {
                    System.out.println("  [" + res.mId + "] " + res.mMessage + " Like: " + res.mlikeCount);
                }
            } else if (action == '*') {
                ArrayList<Database.vote_RowData> res = db.mSelectAll();
                if (res == null)
                    continue;
                System.out.println("  Current Database Contents");
                System.out.println("  -------------------------");
<<<<<<< HEAD:admin/src/main/java/edu/lehigh/cse216/sil320/admin/App.java
                for (Database.vote_RowData rd : res) {
                    System.out.println("  [" + rd.mId + "] " + rd.mSubject);
=======
                for (Database.RowData rd : res) {
                    System.out.println("  [" + rd.mId + "] " + rd.mMessage + " Like: " + rd.mlikeCount);
>>>>>>> 3453b16da530ce7cb743fe583eb1f20d77b58b34:admin/src/main/java/edu/lehigh/cse216/cloud9/admin/App.java
                }
            } else if (action == '-') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                int res = db.mDeleteOne(id);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows deleted");
            } else if (action == '+') {
                String message = getString(in, "Enter the message");
                if (message.equals(""))
                    continue;
<<<<<<< HEAD:admin/src/main/java/edu/lehigh/cse216/sil320/admin/App.java
                int res = db.mInsertOne(subject, message);
=======
                int res = db.insertRow(message);
>>>>>>> 3453b16da530ce7cb743fe583eb1f20d77b58b34:admin/src/main/java/edu/lehigh/cse216/cloud9/admin/App.java
                System.out.println(res + " rows added");
            } else if (action == '~') {
                int id = getInt(in, "Enter the row ID :> ");
                if (id == -1)
                    continue;
                String newMessage = getString(in, "Enter the new message");
                int res = db.mUpdateOne(id, newMessage);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows updated");
<<<<<<< HEAD:admin/src/main/java/edu/lehigh/cse216/sil320/admin/App.java
            }*/
        }
    }

    static void voteMenu(Database db, BufferedReader in){
        vMenu();
        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            // function call
            char action = prompt(in, "TD1*-+~q?");
            if (action == '?') {
                vMenu();
            } else if (action == 'q') {
                break;
            } else if (action == 'T') {
                db.create_voteTable();
            } else if (action == 'D') {
                db.drop_voteTable();
            } /*else if (action == '1') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                Database.vote_RowData res = db.mSelectOne(id);
                if (res != null) {
                    System.out.println("  [" + res.mId + "] " + res.mSubject);
                    System.out.println("  --> " + res.mMessage);
                }
            } else if (action == '*') {
                ArrayList<Database.vote_RowData> res = db.mSelectAll();
                if (res == null)
                    continue;
                System.out.println("  Current Vote Database Contents");
                System.out.println("  -------------------------");
                for (Database.vote_RowData rd : res) {
                    System.out.println("  [" + rd.mId + "] " + rd.mSubject);
                }
            } else if (action == '-') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                int res = db.mDeleteOne(id);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows deleted");
            } else if (action == '+') {
                int uId = getInt(in, "Enter the User Id: ");
                int mId = getInt(in, "Enter the Message Id: ");
                if (subject.equals("") || message.equals(""))
                    continue;
                int res = db.mInsertOne(subject, message);
                System.out.println(res + " rows added");
            } else if (action == '~') {
                int id = getInt(in, "Enter the row ID :> ");
                if (id == -1)
                    continue;
                String newMessage = getString(in, "Enter the new message");
                int res = db.mUpdateOne(id, newMessage);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows updated");
            }*/
        }
    } 

    /**
     * The main routine runs a loop that gets a request from the user and processes
     * it
     * 
     * @param argv Command-line options. Ignored by this program.
     */
    public static void main(String[] argv) {
        // get the Postgres configuration from the environment
        Map<String, String> env = System.getenv();
        String db_url = env.get("DATABASE_URL");
        // Get a fully-configured connection to the database, or exit
        // immediately
        Database db = Database.getDatabase(db_url);
        if (db == null)
            return;

        menu();
        // Start our basic command-line interpreter:
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        //menu();
        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            // function call
            char action = prompt(in, "MUCSVq?");
            if (action == 'q') {
                break;
            } else if (action == 'M') {
                messageMenu(db, in);
            } else if (action == 'U') {
                userMenu(db, in);
            } else if (action == 'C') {
                commentMenu(db, in);
            } else if (action == 'V') {
                voteMenu(db, in);
            } else if (action == 'S') {
                sessionMenu(db, in);
=======
            } else if (action == '&'){
                int id = getInt(in, "Enter the row ID :> ");
                if (id == -1)
                    continue;
                int res = db.addLike(id);;
                System.out.println(res + " Like added");
>>>>>>> 3453b16da530ce7cb743fe583eb1f20d77b58b34:admin/src/main/java/edu/lehigh/cse216/cloud9/admin/App.java
            }
            menu();
        }
        // Always remember to disconnect from the database when the program
        // exits
        db.disconnect();
    }
}