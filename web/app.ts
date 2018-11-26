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

var callback = function(fileData:any){
    stringFile = fileData;
    console.log("File",stringFile);
}

function uploadFile() {
    var file = $("#Upload")[0].files[0];
    var reader  = new FileReader();
    
    reader.addEventListener("load", function () {
      stringFile = reader.result!.toString().split(",")[1];
      callback(reader.result!.toString().split(",")[1]);
      
    }, false);

    if (file) {
      reader.readAsDataURL(file);
    }
  }


