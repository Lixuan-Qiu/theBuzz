/// <reference path="../app.ts"/>
/// <reference path="Navbar.ts"/>
/// <reference path="EditMessage.ts"/>
/// <reference path="MessageList.ts"/>
/// <reference path="NewMessage.ts"/>

// Prevent compiler errors when using jQuery.  "$" will be given a type of 
// "any", so that we can use it anywhere, and assume it has any fields or
// methods, without the compiler producing an error.
//let $: any;

// The 'this' keyword does not behave in JavaScript/TypeScript like it does in
// Java.  Since there is only one NewEntryForm, we will save it to a global, so
// that we can reference it from methods of the NewEntryForm in situations where
// 'this' won't work correctly.
var profile: Profile;

/**
 * NewEntryForm encapsulates all of the code for the form for adding an entry
 */
class Profile {

    /**
     * The name of the DOM entry associated with NewEntryForm
     */
    private static readonly NAME = "Profile";

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
        if (!Profile.isInit) {
                $("body").append('<div id="' + Profile.NAME +
                    '"><h1>Profile</h1></div>');
                Profile.isInit = true;
        }
    }

    /**
     * Refresh() doesn't really have much meaning, but just like in sNavbar, we
     * have a refresh() method so that we don't have front-end code calling
     * init().
     */
    public static refresh() {
        Profile.init();

        //get profile after log in 
        $.ajax({
            type: "GET",
            url: "/profile",
            dataType: "json",
            success: Profile.onParseResponse,
            error: Profile.onResponseFail
        });
    }

   

    /**
     * Send data to submit the form only if the fields are both valid.  
     * Immediately hide the form when we send data, so that the user knows that 
     * their click was received.
     */
    

    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a 
     * result.
     * 
     * @param data The object returned by the server
     */
    private static onParseResponse(data: any) {
        // If we get an "ok" message, clear the form and refresh the main 
        // listing of messages
        console.log("hello from");
        if (data.mStatus === "ok") {
            //var info = JSON.parse(data.mData);
            //mData is the returned array holding profile info
                $('#Profile').html('<h2>data.mData[0]</h2><p>data.mData[1]</p><h3>data.mData[2]</h3>');
            
            MessageList.refresh();
            //$("#editElement").show();
            
        }
        
    }

    //handle the error 
    private static onResponseFail(data: any){

        console.log("response failed");

        if(data.mStatus === "error"){
            window.alert("The server replied with an error:\n" + data.echoMessage);
        }
        else {
            window.alert("Unspecified error");
        }
    }
}