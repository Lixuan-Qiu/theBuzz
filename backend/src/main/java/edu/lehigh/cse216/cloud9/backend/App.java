package edu.lehigh.cse216.cloud9.backend;

import java.lang.String;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;

//import java.io.File;
// Import the Spark package
import spark.Spark;

// Import Google's JSON library
import com.google.gson.*;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
//Import Googles TokenVerifier object
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
//Import Google Drive functions
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.Permission;
import java.util.Base64;
import java.security.GeneralSecurityException;

public class App {
    // beyond this are helper function
    static NetHttpTransport HTTP_TRANSPORT;
    static JsonFactory JSON_FACTORY;
    static Collection<String> SCOPES;
    static GoogleCredential credential;
    static String applicationName;
    static Drive service; 

    private static void setupService() {

        try {
            // parameter setup for service builder
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            JSON_FACTORY = JacksonFactory.getDefaultInstance();

            // add scope authorization here
            SCOPES = new ArrayList<String>();
            SCOPES.add("https://www.googleapis.com/auth/drive");

            // build google credential from local json file
            // see https://cloud.google.com/iam/docs/creating-managing-service-account-keys
            // in section "Creating service account keys" for more info
            credential = GoogleCredential.fromStream(new FileInputStream("credentials.json"))
                    .createScoped(SCOPES);

            // setup application's name
            applicationName = "cse216-buzzapp";

            // initialize google drive service
            service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(applicationName)
                    .build();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    //Service Account Google Drive Set Up
    // Build a new authorized API client service.
    

    public static void main(String[] args) {
        Map<String, String> env = System.getenv();
        String db_url = env.get("DATABASE_URL");

        Map<String, String> fileURLs = new HashMap<>();

        // gson turn JSON into objects, and objects into JSON
        final Gson gson = new Gson();

        // Neal Android Client ID
        String CLIENT_ID_1 = "319649689632-7qvimdmkig66k3pd90rarf1enulobgjg.apps.googleusercontent.com";

        // Kop Android Client ID
        String CLIENT_ID_2 = "319649689632-qnpeij8911do79nb3brgi15s2dpg8k26.apps.googleusercontent.com";

        // Ron Android Client ID
        // String CLIENT_ID_3 =
        // "319649689632-7qvimdmkig66k3pd90rarf1enulobgjg.apps.googleusercontent.com";

        // Web Client ID
        String CLIENT_ID_4 = "319649689632-faqtfv5tgaa3n0urvoprhv66s9kdv6bg.apps.googleusercontent.com";

        final JacksonFactory jacksonFactory = new JacksonFactory();
        NetHttpTransport transport = new NetHttpTransport();
        // Build the verifier that will check ID Token's
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jacksonFactory)
                // Or, if multiple clients access the backend:
                .setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_4)).build();
        
        //Set up service account
        setupService();

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

            System.out.println("receive request to /login");
            System.out.println("url: " + request.url());
            System.out.println("request body: " + request.body());
            System.out.println("attributes list: " + request.attributes());
            System.out.println("contentType: " + request.contentType());
            System.out.println("headers: " + request.headers());
            System.out.println("params: " + request.params());
            System.out.println("raw: " + request.raw());
            System.out.println("requestMethod" + request.requestMethod());
            
            FirstRequest req = gson.fromJson(request.body(), FirstRequest.class);
            // parse request to FirstRequest
            String email = null;
            String name = null;
            String exp = null;
            String idTokenString = req.id_token;
            response.status(200);
            response.type("application/json");

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                Payload payload = idToken.getPayload();

                // Print user identifier
                String userIdString = payload.getSubject();
                System.out.println("User ID: " + userIdString);

