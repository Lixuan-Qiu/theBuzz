/// <reference path="ts/EditEntryForm.ts"/>
/// <reference path="ts/NewEntryForm.ts"/>
/// <reference path="ts/ElementList.ts"/>

// Prevent compiler errors when using jQuery.  "$" will be given a type of 
// "any", so that we can use it anywhere, and assume it has any fields or
// methods, without the compiler producing an error.
var $: any;

/// This constant indicates the path to our backend server
const backendUrl = "https://agile-plateau-21593.herokuapp.com";

let Handlebars: any;





// Run some configuration code when the web page loads
$(document).ready(function () {
    // Create the object that controls the "New Entry" form
    
    // Create the object for the main data list, and populate it with data from
    // the server
    ElementList.refresh();

    NewEntryForm.refresh();
    

    // Create the object that controls the "Edit Entry" form
    EditEntryForm.refresh();

    // set up initial UI state
    $("#addElement").hide();


});