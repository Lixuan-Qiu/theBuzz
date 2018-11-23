"use strict";
/**
 * EditEntryForm encapsulates all of the code for the form for adding an entry
 */
var EditEntryForm = /** @class */ (function () {
    function EditEntryForm() {
    }
    /**
     * Initialize the EditEntryForm by creating its element in the DOM and
     * configuring its buttons.  This needs to be called from any public static
     * method, to ensure that the Singleton is initialized before use
     */
    EditEntryForm.init = function () {
        if (!EditEntryForm.isInit) {
            $("body").append(Handlebars.templates[EditEntryForm.NAME + ".hb"]());
            $("#" + EditEntryForm.NAME + "-OK").click(EditEntryForm.submitForm);
            $("#" + EditEntryForm.NAME + "-Close").click(EditEntryForm.hide);
            EditEntryForm.isInit = true;
        }
    };
    /**
     * Refresh() doesn't really have much meaning, but just like in sNavbar, we
     * have a refresh() method so that we don't have front-end code calling
     * init().
     */
    EditEntryForm.refresh = function () {
        EditEntryForm.init();
    };
    /**
     * Hide the EditEntryForm.  Be sure to clear its fields first
     */
    EditEntryForm.hide = function () {
        $("#" + EditEntryForm.NAME + "-message").val("");
        $("#" + EditEntryForm.NAME).modal("hide");
    };
    /**
     * Show the NewEntryForm.  Be sure to clear its fields, because there are
     * ways of making a Bootstrap modal disapper without clicking Close, and
     * we haven't set up the hooks to clear the fields on the events associated
     * with those ways of making the modal disappear.
     */
    EditEntryForm.show = function (data) {
        console.log("EditEntryForm: show is called with data = " + data + " mId = " + data.mData.mId);
        $("#" + EditEntryForm.NAME + "-message").val(data.mData.mMessage);
        $("#" + EditEntryForm.NAME).modal("show");
        EditEntryForm.id = data.mData.mId;
    };
    /**
     * Send data to submit the form only if the fields are both valid.
     * Immediately hide the form when we send data, so that the user knows that
     * their click was received.
     */
    EditEntryForm.submitForm = function () {
        //check if user logout
        if (session_key === "") {
            console.log("ElementList: refresh: user isn't logged in");
            Login.hideMainPage();
            return;
        }
        // get string from fields
        // and check string not empty
        var msg = "" + $("#" + EditEntryForm.NAME + "-message").val();
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
    };
    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a
     * result.
     *
     * @param data The object returned by the server
     */
    EditEntryForm.onSubmitResponse = function (data) {
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
    };
    /**
     * The name of the DOM entry associated with EditEntryForm
     */
    EditEntryForm.NAME = "EditEntryForm";
    /**
     * Track if the Singleton has been initialized
     */
    EditEntryForm.isInit = false;
    EditEntryForm.id = -1;
    return EditEntryForm;
}());
/**
 * NewEntryForm encapsulates all of the code for the form for adding an entry
 */
