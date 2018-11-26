

/**
 * Login encapsulates all of the code for the form for adding an entry
 */
class Login {

    /**
     * The name of the DOM entry associated with Login
     */
    public static readonly NAME = "Login";

    /**
     * Track if the Singleton has been initialized
     */
    private static isInit = false;

    /**
     * Initialize the Login by creating its element in the DOM and
     * configuring its buttons.  This needs to be called from any public static 
     * method, to ensure that the Singleton is initialized before use
     */
    private static init() {
        if (!Login.isInit) {
            Login.isInit = true;
        }
    }

    /**
     * Refresh() doesn't really have much meaning, but just like in sNavbar, we
     * have a refresh() method so that we don't have front-end code calling
     * init().
     */
    public static refresh() {
        Login.init();
    }

    /**
     * Send data to submit the form only if the fields are both valid.  
     * Immediately hide the form when we send data, so that the user knows that 
     * their click was received.
     */
    private static submitForm() {
        // get the values of the two fields, force them to be strings, and check 
        // that neither is empty
        if (id_token === "" || id_token === null) {
            window.alert("Error: id_token is not valid");
            return;
        }
        
        console.log("Login: requesting put to " + backendUrl + "/login");
        // set up an AJAX post.  When the server replies, the result will go to
        // onSubmitResponse
        $.ajax({
            type: "POST",
            url: backendUrl + "/login",
            dataType: "json",
            data: JSON.stringify({ id_token: id_token}),
            success: Login.onSubmitResponse
        });
    }

    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a 
     * result.
     * 
     * @param data The object returned by the server
     */
    private static onSubmitResponse(data: any) {
        // If we get an "ok" message, clear the form and refresh the main 
        // listing of messages
        if (data.mStatus === "ok") {
            console.log("Login: success, key: " + data.sessionkey);
            user_id = data.uid;
            session_key = data.sessionkey;

            ElementList.refresh();
            Login.callMainPage();
        }
        // Handle explicit errors with a detailed popup message
        else if (data.mStatus === "error") {
            window.alert("The server replied with an error:\n" + data.echoMessage);
        }
        // Handle other errors with a less-detailed popup message
        else {
            window.alert("Unspecified error");
        }
    }

    private static callMainPage(){

        console.log("Login: call main page");
        $("#" + Login.NAME + "-container").hide();
        $("#" + ElementList.NAME).show();
        $("#" + NewEntryForm.NAME).show();
    }
    public static hideMainPage() {

        console.log("Login: hide main page");
        $("#" + Login.NAME + "-container").show();
        $("#" + ElementList.NAME).hide();
        $("#" + NewEntryForm.NAME).hide();
        session_key = "";
    }

    
   
}

