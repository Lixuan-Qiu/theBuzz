/// <reference path="ts/EditEntryForm.ts"/>
/// <reference path="ts/NewEntryForm.ts"/>
/// <reference path="ts/ElementList.ts"/>
/// <reference path="ts/Login.ts"/>

// Prevent compiler errors when using jQuery.  "$" will be given a type of 
// "any", so that we can use it anywhere, and assume it has any fields or
// methods, without the compiler producing an error.
var $: any;
var gapi: any;
var id_token: any = null;
var stringFile: any = "";
var test: number = 0;

/// This constant indicates the path to our backend server
const backendUrl = "https://agile-plateau-21593.herokuapp.com";

let Handlebars: any;
let user_id: number = -1;
let session_key: String = "";






// Run some configuration code when the web page loads
$(document).ready(function () {

    ElementList.refresh();
    NewEntryForm.refresh();
    EditEntryForm.refresh();
    Login.refresh();

});

Handlebars.registerHelper('equaluId', function(this:any, lvalue:any, options:any) {
  if (arguments.length < 2){
      throw new Error("Handlebars Helper equal needs 2 parameters");
  }
  if( lvalue!=user_id ) {
      return options.inverse(this);
  } else{
      return options.fn(this);
  }
});


