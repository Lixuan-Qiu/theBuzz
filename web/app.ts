/// <reference path="ts/EditEntryForm.ts"/>
/// <reference path="ts/NewEntryForm.ts"/>
/// <reference path="ts/ElementList.ts"/>
/// <reference path="ts/Login.ts"/>

// Prevent compiler errors when using jQuery.  "$" will be given a type of 
// "any", so that we can use it anywhere, and assume it has any fields or
// methods, without the compiler producing an error.
var $: any;

/// This constant indicates the path to our backend server
const backendUrl = "https://agile-plateau-21593.herokuapp.com";

let Handlebars: any;
let user_id: number = -1;
let session_key: number = -1;





// Run some configuration code when the web page loads
$(document).ready(function () {

    ElementList.refresh();
    NewEntryForm.refresh();
    EditEntryForm.refresh();
    Login.refresh();

    Login.hideMainPage();


});


