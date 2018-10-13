/// <reference path="EditMessage.ts"/>
/// <reference path="../app.ts"/>
/// <reference path="MessageList.ts"/>
//var $: any;
var login: Login;

class Login {
     /**
     * The name of the DOM entry associated with Login
     */
    private static readonly NAME = "Login";
    /**
     * Track if the Singleton has been initialized
     */
    private static isInit = false;

    /**
     * Initialize the Message singleton by creating its element in the DOM.
     * This needs to be called from any public static method, to ensure that the 
     * Singleton is initialized before use.
     */
    private static init() {
        console.log("test from login");
        if (!Login.isInit) {
            $("body").append('<div id="123"><button id="' + Login.NAME + '-Login">Login</button><div id="');
            Login.isInit = true;
            $("#" + Login.NAME).click(this.submit);
        }
    }
    /**
     * To initialize the object, we say what method of EditMessage should be
     * run in response to each of the form's buttons being clicked.
     */
    constructor() {
        //$("#login").click(this.submit);
        //$("#editDislike").click(this.clickDislike);
    }

    /**
     * init() is called from an AJAX GET, and should populate the form if and 
     * only if the GET did not have an error
     */
    

    /**
    * refresh() is the public method for updating the Message
    */
   public static refresh() {
    // Make sure the singleton is initialized
    Login.init();
    
}


/**
* Send data to submit the form only if the fields are both valid.  
* Immediately hide the form when we send data, so that the user knows that 
* their click was received.
*/
private static submit() {
    // get the values of the two fields, force them to be strings, and check 
    // that neither is empty
    $("addElement").hide();
    $("showElements").hide();
    $("loginform").show();
    console.log("login");
    let user = $("#username").val();
    let pass = $("#password").val();
    console.log(user);
    console.log(pass);
    if (user === "") {
        window.alert("Error: username is not valid");
        return;
    }
    if (pass === "") {
        window.alert("Error: password is not valid");
        return;
    }
    // clear the input box so the user knows his input was taken
    $("#username").val("");
    $("#password").val("");
    // set up an AJAX post.  When the server replies, the result will go to
    // onSubmitResponse
    /*$.ajax({
        type: "POST",
        url:  "/addMessage",
        dataType: "json",
        data: JSON.stringify({ mMessage: msg }),
        success: Message.onSubmitResponse
    });*/
    
        $.ajax({
            type: "POST", 
            url: "/login",
            dataType: "json",
            data:JSON.stringify({uUsername: user, uPassword: pass}),
            success: Login.onSubmitResponse,
            error: Login.onFail
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
            console.log(data.sessionkey);
            window.alert(data.echoMessage);
            var userkey = data.mData;
            //after successful login, allow add message
            NewMessage.refresh();
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

    private static onFail(data: any){
        // Handle explicit errors with a detailed popup message
        if (data.mStatus === "error") {
            window.alert("The server replied with an error:\n" + data.echoMessage);
            Login.refresh();
        }
    }
    /**
     * Invoked when user clicks "login" button
     */
    /*clickSubmit() {
        
        let id = $(this).attr("value");
        console.log(id + "is liked");
        $.ajax({
            type: "POST", 
            url: "/login/"+id+"/like",
            dataType: "json",
            data: JSON.stringify({ uUsername: user, uPassword: pass}),
            success: Message.refresh()
        });
    }*/
}