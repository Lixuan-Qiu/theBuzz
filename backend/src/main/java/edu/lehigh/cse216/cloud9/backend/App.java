package edu.lehigh.cse216.cloud9.backend;

import java.util.Map;

// Import the Spark package
import spark.Spark;

// Import Google's JSON library
import com.google.gson.*;

public class App {
    public static void main(String[] args) {
        Map<String, String> env = System.getenv();
        String db_url = env.get("DATABASE_URL");

        // gson turn JSON into objects, and objects into JSON

        final Gson gson = new Gson();

        Database database = Database.getDatabase(db_url);// ,ip, port, user, pass);
        if (database == null)
            return;

        /////////////// Spark PORT//////////////////

        // Get the port on which to listen for requests
        Spark.port(getIntFromEnv("PORT", 4567));
        // Set up the location for serving static files
        Spark.staticFileLocation("/web");

        // Set up a route for serving the main page

        // ???homepage??? //
        Spark.get("/", (req, res) -> {
            res.redirect("/index.html");
            return "";
        });

        /////////////////////////// LOGIN ///////////////////////////
        Spark.post("/login", (request, response) -> {

            // parse request to FirstRequest
            FirstRequest req = gson.fromJson(request.body(), FirstRequest.class);
            response.status(200);
            response.type("application/json");

            // retrive all necessary variable
            int uid = database.get_userId(req.username);

            // send response back
            // check the password if it is correct
            if (database.get_Password(uid).equals(database.generate_Password(uid, req.password))) {
                // retreive sessionData
                Database.session_RowData sessionData = database.select_sessionOne(uid);
                if (sessionData == null) { // if there's no existing sessionData insert new one
                    database.insert_sessionRow(uid);
                } else { // if there is existing sessionData, delete old one, then insert new one
                    database.delete_sessionRow(uid);
                    database.insert_sessionRow(uid);
                }
                return gson.toJson(new FirstResponse("ok", "session key for uid = " + uid + " is sent.",
                        database.get_sessionKey(uid), uid));
            } else {
                return gson.toJson(new FirstResponse("error", "login error: wrong password", -1, -1));
            }
        });

        /////////////////////// "messages" CRUD operations ///////////////////////
        // >> GET MESSAGE ALL <<
        // return all message_RowData on success
        Spark.get("/messages", (request, response) -> {

            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            int key = req.key;
            int uid = req.uid;
            // session_key check
            if (key == database.get_sessionKey(uid)) {
                // return ArrayList<message_RowData> upon success
                return gson.toJson(new StructuredResponse("ok", null, database.select_messageAll()));

            } else {
                return gson.toJson(new StructuredResponse("error", "login error: wrong sessionkey", null));
            }
        });

        // >> GET MESSAGE SINGLE ROW <<
        // return all message_RowData on success
        Spark.get("/messages/:id", (request, response) -> {

            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            // session_key check
            if (req.key == database.get_sessionKey(req.uid)) {

                int idx = Integer.parseInt(request.params("id"));
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

        // >> POST MESSAGE <<
        //
        Spark.post("/messages", (request, response) -> {
            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            // session_key check
            if (req.key == database.get_sessionKey(req.uid)) {

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

        // >> PUT MESSAGE <<
        // update selected message with given String: mMessage

        Spark.put("/messages/:id", (request, response) -> {
            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            // session_key check
            if (req.key == database.get_sessionKey(req.uid)) {

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

        // >> LIKE MESSAGE <<
        // increase likeCount of selected message

        Spark.put("/messages/:id/like", (request, response) -> {
            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            // session_key check
            if (req.key == database.get_sessionKey(req.uid)) {

                int idx = Integer.parseInt(request.params("id"));
                // check if this guy already like/dislike or not
                Database.vote_RowData Vote = database.select_voteOne(req.uid, idx);
                if (Vote.vote != 1) { // user has not liked this message

                    if (Vote.vote == -1) { // user dislike this message before

                        database.update_voteOne(1, req.uid, idx); // update vote table
                        database.addDislike(idx, -1); // subtract dislike count

                    } else if (Vote.vote == 0) { // if user has no interaction with this message before

                        database.insert_voteRow(1, req.uid, idx); // insert new vote_RowData
                    }

                    int result = database.addLike(idx, 1);
                    if (result == -1) { // if failed to addLike
                        return gson.toJson(new StructuredResponse("error", "unable to update row " + idx, null));
                    } else {
                        return gson.toJson(new StructuredResponse("ok", "message id: " + idx + " is liked.", result));
                    }

                } else // user already like this message
                    return gson.toJson(new StructuredResponse("ok", "message id: " + idx + " is already liked.", 0));
            } else {
                return gson.toJson(new StructuredResponse("error", "login error: wrong sessionkey", null));
            }
        });

        // >> DISLIKE MESSAGE <<
        // increase dislikeCount of selected message

        Spark.put("/messages/:id/dislike", (request, response) -> {

            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            // session_key check
            if (req.key == database.get_sessionKey(req.uid)) {

                int idx = Integer.parseInt(request.params("id"));
                Database.vote_RowData Vote = database.select_voteOne(req.uid, idx);

                if (Vote.vote != -1) { // user has not disliked this message before

                    if (Vote.vote == 1) { // user like this message before

                        database.update_voteOne(-1, req.uid, idx); // update vote table
                        database.addLike(idx, -1); // subtract like count

                    } else if (Vote.vote == 0) { // user has no interaction with this message before

                        database.insert_voteRow(-1, req.uid, idx);
                    }

                    int result = database.addDislike(idx, 1);
                    if (result == -1) { // if addDislike failed
                        return gson.toJson(new StructuredResponse("error", "unable to update row " + idx, null));
                    } else {
                        return gson
                                .toJson(new StructuredResponse("ok", "message id: " + idx + " is disliked.", result));
                    }
                } else
                    return gson.toJson(new StructuredResponse("ok", "message id: " + idx + " is already disliked.", 0));
            } else {
                return gson.toJson(new StructuredResponse("error", "login error: wrong sessionkey", null));
            }
        });

        // >> DELETE MESSAGE <<
        // removing specified message_row

        Spark.delete("/messages/:id", (request, response) -> {
            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            // session_key check
            if (req.key == database.get_sessionKey(req.uid)) {

                int idx = Integer.parseInt(request.params("id"));

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

        /////////////////////// "profile" CRUD operations ///////////////////////

        // >> GET PROFILE <<
        // return self profile

        Spark.get("/profile", (request, response) -> {

            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            // session_key check
            if (req.key == database.get_sessionKey(req.uid)) {

                Database.user_RowData data = database.select_userOne(req.uid);
                if (data == null) { // can't find user_RowData
                    return gson.toJson(new StructuredResponse("error", "user_id: " + req.uid + " not found", null));
                } else {

                    // prepare return value
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

        // >> GET PROFILE OF THAT ID <<
        // return other profile

        Spark.get("/profile/:id", (request, response) -> {

            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            // session_key check
            if (req.key == database.get_sessionKey(req.uid)) {
                int idx = Integer.parseInt(request.params("id"));
                Database.user_RowData data = database.select_userOne(idx);
                if (data == null) {
                    return gson.toJson(new StructuredResponse("error", "user_id: " + idx + " not found", null));
                } else {

                    // prepare return value
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

        // >> PUT PROFILE <<
        // update the specified prifile

        Spark.put("/profile", (request, response) -> {

            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            // session_key check
            if (req.key == database.get_sessionKey(req.uid)) {

                int result = database.update_userProfile(req.uid, req.mMessage);
                if (result == -1) { // on update_userProfile fail
                    return gson.toJson(new StructuredResponse("error", "unable to update profile " + req.uid, null));
                } else {
                    return gson.toJson(
                            new StructuredResponse("ok", "profile of this uid: " + req.uid + " is updated.", result));
                }
            } else {
                return gson.toJson(new StructuredResponse("error", "login error: wrong sessionkey", null));
            }
        });

        // >> PUT PASSWORD <<
        // update password

        Spark.put("/newpassword/:id", (request, response) -> {

            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            // session_key check
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

        /////////////////////// "comment" CRUD operations ///////////////////////

        // >> GET comment <<
        Spark.get("/comment/:id", (request, response) -> {

            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            // session_key check
            if (req.key == database.get_sessionKey(req.uid)) {

                int idx = Integer.parseInt(request.params("id"));
                return gson.toJson(new StructuredResponse("ok", null, database.select_messageComment(idx)));

            } else {
                return gson.toJson(new StructuredResponse("error", "login error: wrong sessionkey", null));
            }
        });

        // >> POST comment <<
        Spark.post("/comment/:id", (request, response) -> {
            int idx = Integer.parseInt(request.params("id"));
            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            // session_key check
            if (req.key == database.get_sessionKey(req.uid)) {
                int result = database.insert_commentRow(req.mMessage, req.uid, idx);
                if (result == 0) { // on failure
                    return gson.toJson(new StructuredResponse("error", idx + " not found", null));

                } else {
                    return gson.toJson(new StructuredResponse("ok", null, result));
                }
            } else {
                return gson.toJson(new StructuredResponse("error", "login error: wrong sessionkey", null));
            }
        });

        // >> LOGOUT <<
        Spark.post("/logout", (request, response) -> {
            // parse request to SimpleRequest
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            // session_key check
            if (req.key == database.get_sessionKey(req.uid)) {
                int result = database.delete_sessionRow(req.uid);
                if (result == -1) { // on failure
                    return gson.toJson(new StructuredResponse("error", req.uid + " not found", null));

                } else {
                    return gson.toJson(new StructuredResponse("ok", null, result));
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