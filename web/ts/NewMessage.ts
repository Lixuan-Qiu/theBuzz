/// <reference path="../app.ts"/>
/// <reference path="Navbar.ts"/>
/// <reference path="EditMessage.ts"/>
/// <reference path="MessageList.ts"/>
/// <reference path="Login.ts"/>
// Prevent compiler errors when using jQuery.  "$" will be given a type of 
// "any", so that we can use it anywhere, and assume it has any fields or
// methods, without the compiler producing an error.
//let $: any;

// The 'this' keyword does not behave in JavaScript/TypeScript like it does in
// Java.  Since there is only one NewEntryForm, we will save it to a global, so
// that we can reference it from methods of the NewEntryForm in situations where
// 'this' won't work correctly.
var newMessage: NewMessage;

/**
 * NewEntryForm encapsulates all of the code for the form for adding an entry
 */

class NewMessage {
    
    /**
     * The name of the DOM entry associated with NewEntryForm
     */
    private static readonly NAME = "NewMessage";

    /**
     * Track if the Singleton has been initialized
     */
    private static isInit = false;

    /**
     * Initialize the NewEntryForm by creating its element in the DOM and 
     * configuring its buttons.  This needs to be called from any public static 
     * method, to ensure that the Singleton is initialized before use
     */
    private static init() {
        if (!NewMessage.isInit) {
            $("body").append(Handlebars.templates[NewMessage.NAME + ".hb"]());
            $("#" + NewMessage.NAME + "-OK").click(NewMessage.submitForm);
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
     * Hide the NewEntryForm.  Be sure to clear its fields first
     */
    private static hide() {
        $("#" + NewMessage.NAME + "-title").val("");
        $("#" + NewMessage.NAME + "-message").val("");
        $("#" + NewMessage.NAME).modal("hide");
    }

    /**
     * Show the NewEntryForm.  Be sure to clear its fields, because there are
     * ways of making a Bootstrap modal disapper without clicking Close, and
     * we haven't set up the hooks to clear the fields on the events associated
     * with those ways of making the modal disappear.
     */
    public static show() {
        $("#" + NewMessage.NAME + "-title").val("");
        $("#" + NewMessage.NAME + "-message").val("");
        $("#" + NewMessage.NAME).modal("show");
    }

    /*public getUserId():number{
        return Login.userid;
    }

    public getSessionKey():number{
        return Login.userkey;
    }*/

    /**
     * Send data to submit the form only if the fields are both valid.  
     * Immediately hide the form when we send data, so that the user knows that 
     * their click was received.
     */
    private static submitForm(data: any) {
        // get the values of the two fields, force them to be strings, and check 
        // that neither is empty
        let title = "" + $("#" + NewMessage.NAME + "-title").val();
        let msg = "" + $("#" + NewMessage.NAME + "-message").val();
        if (title === "" || msg === "") {
            window.alert("Error: title or message is not valid");
            return;
        }
        
        /*console.log(newMessage.getUserId());
        console.log(newMessage.getSessionKey());
        NewMessage.hide();
        // set up an AJAX post.  When the server replies, the result will go to
        // onSubmitResponse
        $.ajax({
            type: "POST",
            url: "/messages",
            dataType: "json",
            data: JSON.stringify({uid: newMessage.getUserId(), key: newMessage.getSessionKey(), mMessage: msg }),
            success: NewMessage.onSubmitResponse
        });*/
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
        console.log("hello from");
        if (data.mStatus === "ok") {
            console.log("it worked");
            NewMessage.refresh();
            //$("#editElement").show();
            
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