/// <reference path="ts/EditEntryForm.ts"/>
/// <reference path="ts/NewEntryForm.ts"/>
/// <reference path="ts/ElementList.ts"/>
/// <reference path="ts/Login.ts"/>
/// <reference path="ts/Map.ts"/>

// Prevent compiler errors when using jQuery.  "$" will be given a type of 
// "any", so that we can use it anywhere, and assume it has any fields or
// methods, without the compiler producing an error.
var $: any;
var gapi: any;
var id_token: any = null;
var stringFile: any = "";
var test: number = 0;
var latitude: number = 360.0;
var longtitude: number = 360.0;
var LAT_VALUE: number = 40.607164238;
var LONG_VALUE: number = -75.378998484;
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
    mMap.refresh();
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

Handlebars.registerHelper('equal360', function(this:any, lvalue:any, options:any) {
    if (arguments.length < 2){
        throw new Error("Handlebars Helper equal needs 2 parameters");
    }
    if( lvalue==360.0 ) {
        return options.inverse(this);
    } else{
        return options.fn(this);
    }
  });

  


