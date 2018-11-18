package edu.lehigh.cse216.cloud9.admin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi.Attribute;
import com.diogonunes.jcdp.color.api.Ansi.BColor;
import com.diogonunes.jcdp.color.api.Ansi.FColor;
import com.sendgrid.*;

// check https://github.com/dialex/JCDP/blob/master/README.md for information on colorprinter
public class App {

    static ColoredPrinter cp;

    /**
     * Print the menu for our program
     */
    static void print_main_menu() {

        print_header("Main Menu");
        cp.setForegroundColor(FColor.YELLOW);
        cp.setBackgroundColor(BColor.BLACK);

        cp.println("  [M] Access Message Table");
        cp.println("  [U] Access User Table");
        cp.println("  [C] Access Comment Table");
        cp.println("  [S] Access Session Key Table");
        cp.println("  [V] Acces Vote Table");
        cp.println("  [D] Drop All Tables");
        cp.println("  [q] Quit Program");

        cp.clear();
    }

    /**
     * Print the menu for the Message table
     */
    static void print_messageMenu() {

        print_header("Message Menu");
        cp.setForegroundColor(FColor.YELLOW);
        cp.setBackgroundColor(BColor.BLACK);

        cp.println("  [T] Create tblData");
        cp.println("  [D] Drop tblData");
        cp.println("  [1] Query for a specific Message");
        cp.println("  [*] Query for all Messages");
        cp.println("  [-] Delete a Message");
        cp.println("  [+] Insert a new Message");
        cp.println("  [~] Update a Message");
        cp.println("  [^] Add Like");
        cp.println("  [v] Add Dislike");
        cp.println("  [q] Quit Program");
        cp.println("  [?] Help (this message)");

        cp.clear();
    }

    /**
     * Print the menu for the User table
     */
    static void print_userMenu() {

        print_header("User Menu");
        cp.setForegroundColor(FColor.YELLOW);
        cp.setBackgroundColor(BColor.BLACK);

        cp.println("  [T] Create tblData");
        cp.println("  [D] Drop tblData");
        cp.println("  [1] Query for a specific User");
        cp.println("  [*] Query for all Users");
        cp.println("  [-] Delete a User");
        cp.println("  [+] Insert a new User");
        cp.println("  [~] Update a Profile");
        cp.println("  [i] Query for User ID by Username");
        cp.println("  [q] Quit Program");
        cp.println("  [?] Help (this message)");

        cp.clear();
    }

    /**
     * Print the menu for the Comment table
     */
    static void print_commentMenu() {

        print_header("Comment Menu");
        cp.setForegroundColor(FColor.YELLOW);
        cp.setBackgroundColor(BColor.BLACK);

        cp.println("  [T] Create tblData");
        cp.println("  [D] Drop tblData");
        cp.println("  [1] Query for a specific Comment");
        cp.println("  [*] Query for all Comments");
        cp.println("  [m] Query for all Comments for a specific Message");
        cp.println("  [-] Delete a Comment");
        cp.println("  [+] Insert a new Comment");
        cp.println("  [q] Quit Program");
        cp.println("  [?] Help (this message)");

        cp.clear();
    }

    /**
     * Print the menu for the Session table
     */
    static void print_sessionMenu() {

        print_header("Session Menu");
        cp.setForegroundColor(FColor.YELLOW);
        cp.setBackgroundColor(BColor.BLACK);

        cp.println("  [T] Create tblData");
        cp.println("  [D] Drop tblData");
        cp.println("  [1] Query for a specific user's session");
        cp.println("  [k] query for a specific session");
        cp.println("  [*] Query for all sessions");
        cp.println("  [-] Delete a session");
        cp.println("  [+] Insert a new row");
        cp.println("  [q] Quit Program");
        cp.println("  [?] Help (this message)");

        cp.clear();
    }

