// <reference path="ts/NewMessage.ts"/>
// <reference path="ts/EditMessage.ts"/>
/// <reference path="ts/Login.ts"/>
/// <reference path="ts/EditMessage.ts"/>
/// <reference path="ts/NewMessage.ts"/>
/// <reference path="ts/MessageList.ts"/>
/// <reference path="ts/Navbar.ts"/>
/// This constant indicates the path to our backend server
//const backendUrl = "https://agile-plateau-21593.herokuapp.com";
// Prevent compiler errors when using jQuery.  "$" will be given a type of 
// "any", so that we can use it anywhere, and assume it has any fields or
// methods, without the compiler producing an error.

//const backendUrl = "https://guarded-oasis-49145.herokuapp.com";
// Prevent compiler errors when using Handlebars
let Handlebars: any;
let $: any;
// a global for the EditEntryForm of the program.  See newEntryForm for 
// explanation
var editMessage: EditMessage;
// Run some configuration code when the web page loads
$(document).ready(function () {
    Navbar.refresh();
    //NewMessage.refresh();
    //MessageList.refresh();
    Login.refresh();

    // Create the object that controls the "Login" form
    login = new Login();
    // Create the object that controls the "Edit Entry" form
    //editMessage = new EditMessage();
    newMessage = new NewMessage();
    editMessage = new EditMessage();
    messageList = new MessageList();
    // set up initial UI state
     $("#editElement").hide();
     
     //$("#showElements").hide();
     //$("#Login").click(function () {
        //$("#loginform").show();
        //$("#showElements").hide();
    //});
});