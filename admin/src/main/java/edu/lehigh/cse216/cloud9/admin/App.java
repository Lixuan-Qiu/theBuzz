
package edu.lehigh.cse216.cloud9.admin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Map;
import java.security.SecureRandom;
import java.util.Random;

import com.sendgrid.*;

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
        System.out.println("  [D] Drop All Tables");
        System.out.println("  [q] Quit Program");
    }

    /**
     * Print the menu for the Message table
     */
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

    /**
     * Print the menu for the User table
     */
    static void uMenu() {
        System.out.println("User Menu");
        System.out.println("  [T] Create tblData");
        System.out.println("  [D] Drop tblData");
        System.out.println("  [1] Query for a specific User");
        System.out.println("  [*] Query for all Users");
        System.out.println("  [-] Delete a User");
        System.out.println("  [+] Insert a new User");
        System.out.println("  [~] Update a Profile");
        System.out.println("  [i] Query for User ID by Username");
        System.out.println("  [q] Quit Program");
        System.out.println("  [?] Help (this message)");
    }

    /**
     * Print the menu for the Comment table
     */
    static void cMenu() {
        System.out.println("Comment Menu");
        System.out.println("  [T] Create tblData");
        System.out.println("  [D] Drop tblData");
        System.out.println("  [1] Query for a specific Comment");
        System.out.println("  [*] Query for all Comments");
        System.out.println("  [m] Query for all Comments for a specific Message");
        System.out.println("  [-] Delete a Comment");
        System.out.println("  [+] Insert a new Comment");
        System.out.println("  [q] Quit Program");
        System.out.println("  [?] Help (this message)");
    }

    /**
     * Print the menu for the Session table
     */
    static void sMenu() {
        System.out.println("Session Menu");
        System.out.println("  [T] Create tblData");
        System.out.println("  [D] Drop tblData");
        System.out.println("  [1] Query for a specific user's session");
        System.out.println("  [k] query for a specific session");
        System.out.println("  [*] Query for all sessions");
        System.out.println("  [-] Delete a session");
        System.out.println("  [+] Insert a new row");
        System.out.println("  [q] Quit Program");
        System.out.println("  [?] Help (this message)");
    }

    /**
     * Print the menu for the Vote table
     */
    static void vMenu() {
        System.out.println("Vote Menu");
        System.out.println("  [T] Create tblData");
        System.out.println("  [D] Drop tblData");
        System.out.println("  [1] Query for a specific row");
        System.out.println("  [*] Query for all rows");
        System.out.println("  [-] Delete a row");
        System.out.println("  [+] Insert a new row");
        System.out.println("  [~] Update a row");
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
    static char prompt(BufferedReader in, String actions) {
        // We repeat until a valid single-character option is selected
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

    static void messageMenu(Database db, BufferedReader in) {
        mMenu();
        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            // function call
            char action = prompt(in, "TD1*-+~q^v?");
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
                    System.out.println("User Id: " + res.uId + ", Message Id: " + res.mId);
                    System.out
                            .println("  --> " + res.mMessage + "  Vote Count: " + (res.mlikeCount - res.mdislikeCount));
                }
            } else if (action == '*') {
                ArrayList<Database.message_RowData> res = db.select_messageAll();
                if (res == null)
                    continue;
                System.out.println("  Current Message Database Contents");
                System.out.println("  -------------------------");
                for (Database.message_RowData rd : res) {
                    System.out.println("User Id: " + rd.uId + ", Message Id: " + rd.mId);
                    System.out.println("  --> " + rd.mMessage + "   Vote Count: " + (rd.mlikeCount - rd.mdislikeCount));
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
            } else if (action == 'v') {
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

    static void userMenu(Database db, BufferedReader in) {
        uMenu();
        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            // function call
            char action = prompt(in, "TD1*-+~irq?");
            if (action == '?') {
                uMenu();
            } else if (action == 'q') {
                break;
            } else if (action == 'T') {
                db.create_userTable();
            } else if (action == 'D') {
                db.drop_userTable();
            } else if (action == '1') {
                int uId = getInt(in, "Enter the User ID: ");
                if (uId == -1)
                    continue;
                Database.user_RowData res = getOneUser(db, uId);
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
            } else if (action == '+') {
                String username = getString(in, "Enter the username: ");
                String realname = getString(in, "Enter the real name: ");
                String email = getString(in, "Enter the email: ");
                if (username.equals("") || realname.equals("") || email.equals(""))
                    continue;
                int res = addUser(db, username, realname, email);
                System.out.println(res + " rows added");
                if (res > 0)
                    try {
                        sendEmail(email, realname, username);
                    } catch (IOException e) {
                        System.err.println("Caught IOException: " + e.getMessage());
                    }
            } else if (action == '~') {
                int id = getInt(in, "Enter the user ID: ");
                if (id == -1)
                    continue;
                String newProfile = getString(in, "Enter the new Profile");
                int res = db.update_userProfile(id, newProfile);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows updated");
            } else if (action == 'i') {
                String username = getString(in, "Enter the Username: ");
                if (username.equals(""))
                    continue;
                int res = db.get_userId(username);
                if (res == -1) {
                    System.out.println("Sorry we can't locate a user id by the user name you inputed");
                    continue;
                }
                System.out.println("User ID: " + res + " Username: " + username);
            }
        }
    }

    /**
     * Runs the insert_userRow method to insert a new row
     * 
     * @param db       the current database
     * @param username the new user's username
     * @param realname the new user's real name
     * @param email    the new user's email
     * @return the number of rows added
     */
    static int addUser(Database db, String username, String realname, String email) {
        int res = db.insert_userRow(username, realname, email);
        return res;
    }

    /**
     * Gets one user's info from the database
     * 
     * @param db  the current database
     * @param uId the Id of the user who's information we are getting
     * @return the user's information in a user_RowData
     */
    static Database.user_RowData getOneUser(Database db, int uId) {
        Database.user_RowData res = db.select_userOne(uId);
        return res;
    }

    static void commentMenu(Database db, BufferedReader in) {
        cMenu();
        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            // function call
            char action = prompt(in, "TD1*m-+~q?");
            if (action == '?') {
                cMenu();
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
                    System.out.println("User Id: " + res.uId + ", Message Id: " + res.mId + ", Comment Id: " + res.cId);
                    System.out.println("  --> " + res.cComment);
                }
            } else if (action == '*') {
                ArrayList<Database.comment_RowData> res = db.select_commentAll();
                if (res == null)
                    continue;
                System.out.println("  Current Comment Database Contents");
                System.out.println("  -------------------------");
                for (Database.comment_RowData rd : res) {
                    System.out.println("User Id: " + rd.uId + ", Message Id: " + rd.mId + ", Comment Id: " + rd.cId);
                    System.out.println("  --> " + rd.cComment);
                }
            } else if (action == 'm') {
                int id = getInt(in, "Enter the Message ID: ");
                if (id == -1)
                    continue;
                ArrayList<Database.comment_RowData> res = db.select_messageComment(id);
                if (res == null)
                    continue;
                System.out.println("  Current Comment Database For Message " + id);
                System.out.println("  -------------------------");
                for (Database.comment_RowData rd : res) {
                    System.out.println("User Id: " + rd.uId + ", Comment Id: " + rd.cId);
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
            } /*
               * else if (action == '~') { int id = getInt(in, "Enter the comment ID: "); if
               * (id == -1) continue; String newMessage = getString(in,
               * "Enter the new message"); int res = db.mUpdateOne(id, newMessage); if (res ==
               * -1) continue; System.out.println("  " + res + " rows updated"); }
               */
        }
    }

    static void clearAll(Database db, BufferedReader in) {
        boolean safe = true;
        while (safe) {
            char action = prompt(in,
                    "Good afternoon, Mr. Hunt. Your mission, should you choose to accept it, involves the elimination of all current data. (y/n)");
            if (action == 'y') {
                System.out.println(
                        "As always, should you or any of your Force be caught or killed, the Secretary will disavow any knowledge of your actions. These data tables will self-destruct in one second. Good luck");
                db.drop_voteTable();
                db.drop_commentTable();
                db.drop_sessionTable();
                db.drop_messageTable();
                db.drop_userTable();
                safe = false;
            } else if (action == 'n') {
                break;
            }
        }
    }

    static void sessionMenu(Database db, BufferedReader in) {
        sMenu();
        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            // function call
            char action = prompt(in, "TD1*-+kq?");
            if (action == '?') {
                sMenu();
            } else if (action == 'q') {
                break;
            } else if (action == 'T') {
                db.create_sessionTable();
            } else if (action == 'D') {
                db.drop_sessionTable();
            } else if (action == '1') {
                int uId = getInt(in, "Enter the User ID");
                if (uId == -1)
                    continue;
                Database.session_RowData res = db.select_sessionOne(uId);
                if (res != null) {
                    System.out.println("  [" + res.uId + "] " + res.key);
                }
            } else if (action == '*') {
                ArrayList<Database.session_RowData> res = db.select_sessionAll();
                if (res == null)
                    continue;
                System.out.println("  Current Session Key Database Contents");
                System.out.println("  -------------------------");
                for (Database.session_RowData rd : res) {
                    System.out.println("  [" + rd.uId + "] " + rd.key);
                }
            } else if (action == '-') {
                int uId = getInt(in, "Enter the User Id: ");
                if (uId == -1)
                    continue;
                int res = db.delete_sessionRow(uId);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows deleted");
            } else if (action == '+') {
                int uId = getInt(in, "Enter the User Id: ");
                if (uId == -1)
                    continue;
                String key = getString(in, "Enter the new session key");
                int res = db.insert_sessionRow(uId, key);
                System.out.println(res + " rows added");
            } else if (action == 'k') {
                int uId = getInt(in, "Enter the User ID: ");
                if (uId == -1)
                    continue;
                int res = db.get_sessionKey(uId);
                if (res != -1) {
                    System.out.println("  [" + uId + "] " + res);
                }
            }
        }
    }

    static void voteMenu(Database db, BufferedReader in) {
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
            } else if (action == '1') {
                int uId = getInt(in, "Enter the User Id: ");
                int mId = getInt(in, "Enter the Message Id: ");
                if (uId == -1 || mId == -1)
                    continue;
                Database.vote_RowData res = db.select_voteOne(uId, mId);
                if (res != null) {
                    System.out.println("User Id: " + res.uId + ", Message Id: " + res.mId + ", Vote: " + res.vote);
                }
            } else if (action == '*') {
                ArrayList<Database.vote_RowData> res = db.select_voteAll();
                if (res == null)
                    continue;
                System.out.println("  Current Vote Database Contents");
                System.out.println("  -------------------------");
                for (Database.vote_RowData rd : res) {
                    System.out.println("User Id: " + rd.uId + ", Message Id: " + rd.mId + ", Vote: " + rd.vote);
                }
            } else if (action == '-') {
                int uId = getInt(in, "Enter the User Id: ");
                int mId = getInt(in, "Enter the Message Id: ");
                if (uId == -1 || mId == -1)
                    continue;
                int res = db.delete_voteRow(uId, mId);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows deleted");
            } else if (action == '+') {
                int uId = getInt(in, "Enter the User Id: ");
                int mId = getInt(in, "Enter the Message Id: ");
                int vote = getInt(in, "Enter the vote: ");
                if (uId == -1 || mId == -1 || vote == -1)
                    continue;
                int res = db.insert_voteRow(vote, uId, mId);
                System.out.println(res + " rows added");
            } else if (action == '~') {
                int uId = getInt(in, "Enter the User Id: ");
                int mId = getInt(in, "Enter the Message Id: ");
                if (uId == -1 || mId == -1)
                    continue;
                int newVote = getInt(in, "Enter the new vote: ");
                int res = db.update_voteOne(newVote, uId, mId);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows updated");
            }
        }
    }

    /**
     * Sends an email to the new user containing their password and username
     * 
     * @param toEmail  the address of where the email is going
     * @param realname the name of the new user
     * @param username the username of the new user
     */
    static void sendEmail(String toEmail, String realname, String username) throws IOException {
        Email from = new Email("cse216cloud9@gmail.com");
        String subject = "Welcome to The Buzz!";
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", "hi");
        String templateId = "d-fc5401079cc548d78cc952334ad91a6c";
        Mail mail = new Mail(from, subject, to, content);
        mail.setTemplateId(templateId);

        // Attaches the values to the tags. TECHNICAL DEBT:Figure out a more effective
        // and consistent way to do this
        mail.addCustomArg("realname", realname);
        mail.addCustomArg("username", username);
        Personalization obj = new Personalization();
        obj.addTo(to);
        obj.addDynamicTemplateData("realname", realname);
        obj.addDynamicTemplateData("username", username);
        mail.addPersonalization(obj);

        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            throw ex;
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
        // menu();
        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            // function call
            char action = prompt(in, "MUCSVDq?");
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
            } else if (action == 'D') {
                clearAll(db, in);
            }
            menu();
        }
        // Always remember to disconnect from the database when the program
        // exits
        db.disconnect();
    }
}