var NewEntryForm = /** @class */ (function () {
    function NewEntryForm() {
    }
    /**
     * Initialize the NewEntryForm by creating its element in the DOM and
     * configuring its buttons.  This needs to be called from any public static
     * method, to ensure that the Singleton is initialized before use
     */
    NewEntryForm.init = function () {
        if (!NewEntryForm.isInit) {
            $("body").append(Handlebars.templates[NewEntryForm.NAME + ".hb"]());
            //$("#" + "Upload").click(NewEntryForm.submitForm);
            $("#" + NewEntryForm.NAME + "-OK").click(NewEntryForm.submitForm);
            NewEntryForm.isInit = true;
        }
    };
    /**
     * Refresh() doesn't really have much meaning, but just like in sNavbar, we
     * have a refresh() method so that we don't have front-end code calling
     * init().
     */
    NewEntryForm.refresh = function () {
        NewEntryForm.init();
    };
    /**
     * Send data to submit the form only if the fields are both valid.
     * Immediately hide the form when we send data, so that the user knows that
     * their click was received.
     */
    NewEntryForm.submitForm = function () {
        var stringFile;
        var fileName = "";
        var key = 0;
        //check if user logout
        if (session_key === "") {
            console.log("ElementList: refresh: user isn't logged in");
            Login.hideMainPage();
            return;
        }
        function onChange(event) {
            var file = event.target.files[0];
            fileName = file.name;
            var reader = new FileReader();
            reader.onload = function (e) {
                // The file's text will be printed here
                console.log(event.target.result.toString().split(",")[1]);
            };
            reader.readAsDataURL(file);
        }
        // get the values of the two fields, force them to be strings, and check 
        // that neither is empty
        var msg = "" + $("#" + NewEntryForm.NAME + "-message").val();
        if (msg === "") {
            window.alert("Error: title or message is not valid");
            return;
        }
        if ($("#Upload")[0].files.length === 1) {
            var stringName = $("#Upload")[0];
        }
        console.log("NewEntryForm: submitting form with msg = " + msg);
        // set up an AJAX post.  When the server replies, the result will go to
        // onSubmitResponse
        $.ajax({
            type: "POST",
            url: "/messages",
            dataType: "json",
            headers: { "Authorization": session_key },
            data: JSON.stringify({ uid: user_id, key: key, mMessage: msg, img: "", mfileID: "", fileName: fileName, file: stringFile }),
            success: NewEntryForm.onSubmitResponse
        });
    };
    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a
     * result.
     *
     * @param data The object returned by the server
     */
    NewEntryForm.onSubmitResponse = function (data) {
        console.log("NewEntryForm.onSubmitResponse: status = " + data.mStatus);
        if (data.mStatus === "ok") {
            ElementList.refresh();
            $("#" + NewEntryForm.NAME + "-message").val("");
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
    };
    /**
     * The name of the DOM entry associated with NewEntryForm
     */
    NewEntryForm.NAME = "NewEntryForm";
    /**
     * Track if the Singleton has been initialized
     */
    NewEntryForm.isInit = false;
    return NewEntryForm;
}());
/**
 * The ElementList Singleton provides a way of displaying all of the data
 * stored on the server as an HTML table.
 */
var ElementList = /** @class */ (function () {
    function ElementList() {
    }
    /**
    * Initialize the ElementList singleton.
    * This needs to be called from any public static method, to ensure that the
    * Singleton is initialized before use.
    */
    ElementList.init = function () {
        if (!ElementList.isInit) {
            ElementList.isInit = true;
        }
    };
    /**
     * update() is the private method used by refresh() to update the
     * ElementList
     */
    ElementList.update = function (data) {
        console.log("ElementList: update");
        // Remove the table of data, if it exists
        $("#" + ElementList.NAME).remove();
        // Use a template to re-generate the table, and then insert it
        $("body").append(Handlebars.templates[ElementList.NAME + ".hb"](data));
        // Find all of the delete buttons, and set their behavior
        $("." + ElementList.NAME + "-delbtn").click(ElementList.clickDelete);
        // Find all of the Edit buttons, and set their behavior
        $("." + ElementList.NAME + "-editbtn").click(ElementList.clickEdit);
        // Find all of the delete buttons, and set their behavior
        $("." + ElementList.NAME + "-likebtn").click(ElementList.clickLike);
        // Find all of the Edit buttons, and set their behavior
        $("." + ElementList.NAME + "-dislikebtn").click(ElementList.clickDislike);
        $("#" + ElementList.NAME + "-logoutbtn").click(ElementList.clickLogout);
    };
    ElementList.refresh = function () {
        console.log("ElementList: refresh");
        // Make sure the ElementList is initialized
        ElementList.init();
        //check if user logout
        if (session_key === "") {
            console.log("ElementList: refresh: user isn't logged in");
            Login.hideMainPage();
            return;
        }
        // get message list
        $.ajax({
            type: "GET",
            url: "/messages",
            dataType: "json",
            headers: { "Authorization": session_key },
            success: ElementList.update
        });
    };
    /**
     * clickDelete is the code we run in response to a click of a delete button
     */
    ElementList.clickDelete = function () {
        //check if user logout
        if (session_key === "") {
            console.log("ElementList: clickDelete: user isn't logged in");
            Login.hideMainPage();
            return;
        }
        var id = $(this).data("value");
        $.ajax({
            type: "DELETE",
            url: "/messages/" + id,
            dataType: "json",
            headers: { "Authorization": session_key },
            data: JSON.stringify({ uid: user_id }),
            // TODO: we should really have a function that looks at the return
            //       value and possibly prints an error message.
            success: ElementList.onSubmitResponse
        });
    };
    /**
     * clickEdit is the code we run in response to a click of a delete button
     */
    ElementList.clickEdit = function () {
        if (session_key === "") {
            console.log("ElementList: refresh: user isn't logged in");
            Login.hideMainPage();
            return;
        }
        var id = $(this).data("value");
        $.ajax({
            type: "GET",
            url: "/messages/" + id,
            dataType: "json",
            headers: { "Authorization": session_key },
            success: EditEntryForm.show
        });
    };
    /**
     * clickLike is the code we run in response to a click of a like button
     */
    ElementList.clickLike = function () {
        if (session_key === "") {
            console.log("ElementList: refresh: user isn't logged in");
            Login.hideMainPage();
            return;
        }
        var id = $(this).data("value");
        $.ajax({
            type: "PUT",
            url: "/messages/" + id + "/like",
            dataType: "json",
            headers: { "Authorization": session_key },
            data: JSON.stringify({ uid: user_id }),
            success: ElementList.onSubmitResponse
        });
    };
    /**
     * clickDislike is the code we run in response to a click of a dislike button
     */
    ElementList.clickDislike = function () {
        if (session_key === "") {
            console.log("ElementList: refresh: user isn't logged in");
            Login.hideMainPage();
            return;
        }
        var id = $(this).data("value");
        $.ajax({
            type: "PUT",
            url: "/messages/" + id + "/dislike",
            dataType: "json",
            headers: { "Authorization": session_key },
            data: JSON.stringify({ uid: user_id }),
            success: ElementList.onSubmitResponse
        });
    };
    ElementList.clickLogout = function () {
        var auth2 = gapi.auth2.getAuthInstance();
        auth2.signOut();
        console.log('User signed out.');
        test = -1;
        session_key = "";
    };
    ElementList.onSubmitResponse = function (data) {
        console.log("ElementList.onSubmitResponse: status = " + data.mStatus);
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
    };
    /**
     * The name of the DOM entry associated with ElementList
     */
    ElementList.NAME = "ElementList";
    /**
     * Track if the Singleton has been initialized
     */
    ElementList.isInit = false;
    return ElementList;
}());
/**
 * Login encapsulates all of the code for the form for adding an entry
 */
var Login = /** @class */ (function () {
    function Login() {
    }
    /**
     * Initialize the Login by creating its element in the DOM and
     * configuring its buttons.  This needs to be called from any public static
     * method, to ensure that the Singleton is initialized before use
     */
    Login.init = function () {
        if (!Login.isInit) {
            Login.isInit = true;
        }
    };
    /**
     * Refresh() doesn't really have much meaning, but just like in sNavbar, we
     * have a refresh() method so that we don't have front-end code calling
     * init().
     */
    Login.refresh = function () {
        Login.init();
    };
    /**
     * Send data to submit the form only if the fields are both valid.
     * Immediately hide the form when we send data, so that the user knows that
     * their click was received.
     */
    Login.submitForm = function () {
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
            data: JSON.stringify({ id_token: id_token }),
            success: Login.onSubmitResponse
        });
    };
    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a
     * result.
     *
     * @param data The object returned by the server
     */
    Login.onSubmitResponse = function (data) {
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
    };
    Login.callMainPage = function () {
        console.log("Login: call main page");
        $("#" + Login.NAME + "-container").hide();
        $("#" + ElementList.NAME).show();
        $("#" + NewEntryForm.NAME).show();
    };
    Login.hideMainPage = function () {
        console.log("Login: hide main page");
        $("#" + Login.NAME + "-container").show();
        $("#" + ElementList.NAME).hide();
        $("#" + NewEntryForm.NAME).hide();
        session_key = "";
    };
    /**
     * The name of the DOM entry associated with Login
     */
    Login.NAME = "Login";
    /**
     * Track if the Singleton has been initialized
     */
    Login.isInit = false;
    return Login;
}());
/// <reference path="ts/EditEntryForm.ts"/>
/// <reference path="ts/NewEntryForm.ts"/>
/// <reference path="ts/ElementList.ts"/>
/// <reference path="ts/Login.ts"/>
// Prevent compiler errors when using jQuery.  "$" will be given a type of 
// "any", so that we can use it anywhere, and assume it has any fields or
// methods, without the compiler producing an error.
var $;
var gapi;
var id_token = null;
var test = 0;
/// This constant indicates the path to our backend server
var backendUrl = "https://agile-plateau-21593.herokuapp.com";
var Handlebars;
var user_id = -1;
var session_key = "";
// Run some configuration code when the web page loads
$(document).ready(function () {
    ElementList.refresh();
    NewEntryForm.refresh();
    EditEntryForm.refresh();
    Login.refresh();
});
function onSignIn(googleUser) {

    var profile = googleUser.getBasicProfile();
    // console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
    // console.log('Name: ' + profile.getName());
    // console.log('Image URL: ' + profile.getImageUrl());
    // console.log('Email: ' + profile.getEmail()); // This is null if the 'email' scope is not present.

    id_token = googleUser.getAuthResponse().id_token;
    // console.log("ID Token: " + id_token);
    // console.log("test: " + test);
    // test = 1;
    // console.log("test: " + test);
    Login.submitForm();
}
