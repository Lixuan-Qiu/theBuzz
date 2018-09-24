/**
 * NewMessage encapsulates all of the code for the form for adding an entry
 */
class NewMessage {

    /**
     * The name of the DOM entry associated with NewMessage
     */
    private static readonly NAME = "NewMessage";
    
    /**
     * Track if the Singleton has been initialized
     */
    private static isInit = false;

    /**
     * Initialize the NewMessage by creating its element in the DOM and 
     * configuring its buttons.  This needs to be called from any public static 
     * method, to ensure that the Singleton is initialized before uses
     */
    private static init() {
        if (!NewMessage.isInit) {
            $("body").append(Handlebars.templates[NewMessage.NAME + ".hb"]());
            $("#" + NewMessage.NAME + "-OK").click(NewMessage.addMessage);
            $("#" + NewMessage.NAME + "-Close").click(NewMessage.hide);
            NewMessage.isInit = true;
        }
    }

    /**
     * Refresh() doesn't really have much meaning, but just like in sNavbar, we
     * have a refresh() method so that we don't have front-end code calling
     * init().
     */
    public static refresh() {
        NewMessage.init();
    }

    /**
     * Hide the NewMessage.  Be sure to clear its fields first
     */
    private static hide() {
        //$("#" + NewMessage.NAME + "-title").val("");
        $("#" + NewMessage.NAME + "-message").val("");
        $("#" + NewMessage.NAME).modal("hide");
    }

    /**
     * Show the NewMessage.  Be sure to clear its fields, because there are
     * ways of making a Bootstrap modal disapper without clicking Close, and
     * we haven't set up the hooks to clear the fields on the events associated
     * with those ways of making the modal disappear.
     */
    public static show() {
        //$("#" + NewMessage.NAME + "-title").val("");
        $("#" + NewMessage.NAME + "-message").val("");
        $("#" + NewMessage.NAME).modal("show");
    }


    /**
     * Send data to submit the form only if the fields are both valid.  
     * Immediately hide the form when we send data, so that the user knows that 
     * their click was received.
     */
    public static addMessage() {
        // get the values of the two fields, force them to be strings, and check 
        // that neither is empty
        let msg = "" + $("#" + NewMessage.NAME + "-message").val();
        if (msg === "") {
            window.alert("Error: message is not valid");
            return;
        }
        NewMessage.hide();
        // set up an AJAX post.  When the server replies, the result will go to
        // onSubmitResponse
        $.ajax({
            type: "POST",
            url: backendUrl + "/addMessage/",
            dataType: "json",
            data: JSON.stringify({ mMessage: msg }),
            success: NewMessage.onSubmitResponse
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
            Message.refresh();
        }
        // Handle explicit errors with a detailed popup message
        else if (data.mStatus === "error") {
            window.alert("The server replied with an error:\n" + data.mMessage);
        }
        // Handle other errors with a less-detailed popup message
        else {
            window.alert("Unspecified error");
        }
    }
}