                // Get profile information from payload
                email = payload.getEmail();
                boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
                name = (String) payload.get("name");
                exp = payload.get("exp").toString();
                /*
                 * String locale = (String) payload.get("locale"); String familyName = (String)
                 * payload.get("family_name"); String givenName = (String)
                 * payload.get("given_name");
                 */

            } else {
                return gson.toJson(new FirstResponse("error", "login error: Invalid ID token.", null, -1));
            }
            if (email.indexOf("@lehigh.edu") == -1) {
                return gson
                        .toJson(new FirstResponse("error", "login error: Invalid Email, must be lehigh.edu", null, -1));
            }
            int uId = database.get_userId2(email);
            if (uId == -1) {
                // If new user, then insert new row
                int l = database.insert_userRow(name, name, email);
                System.out.println("insert user row:" + l);
                uId = database.get_userId2(email);
                System.out.println("user ID:" + uId);
            } else {
                // retreive sessionData
                Database.session_RowData sessionData = database.select_sessionOne(uId);
                if (sessionData != null) { // if there is existing sessionData, delete old one
                    database.delete_sessionRow(uId);
                }
            }

            exp = exp + uId;
            // Inset new session row
            database.insert_sessionRow(uId, exp);
            // send response back
            return gson.toJson(new FirstResponse("ok", "session key for uid = " + uId + " is sent.",
                    database.get_sessionKey(uId), uId));
        });

        /////////////////////// "messages" CRUD operations ///////////////////////
        // >> GET MESSAGE ALL <<
        // return all message_RowData on success
        Spark.get("/messages", (request, response) -> {

            System.out.println("receive request to /messages");
            System.out.println("url: " + request.url());
            System.out.println("request body: " + request.headers("Authorization"));
            System.out.println("request body: " + request.body());
            System.out.println("attributes list: " + request.attributes());
            System.out.println("contentType: " + request.contentType());
            System.out.println("headers: " + request.headers());
            System.out.println("params: " + request.params());
            System.out.println("raw: " + request.raw());
            System.out.println("requestMethod" + request.requestMethod());

            // Get Session key from Authorization Header
            String key = request.headers("Authorization");

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            if (database.check_sessionKey(key)) {
                // return ArrayList<message_RowData> upon success
                return gson.toJson(new StructuredResponse("ok", null, database.select_messageAll()));

            } else {
                return gson.toJson(new StructuredResponse("error", "login error: wrong sessionkey", null));
            }
        });

        // >> GET MESSAGE SINGLE ROW <<
        // return all message_RowData on success
        Spark.get("/messages/:id", (request, response) -> {
            // String token = request.headers("Authorization");
            // Get Session key from Authorization Header
            String key = request.headers("Authorization");

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            if (database.check_sessionKey(key)) {

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

        // >> GET MESSAGE ROW FILE<<
        // return file on success
        Spark.get("/messages/:id/file", (request, response) -> {
            // String token = request.headers("Authorization");
            // Get Session key from Authorization Header
            String key = request.headers("Authorization");

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            if (database.check_sessionKey(key)) {

                int idx = Integer.parseInt(request.params("id"));
                Database.message_RowData data = database.select_messageOne(idx);
                if (data == null) {
                    return gson.toJson(new StructuredResponse("error", idx + " not found", null));
                } else {
                    String fileId = data.mfileid;
                    System.out.println("File Id:" + fileId);
                    String link = fileURLs.get(fileId);
                    return gson.toJson(new StructuredResponse("ok", null, link));
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
            // Get Session key from Authorization Header
            String key = request.headers("Authorization");
            System.out.println(key);
            System.out.println(request.headers());
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            //String image = null;
            String mfileID = req.mfileID;
            String webLink = "";

            if (database.check_sessionKey(key)) {
                if(!req.fileName.equals("")){
                    String fileName = req.fileName;
                    String filepath = fileName +".pdf";
                    System.out.println("Filename:" + req.fileName);
                    System.out.println("File:" + req.file);
                    FileOutputStream fos = new FileOutputStream(filepath);
                    fos.write(Base64.getDecoder().decode(req.file));
                    fos.close();
                    File fileMetadata = new File();
                    fileMetadata.setName(fileName);
                    java.io.File filePathf = new java.io.File(filepath);
                    FileContent mediaContent = new FileContent("application/pdf", filePathf);
                    File file = service.files().create(fileMetadata, mediaContent)
                        .setFields("id, webContentLink")
                        .execute();
                    mfileID = file.getId();
                    webLink = file.getWebContentLink();
                    fileURLs.put(mfileID, webLink);
                    Permission userPermission = new Permission()
                        .setType("anyone")
                        .setRole("reader");
                    service.permissions().create(mfileID, userPermission)
                    .execute();
                    filePathf.delete();
                }
                    int newId = database.insert_messageRow(req.mMessage, req.uid, req.img, mfileID);
                //return gson.toJson(new StructuredResponse("ok", "executeUpdate() return: " + newId, null));
                /*if(!request.getParameter("image")){
                    int newId = database.insert_messageRow(req.mMessage, req.uid);
                }*/
                if (newId == -1) {
                    return gson.toJson(new StructuredResponse("error", "error performing insertion", null));
                } 
                else {
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

            // Get Session key from Authorization Header
            String key = request.headers("Authorization");

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            if (database.check_sessionKey(key)) {

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

            // Get Session key from Authorization Header
            String key = request.headers("Authorization");

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            if (database.check_sessionKey(key)) {

                int idx = Integer.parseInt(request.params("id"));
                // check if this guy already like/dislike or not
                Database.vote_RowData Vote = database.select_voteOne(req.uid, idx);
                if (Vote == null) {
                    database.insert_voteRow(1, req.uid, idx); // insert new vote_RowData
                    int result = database.addLike(idx, 1);
                    if (result == -1) { // if failed to addLike
                        return gson.toJson(new StructuredResponse("error", "unable to update row " + idx, null));
                    } else {
                        return gson.toJson(new StructuredResponse("ok", "message id: " + idx + " is liked.", result));
                    }
                } else if (Vote.vote != 1) { // user has not liked this message

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

            // Get Session key from Authorization Header
            String key = request.headers("Authorization");

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            if (database.check_sessionKey(key)) {

                int idx = Integer.parseInt(request.params("id"));
                Database.vote_RowData Vote = database.select_voteOne(req.uid, idx);
                if (Vote == null) {
                    database.insert_voteRow(-1, req.uid, idx); // insert new vote_RowData
                    int result = database.addDislike(idx, 1);
                    if (result == -1) { // if failed to addLike
                        return gson.toJson(new StructuredResponse("error", "unable to update row " + idx, null));
                    } else {
                        return gson
                                .toJson(new StructuredResponse("ok", "message id: " + idx + " is disliked.", result));
                    }
                }
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
            // Get Session key from Authorization Header
            String key = request.headers("Authorization");

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            if (database.check_sessionKey(key)) {

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

            // Get Session key from Authorization Header
            String key = request.headers("Authorization");

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            if (database.check_sessionKey(key)) {
                int uid = database.get_uId_fromSession(key);
                Database.user_RowData data = database.select_userOne(uid);
                if (data == null) { // can't find user_RowData
                    return gson.toJson(new StructuredResponse("error", "user_id: " + uid + " not found", null));
                } else {

                    // prepare return value
                    String profile = data.uProfile;
                    String realname = data.uRealname;
                    String[] box = { realname, profile };
                    return gson.toJson(new StructuredResponse("ok", null, box));
                }
            } else {
                return gson.toJson(new StructuredResponse("error", "login error: wrong sessionkey", null));
            }
        });

        // >> GET PROFILE OF THAT ID <<
        // return other profile

        Spark.get("/profile/:id", (request, response) -> {

            // Get Session key from Authorization Header
            String key = request.headers("Authorization");

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            if (database.check_sessionKey(key)) {
                int idx = Integer.parseInt(request.params("id"));
                Database.user_RowData data = database.select_userOne(idx);
                if (data == null) {
                    return gson.toJson(new StructuredResponse("error", "user_id: " + idx + " not found", null));
                } else {

                    // prepare return value
                    String profile = data.uProfile;
                    String realname = data.uRealname;
                    String[] box = { realname, profile };
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

            // Get Session key from Authorization Header
            String key = request.headers("Authorization");

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            if (database.check_sessionKey(key)) {

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

        /////////////////////// "comment" CRUD operations ///////////////////////

        // >> GET comment <<
        Spark.get("/comment/:id", (request, response) -> {

            // Get Session key from Authorization Header
            String key = request.headers("Authorization");

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            if (database.check_sessionKey(key)) {

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

            // Get Session key from Authorization Header
            String key = request.headers("Authorization");
            //String image = request.params("uploadType");
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            if (database.check_sessionKey(key)) {
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

            // Get Session key from Authorization Header
            String key = request.headers("Authorization");

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            if (database.check_sessionKey(key)) {
                System.out.println("uid: " + req.uid + " key: " + key);
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