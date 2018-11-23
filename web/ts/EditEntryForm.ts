

/**
 * EditEntryForm encapsulates all of the code for the form for adding an entry
 */
class EditEntryForm {

    /**
     * The name of the DOM entry associated with EditEntryForm
     */
    public static readonly NAME = "EditEntryForm";

    /**
     * Track if the Singleton has been initialized
     */
    private static isInit = false;

    private static id = -1;

    /**
     * Initialize the EditEntryForm by creating its element in the DOM and
     * configuring its buttons.  This needs to be called from any public static 
     * method, to ensure that the Singleton is initialized before use
     */
    private static init() {
        if (!EditEntryForm.isInit) {
            $("body").append(Handlebars.templates[EditEntryForm.NAME + ".hb"]());
            $("#" + EditEntryForm.NAME + "-OK").click(EditEntryForm.submitForm);
            $("#" + EditEntryForm.NAME + "-Close").click(EditEntryForm.hide);
            EditEntryForm.isInit = true;
        }
    }

    /**
     * Refresh() doesn't really have much meaning, but just like in sNavbar, we
     * have a refresh() method so that we don't have front-end code calling
     * init().
     */
    public static refresh() {
        EditEntryForm.init();
    }

    /**
     * Hide the EditEntryForm.  Be sure to clear its fields first
     */
    private static hide() {
        $("#" + EditEntryForm.NAME + "-message").val("");
        $("#" + EditEntryForm.NAME).modal("hide");
    }

    /**
     * Show the NewEntryForm.  Be sure to clear its fields, because there are
     * ways of making a Bootstrap modal disapper without clicking Close, and
     * we haven't set up the hooks to clear the fields on the events associated
     * with those ways of making the modal disappear.
     */
    public static show(data: any) {
        console.log("EditEntryForm: show is called with data = " + data + " mId = " + data.mData.mId);
        $("#" + EditEntryForm.NAME + "-message").val(data.mData.mMessage);
        $("#" + EditEntryForm.NAME).modal("show");
        EditEntryForm.id = data.mData.mId;
    }


    /**
     * Send data to submit the form only if the fields are both valid.  
     * Immediately hide the form when we send data, so that the user knows that 
     * their click was received.
     */
    private static submitForm() {

        //check if user logout
        if (session_key === "") {
            console.log("ElementList: refresh: user isn't logged in");
            Login.hideMainPage();
            return;
        }

        // get string from fields
        // and check string not empty
        let msg = "" + $("#" + EditEntryForm.NAME + "-message").val();
        if (msg === "") {
            window.alert("Error: title or message is not valid");
            return;
        }

        // hide the modal
        EditEntryForm.hide();
        console.log("EditEntryForm: requesting put to " + backendUrl + "/messages/" + EditEntryForm.id);
        
        // call PUT message to backend
        $.ajax({
            type: "PUT",
            url: backendUrl + "/messages/" + EditEntryForm.id,
            dataType: "json",
            headers: { "Authorization": session_key },
            data: JSON.stringify({ uid: user_id, mMessage: msg, img: "", mfileID: "" }),
            success: EditEntryForm.onSubmitResponse
        });

    }

    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a 
     * result.
     * 
     * @param data The object returned by the server
     */
    private static onSubmitResponse(data: any) {

        console.log("EditEntryForm.onSubmitResponse: status = " + data.mStatus);

        if (data.mStatus === "ok") {
            ElementList.refresh();
        }
        // Handle explicit errors with a detailed popup message
        else if (data.mStatus === "error") {
            window.alert("The server replied with an error:\n" + data.echoMessage);
            Login.hideMainPage();
        }
        // Handle other errors with a less-detailed popup message
        else {
            window.alert("Unspecified error");
        }
    }
}