/// <reference path="ts/Message.ts"/>
/// <reference path="ts/EditMessage.ts"/>

/// This constant indicates the path to our backend server
//const backendUrl = "https://agile-plateau-21593.herokuapp.com";
// Prevent compiler errors when using jQuery.  "$" will be given a type of 
// "any", so that we can use it anywhere, and assume it has any fields or
// methods, without the compiler producing an error.
var $: any;

// Prevent compiler errors when using Handlebars
var Handlebars: any;

// a global for the EditEntryForm of the program.  See newEntryForm for 
// explanation

// Run some configuration code when the web page loads
$(document).ready(function () {
    //Navbar.refresh();
    Message.refresh();
    //NewMessage.refresh();

    // Create the object that controls the "Edit Entry" form
    editMessage = new EditMessage();
    // set up initial UI state
    // $("#editElement").hide();
});