    /**
     * Print the menu for the Vote table
     */
    static void print_voteMenu() {

        print_header("Vote Menu");
        cp.setForegroundColor(FColor.YELLOW);
        cp.setBackgroundColor(BColor.BLACK);

        cp.println("  [T] Create tblData");
        cp.println("  [D] Drop tblData");
        cp.println("  [1] Query for a specific row");
        cp.println("  [*] Query for all rows");
        cp.println("  [-] Delete a row");
        cp.println("  [+] Insert a new row");
        cp.println("  [~] Update a row");
        cp.println("  [q] Quit Program");
        cp.println("  [?] Help (this message)");

        cp.clear();
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
            cp.print("\n[" + actions + "] :> ");
            System.out.flush();
            // cp.println("");
            String action;
            try {
                action = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            if (action.length() != 1) {
                cp.println("Please enter only 1 character");
                continue;
            }

            if (actions.contains(action)) {
                return action.charAt(0);
            }
            cp.println("Invalid Command");
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
            cp.print(message + " :> ");
            System.out.flush();
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
            cp.print(message + " :> ");
            System.out.flush();
            i = Integer.parseInt(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return i;
    }

    // interaction with message
    static void messageMenu(Database db, BufferedReader in) {
        int check = 1;
        print_messageMenu();
        while (check == 1) {

            // Get the user's request, and do it
            char action = prompt(in, "TD1*-+~q^v?");

            int id; // input interger (ID)
            int result; // returned result from database

            /////////////////////////////////////////////////////////////////
            ////////////////// LIST OF ALL MESSAGE COMMAND //////////////////
            /////////////////////////////////////////////////////////////////

            switch (action) {
            // print help message
            case '?':
                print_messageMenu();
                break;

            // quit message menu
            case 'q':
                check = 0;
                break;

            // create new message_Table
            case 'T':
                db.create_messageTable();
                break;

            // delete message_Table
            case 'D':
                db.drop_messageTable();
                break;

            // Query for a specific Message, will ask for message_ID
            case '1':
                id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                Database.message_RowData res = db.select_messageOne(id);
                if (res != null) {
                    cp.println("User Id: " + res.uId + ", Message Id: " + res.mId);
                    cp.println("Content: \"" + res.mMessage + "\"");
                    cp.println("Like: " + res.mlikeCount + "\tDislike: " + res.mdislikeCount);
                }
                break;

            // show all Messages
            case '*':
                ArrayList<Database.message_RowData> messageList = db.select_messageAll();
                if (messageList == null)
                    continue;
                cp.println("  Current Message Database Contents");
                cp.println("  -------------------------");
                for (Database.message_RowData row : messageList) {
                    cp.println("User Id: " + row.uId + ", Message Id: " + row.mId);
                    cp.println("Content: \"" + row.mMessage + "\"");
                    cp.println("Like: " + row.mlikeCount + "\tDislike: " + row.mdislikeCount);
                }
                break;

            // delete one message specified by ID
            case '-':
                id = getInt(in, "Enter the Message ID:");
                if (id == -1)
                    continue;
                result = db.delete_messageRow(id);
                if (result == -1)
                    continue;
                cp.println("  " + result + " rows deleted");
                break;

            // insert new message
            case '+':
                String message = getString(in, "Enter the message");
                id = getInt(in, "Eneter the User ID");
                if (id == -1 || message.equals(""))
                    continue;
                result = db.insert_messageRow(message, id);

                cp.println(result + " rows added");
                break;

            // update one message specified by ID
            case '~':
                id = getInt(in, "Enter the Message ID: ");
                if (id == -1)
                    continue;
                String newMessage = getString(in, "Enter the new message");
                result = db.update_messageOne(id, newMessage);
                if (result == -1)
                    continue;
                cp.println("row #" + result + ": updated");
                break;

            // add like on specific message
            case '^':
                id = getInt(in, "Enter the Message ID: ");
                if (id == -1)
                    continue;
                result = db.addLike(id, 1);
                if (result == -1) {
                    cp.println("Adding like failed");
                    ;
                    continue;
                }

                cp.println("row #" + result + ": like added");
                break;

            // add dislike on specific message
            case 'v':
                id = getInt(in, "Enter the Message ID: ");
                if (id == -1)
                    continue;
                result = db.addDislike(id, -1);
                if (result == -1)
                    continue;
                cp.println("row #" + result + ": dislike added");
                break;

            }
        }
    }

    static void userMenu(Database db, BufferedReader in) {
        print_userMenu();
        while (true) {
            // Get the user's request, and do it
            char action = prompt(in, "TD1*-+~irq?");

            if (action == '?') {
                print_userMenu();
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
                    cp.println("User Id: " + res.uId + ", Real Name: " + res.uRealname);
                    cp.println("Username: " + res.uUsername + ", Email: " + res.uEmail);
                    cp.println("Bio: " + res.uProfile);
                }
            } else if (action == '*') {
                ArrayList<Database.user_RowData> res = db.select_userAll();
                if (res == null)
                    continue;
                cp.println("  Current User Database Contents");
                cp.println("  -------------------------");
                for (Database.user_RowData rd : res) {
                    cp.println("User Id: " + rd.uId + ", Real Name: " + rd.uRealname);
                    cp.println("Username: " + rd.uUsername + ", Email: " + rd.uEmail);
                    cp.println("Bio: " + rd.uProfile + "\n");
                }
            } else if (action == '-') {
                int id = getInt(in, "Enter the User ID: ");
                if (id == -1)
                    continue;
                int res = db.delete_userRow(id);
                if (res == -1)
                    continue;
                cp.println("  " + res + " rows deleted");
            } else if (action == '+') {
                String username = getString(in, "Enter the username: ");
                String realname = getString(in, "Enter the real name: ");
                String email = getString(in, "Enter the email: ");
                if (username.equals("") || realname.equals("") || email.equals(""))
                    continue;
                int res = addUser(db, username, realname, email);
                cp.println(res + " rows added");
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
                cp.println("  " + res + " rows updated");
            } else if (action == 'i') {
                String username = getString(in, "Enter the Username: ");
                if (username.equals(""))
                    continue;
                int res = db.get_userId(username);
                if (res == -1) {
                    cp.println("Sorry we can't locate a user id by the user name you inputed");
                    continue;
                }
                cp.println("User ID: " + res + " Username: " + username);
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
        print_commentMenu();
        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            // function call
            char action = prompt(in, "TD1*m-+~q?");
            if (action == '?') {
                print_commentMenu();
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
                    cp.println("User Id: " + res.uId + ", Message Id: " + res.mId + ", Comment Id: " + res.cId);
                    cp.println("  --> " + res.cComment);
                }
            } else if (action == '*') {
                ArrayList<Database.comment_RowData> res = db.select_commentAll();
                if (res == null)
                    continue;
                cp.println("  Current Comment Database Contents");
                cp.println("  -------------------------");
                for (Database.comment_RowData rd : res) {
                    cp.println("User Id: " + rd.uId + ", Message Id: " + rd.mId + ", Comment Id: " + rd.cId);
                    cp.println("  --> " + rd.cComment);
                }
            } else if (action == 'm') {
                int id = getInt(in, "Enter the Message ID: ");
                if (id == -1)
                    continue;
                ArrayList<Database.comment_RowData> res = db.select_messageComment(id);
                if (res == null)
                    continue;
                cp.println("  Current Comment Database For Message " + id);
                cp.println("  -------------------------");
                for (Database.comment_RowData rd : res) {
                    cp.println("User Id: " + rd.uId + ", Comment Id: " + rd.cId);
                    cp.println("  --> " + rd.cComment);
                }
            } else if (action == '-') {
                int id = getInt(in, "Enter the comment ID: ");
                if (id == -1)
                    continue;
                int res = db.delete_commentRow(id);
                if (res == -1)
                    continue;
                cp.println("  " + res + " rows deleted");
            } else if (action == '+') {
                int uId = getInt(in, "Enter the User Id: ");
                int mId = getInt(in, "Enter the Message Id: ");
                String comment = getString(in, "Enter the Comment: ");
                if (comment.equals("") || uId == -1 || mId == -1)
                    continue;
                int res = db.insert_commentRow(comment, uId, mId);
                cp.println(res + " rows added");
            } /*
               * else if (action == '~') { int id = getInt(in, "Enter the comment ID: "); if
               * (id == -1) continue; String newMessage = getString(in,
               * "Enter the new message"); int res = db.mUpdateOne(id, newMessage); if (res ==
               * -1) continue; cp.println("  " + res + " rows updated"); }
               */
        }
    }

    static void clearAll(Database db, BufferedReader in) {
        boolean safe = true;
        while (safe) {
            char action = prompt(in,
                    "Good afternoon, Mr. Hunt. Your mission, should you choose to accept it, involves the elimination of all current data. (y/n)");
            if (action == 'y') {
                cp.println(
                        "As always, should you or any of your Force be caught or killed, the Secretary will disavow any knowledge of your actions. These data tables will self-destruct in one second. Good luck");
                db.drop_voteTable();
                db.drop_commentTable();
                db.drop_sessionTable();
                db.drop_messageTable();
                db.drop_userTable();
                break;
            } else if (action == 'n') {
                break;
            }
        }
    }

    static void sessionMenu(Database db, BufferedReader in) {
        print_sessionMenu();
        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            // function call
            char action = prompt(in, "TD1*-+kq?");
            if (action == '?') {
                print_sessionMenu();
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
                    cp.println("  [" + res.uId + "] " + res.key);
                }
            } else if (action == '*') {
                ArrayList<Database.session_RowData> res = db.select_sessionAll();
                if (res == null)
                    continue;
                cp.println("  Current Session Key Database Contents");
                cp.println("  -------------------------");
                for (Database.session_RowData rd : res) {
                    cp.println("  [" + rd.uId + "] " + rd.key);
                }
            } else if (action == '-') {
                int uId = getInt(in, "Enter the User Id: ");
                if (uId == -1)
                    continue;
                int res = db.delete_sessionRow(uId);
                if (res == -1)
                    continue;
                cp.println("  " + res + " rows deleted");
            } else if (action == '+') {
                int uId = getInt(in, "Enter the User Id: ");
                if (uId == -1)
                    continue;
                String key = getString(in, "Enter the new session key");
                int res = db.insert_sessionRow(uId, key);
                cp.println(res + " rows added");
            } else if (action == 'k') {
                int uId = getInt(in, "Enter the User ID: ");
                if (uId == -1)
                    continue;
                String res = db.get_sessionKey(uId);
                if (res != null) {
                    cp.println("  [" + uId + "] " + res);
                }
            }
        }
    }

    static void voteMenu(Database db, BufferedReader in) {
        print_voteMenu();
        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            // function call
            char action = prompt(in, "TD1*-+~q?");
            if (action == '?') {
                print_voteMenu();
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
                    cp.println("User Id: " + res.uId + ", Message Id: " + res.mId + ", Vote: " + res.vote);
                }
            } else if (action == '*') {
                ArrayList<Database.vote_RowData> res = db.select_voteAll();
                if (res == null)
                    continue;
                cp.println("  Current Vote Database Contents");
                cp.println("  -------------------------");
                for (Database.vote_RowData rd : res) {
                    cp.println("User Id: " + rd.uId + ", Message Id: " + rd.mId + ", Vote: " + rd.vote);
                }
            } else if (action == '-') {
                int uId = getInt(in, "Enter the User Id: ");
                int mId = getInt(in, "Enter the Message Id: ");
                if (uId == -1 || mId == -1)
                    continue;
                int res = db.delete_voteRow(uId, mId);
                if (res == -1)
                    continue;
                cp.println("  " + res + " rows deleted");
            } else if (action == '+') {
                int uId = getInt(in, "Enter the User Id: ");
                int mId = getInt(in, "Enter the Message Id: ");
                int vote = getInt(in, "Enter the vote: ");
                if (uId == -1 || mId == -1 || vote == -1)
                    continue;
                int res = db.insert_voteRow(vote, uId, mId);
                cp.println(res + " rows added");
            } else if (action == '~') {
                int uId = getInt(in, "Enter the User Id: ");
                int mId = getInt(in, "Enter the Message Id: ");
                if (uId == -1 || mId == -1)
                    continue;
                int newVote = getInt(in, "Enter the new vote: ");
                int res = db.update_voteOne(newVote, uId, mId);
                if (res == -1)
                    continue;
                cp.println("  " + res + " rows updated");
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
            cp.println(response.getStatusCode());
            cp.println(response.getBody());
            cp.println(response.getHeaders());
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

        // set database url
        String db_url = "postgres://tcwbhxrhfigzfm:d568d19b943919030ec1ba14dd3a8a46aa49826f718793400c245a2ea7c39c6c@ec2-107-21-233-72.compute-1.amazonaws.com:5432/d4934ifjf01fi2";

        // Get a fully-configured connection to the database
        Database db = Database.getDatabase(db_url);
        if (db == null)
            return;

        // set up color printer
        cp = new ColoredPrinter.Builder(1, false).foreground(FColor.WHITE).background(BColor.BLACK).build();

        print_main_menu();
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
            print_main_menu();
        }
        // Always remember to disconnect from the database when the program
        // exits
        db.disconnect();
    }

    // beyond this are helper function

    // print header (menu)
    private static void print_header(String header) {

        String filler = "";
        for (int i = 0; i < header.length(); i++) {
            filler += "/";
        }

        cp.setForegroundColor(FColor.GREEN);
        // cp.setBackgroundColor(BColor.WHITE);
        cp.setBackgroundColor(BColor.BLACK);
        cp.println("");
        cp.println("///////////////" + filler + "////////////////");
        cp.println("////////////// " + header + " ///////////////");
        cp.println("///////////////" + filler + "////////////////");
        cp.clear();
        cp.println("");
    }
}