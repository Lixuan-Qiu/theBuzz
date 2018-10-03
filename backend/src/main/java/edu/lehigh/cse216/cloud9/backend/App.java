package edu.lehigh.cse216.cloud9.backend;

import java.util.Map;

// Import the Spark package, so that we can make use of the "get" function to 
// create an HTTP GET route
import spark.Spark;

// Import Google's JSON library
import com.google.gson.*;

/**
 * For now, our app creates an HTTP server that can only get and add data.
 */
public class App {
    public static void main(String[] args) {
        Map<String, String> env = System.getenv();
        String db_url = env.get("DATABASE_URL");
        /*
         * String ip = env.get("POSTGRES_IP"); String port = env.get("POSTGRES_PORT");
         * String user = env.get("POSTGRES_USER"); String pass =
         * env.get("POSTGRES_PASS");
         */

        // gson provides us with a way to turn JSON into objects, and objects
        // into JSON.
        //
        // NB: it must be final, so that it can be accessed from our lambdas
        //
        // NB: Gson is thread-safe. See
        // https://stackoverflow.com/questions/10380835/is-it-ok-to-use-gson-instance-as-a-static-field-in-a-model-bean-reuse
        final Gson gson = new Gson();

        // dataStore holds all of the data that has been provided via HTTP
        // requests
        //
        // NB: every time we shut down the server, we will lose all data, and
        // every time we start the server, we'll have an empty dataStore,
        // with IDs starting over from 0.
        // Give the Database object a connection, fail if we cannot get one
        Database database = Database.getDatabase(db_url);// ,ip, port, user, pass);
        if (database == null)
            return;

        /////////////// Spark PORT//////////////////

        // Get the port on which to listen for requests
        Spark.port(getIntFromEnv("PORT", 4567));
        // Set up the location for serving static files
        Spark.staticFileLocation("/web");

        // Set up a route for serving the main page

        /////////////////////// GET///////////////////////
        Spark.get("/", (req, res) -> {
            res.redirect("/index.html");
            return "";
        });

        ///////////////// LOGIN ///////////////////////////
        Spark.get("/login", (request, response) -> {
            // parse request to SimpleRequest
            FirstRequest req = gson.fromJson(request.body(), FirstRequest.class);
            response.status(200);
            response.type("application/json");

            // retrive all necessary variable
            int uid = database.get_userId(req.username);

            // send response back
            // check the password if it is correct
            if (database.get_Password(uid).equals(database.generate_Password(uid, req.password))) {
                Database.session_RowData sessionkey = database.select_sessionOne(uid);
                if (sessionkey == null) {
                    database.insert_sessionRow(uid);
                } else {
                    database.delete_sessionRow(uid);
                    database.insert_sessionRow(uid);
                }
                return gson.toJson(new FirstResponse("ok", "session key for uid = " + uid + " is sent.",
                        database.get_sessionKey(uid)));
            } else {
                return gson.toJson(new StructuredResponse("error", "login error: wrong password", null));
            }
        });
        /////////////////////// GET MESSAGE ALL///////////////////////
        // GET route that returns all message titles and Ids. All we do is get
        // the data, embed it in a StructuredResponse, turn it into JSON, and
        // return it. If there's no data, we return "[]", so there's no need
        // for error handling.
        Spark.get("/messages", (request, response) -> {
            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.status(200);
            response.type("application/json");
            // ensure status 200 OK, with a MIME type of JSON
            if (req.key == database.get_sessionKey(req.uid)) {
                response.status(200);
                response.type("application/json");
                return gson.toJson(new StructuredResponse("ok", null, database.select_messageAll()));
            } else {
                return gson.toJson(new StructuredResponse("error", "login error: wrong sessionkey", null));
            }
        });

        /////////////////////// GET MESSAGE SINGLE ROW///////////////////////
        // GET route that returns everything for a single row in the DataStore.
        // The ":id" suffix in the first parameter to get() becomes
        // request.params("id"), so that we can get the requested row ID. If
        // ":id" isn't a number, Spark will reply with a status 500 Internal
        // Server Error. Otherwise, we have an integer, and the only possible
        // error is that it doesn't correspond to a row with data.
        Spark.get("/messages/:id", (request, response) -> {
            int idx = Integer.parseInt(request.params("id"));
            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.status(200);
            response.type("application/json");
            // ensure status 200 OK, with a MIME type of JSON
            if (req.key == database.get_sessionKey(req.uid)) {
                Database.message_RowData data = database.select_messageOne(idx);
                if (data == null) {
                    return gson.toJson(new StructuredResponse("error", idx + " not found", null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, data));
                }
            } else {
                return gson.toJson(new StructuredResponse("error", "login error: wrong sessionkey", null));
            }
        });

        /////////////////////// POST MESSAGE///////////////////////
        // POST route for adding a new element to the DataStore. This will read
        // JSON from the body of the request, turn it into a SimpleRequest
        // object, extract the title and message, insert them, and return the
        // ID of the newly created row.
        Spark.post("/messages", (request, response) -> {
            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.status(200);
            response.type("application/json");
            // ensure status 200 OK, with a MIME type of JSON
            if (req.key == database.get_sessionKey(req.uid)) {
                // NB: createEntry checks for null title and message
                int newId = database.insert_messageRow(req.mMessage, req.uid);
                if (newId == -1) {
                    return gson.toJson(new StructuredResponse("error", "error performing insertion", null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", "executeUpdate() return: " + newId, null));
                }
            } else {
                return gson.toJson(new StructuredResponse("error", "login error: wrong sessionkey", null));
            }
        });

        /////////////////////// PUT MESSAGE///////////////////////
        // PUT route for updating a row in the DataStore. This is almost
        // exactly the same as POST
        Spark.put("/messages/:id", (request, response) -> {
            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.status(200);
            response.type("application/json");
            // ensure status 200 OK, with a MIME type of JSON
            if (req.key == database.get_sessionKey(req.uid)) {
                // If we can't get an ID or can't parse the JSON, Spark will send
                // a status 500
                int idx = Integer.parseInt(request.params("id"));
                int result = database.update_messageOne(idx, req.mMessage);
                if (result == -1) {
                    return gson.toJson(new StructuredResponse("error", "unable to update row " + idx, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", "message id: " + idx + " is updated.", result));
                }
            } else {
                return gson.toJson(new StructuredResponse("error", "login error: wrong sessionkey", null));
            }
        });
        /////////////////////// LIKE MESSAGE///////////////////////
        // PUT route for updating a row in the DataStore. This is almost
        // exactly the same as POST
        Spark.put("/messages/:id/like", (request, response) -> {
            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.status(200);
            response.type("application/json");
            // ensure status 200 OK, with a MIME type of JSON
            if (req.key == database.get_sessionKey(req.uid)) {
                // If we can't get an ID or can't parse the JSON, Spark will send
                // a status 500
                int idx = Integer.parseInt(request.params("id"));
                // check if this guy already like/dislike or not
                Database.vote_RowData Vote = database.select_voteOne(req.uid, idx);
                if (Vote.vote != 1) { 
                    if (Vote.vote == -1) {
                        database.update_voteOne(1, req.uid, idx);
                        database.addDislike(idx, -1);
                    } else if (Vote.vote == 0) {
                        database.insert_voteRow(1, req.uid, idx);
                    }
                    int result = database.addLike(idx, 1);
                    if (result == -1) {
                        return gson.toJson(new StructuredResponse("error", "unable to update row " + idx, null));
                    } else {
                        return gson.toJson(new StructuredResponse("ok", "message id: " + idx + " is liked.", result));
                    }
                }
                else
                    return gson.toJson(new StructuredResponse("ok", "message id: " + idx + " is already disliked.", 0));
            } else {
                return gson.toJson(new StructuredResponse("error", "login error: wrong sessionkey", null));
            }
        });
        /////////////////////// DISLIKE MESSAGE///////////////////////
        // PUT route for updating a row in the DataStore. This is almost
        // exactly the same as POST
        Spark.put("/messages/:id/dislike", (request, response) -> {
            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.status(200);
            response.type("application/json");
            // ensure status 200 OK, with a MIME type of JSON
            if (req.key == database.get_sessionKey(req.uid)) {
                // If we can't get an ID or can't parse the JSON, Spark will send
                // a status 500
                int idx = Integer.parseInt(request.params("id"));
                // ensure status 200 OK, with a MIME type of JSON
                Database.vote_RowData Vote = database.select_voteOne(req.uid, idx);
                if (Vote.vote != -1) { 
                    if (Vote.vote == 1) {
                        database.update_voteOne(-1, req.uid, idx);
                        database.addLike(idx, -1);
                    } else if (Vote.vote == 0) {
                        database.insert_voteRow(-1, req.uid, idx);
                    }
                    int result = database.addDislike(idx, 1);
                    if (result == -1) {
                        return gson.toJson(new StructuredResponse("error", "unable to update row " + idx, null));
                    } else {
                        return gson.toJson(new StructuredResponse("ok", "message id: " + idx + " is disliked.", result));
                    }
                }
                else
                    return gson.toJson(new StructuredResponse("ok", "message id: " + idx + " is already liked.", 0));
            } else {
                return gson.toJson(new StructuredResponse("error", "login error: wrong sessionkey", null));
            }
        });

        /////////////////////// DELETE MESSAGE///////////////////////
        // DELETE route for removing a row from the DataStore
        Spark.delete("/messages/:id", (request, response) -> {
            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.status(200);
            response.type("application/json");
            // ensure status 200 OK, with a MIME type of JSON
            if (req.key == database.get_sessionKey(req.uid)) {
                // If we can't get an ID, Spark will send a status 500
                int idx = Integer.parseInt(request.params("id"));

                // NB: we won't concern ourselves too much with the quality of the
                // message sent on a successful delete
                int result = database.delete_messageRow(idx);
                if (result == -1) {
                    return gson.toJson(new StructuredResponse("error", "unable to delete row " + idx, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", "message id: " + idx + " is deleted.", null));
                }
            } else {
                return gson.toJson(new StructuredResponse("error", "login error: wrong sessionkey", null));
            }
        });

        /////////////////////// GET PROFILE///////////////////////
        // GET route that returns everything for a single row in the DataStore.
        // The ":id" suffix in the first parameter to get() becomes
        // request.params("id"), so that we can get the requested row ID. If
        // ":id" isn't a number, Spark will reply with a status 500 Internal
        // Server Error. Otherwise, we have an integer, and the only possible
        // error is that it doesn't correspond to a row with data.
        Spark.get("/profile", (request, response) -> {
            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.status(200);
            response.type("application/json");
            // ensure status 200 OK, with a MIME type of JSON
            if (req.key == database.get_sessionKey(req.uid)) {
                Database.user_RowData data = database.select_userOne(req.uid);
                if (data == null) {
                    return gson.toJson(new StructuredResponse("error", "user_id: " + req.uid + " not found", null));
                } else {
                    String profile = data.uProfile;
                    String email = data.uEmail;
                    String realname = data.uRealname;
                    String[] box = { realname, profile, email };
                    return gson.toJson(new StructuredResponse("ok", null, box));
                }
            } else {
                return gson.toJson(new StructuredResponse("error", "login error: wrong sessionkey", null));
            }
        });

        /////////////////////// GET PROFILE OF THAT ID///////////////////////
        // GET route that returns everything for a single row in the DataStore.
        // The ":id" suffix in the first parameter to get() becomes
        // request.params("id"), so that we can get the requested row ID. If
        // ":id" isn't a number, Spark will reply with a status 500 Internal
        // Server Error. Otherwise, we have an integer, and the only possible
        // error is that it doesn't correspond to a row with data.
        Spark.get("/profile/:id", (request, response) -> {
            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.status(200);
            response.type("application/json");
            // ensure status 200 OK, with a MIME type of JSON
            if (req.key == database.get_sessionKey(req.uid)) {
                int idx = Integer.parseInt(request.params("id"));
                Database.user_RowData data = database.select_userOne(idx);
                if (data == null) {
                    return gson.toJson(new StructuredResponse("error", "user_id: " + idx + " not found", null));
                } else {
                    String profile = data.uProfile;
                    String email = data.uEmail;
                    String realname = data.uRealname;
                    String[] box = { realname, profile, email };
                    return gson.toJson(new StructuredResponse("ok", null, box));
                }
            } else {
                return gson.toJson(new StructuredResponse("error", "login error: wrong sessionkey", null));
            }
        });

        /////////////////////// PUT PROFILE///////////////////////
        // PUT route for updating a row in the DataStore. This is almost
        // exactly the same as POST
        Spark.put("/profile", (request, response) -> {
            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            if (req.key == database.get_sessionKey(req.uid)) {
                Database.user_RowData data = database.select_userOne(req.uid);
                String profile = req.mMessage;
                int result = database.update_userProfile(req.uid, req.mMessage);
                if (result == -1) {
                    return gson.toJson(new StructuredResponse("error", "unable to update profile " + req.uid, null));
                } else {
                    return gson.toJson(
                            new StructuredResponse("ok", "profile of this uid: " + req.uid + " is updated.", result));
                }
            } else {
                return gson.toJson(new StructuredResponse("error", "login error: wrong sessionkey", null));
            }
        });

        /////////////////////// PUT PASSWORD///////////////////////
        // PUT route for updating a row in the DataStore. This is almost
        // exactly the same as POST
        Spark.put("/newpassword/:id", (request, response) -> {
            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.status(200);
            response.type("application/json");
            // ensure status 200 OK, with a MIME type of JSON
            if (req.key == database.get_sessionKey(req.uid)) {
                int result = database.update_userPassword(req.uid, req.mMessage);
                if (result == -1) {
                    return gson.toJson(new StructuredResponse("error", "unable to update row " + req.uid, null));
                } else {
                    return gson.toJson(
                            new StructuredResponse("ok", "the password of uid: " + req.uid + " is updated.", result));
                }
            } else {
                return gson.toJson(new StructuredResponse("error", "login error: wrong sessionkey", null));
            }
        });

        ///////////////////// GET COMMEND /////////////////////////
        Spark.get("/commend/:id", (request, response) -> {
            int idx = Integer.parseInt(request.params("id"));
            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.status(200);
            response.type("application/json");
            // ensure status 200 OK, with a MIME type of JSON
            if (req.key == database.get_sessionKey(req.uid)) {

                return gson.toJson(new StructuredResponse("ok", null, database.select_messageComment(idx)));
            } else {
                return gson.toJson(new StructuredResponse("error", "login error: wrong sessionkey", null));
            }
        });

        ///////////////////// POST COMMEND /////////////////////////
        Spark.post("/commend/:id", (request, response) -> {
            int idx = Integer.parseInt(request.params("id"));
            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.status(200);
            response.type("application/json");
            // ensure status 200 OK, with a MIME type of JSON
            if (req.key == database.get_sessionKey(req.uid)) {
                int data = database.insert_commentRow(req.mMessage, req.uid, idx);
                if (data == 0) {
                    return gson.toJson(new StructuredResponse("error", idx + " not found", null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, data));
                }
            } else {
                return gson.toJson(new StructuredResponse("error", "login error: wrong sessionkey", null));
            }
        });

    }

    /**
     * Get an integer environment varible if it exists, and otherwise return the
     * default value.
     * 
     * @envar The name of the environment variable to get.
     * @defaultVal The integer value to use as the default if envar isn't found
     * 
     * @returns The best answer we could come up with for a value for envar
     */
    static int getIntFromEnv(String envar, int defaultVal) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get(envar) != null) {
            return Integer.parseInt(processBuilder.environment().get(envar));
        }
        return defaultVal;
    }
}