/**
 * NewEntryForm encapsulates all of the code for the form for adding an entry
 */

class NewEntryForm {


    /**
     * The name of the DOM entry associated with NewEntryForm
     */
    public static readonly NAME = "NewEntryForm";

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
        if (!NewEntryForm.isInit) {
            $("body").append(Handlebars.templates[NewEntryForm.NAME + ".hb"]());
            //$("#" + "Upload").click(NewEntryForm.submitForm);
            $("#" + NewEntryForm.NAME + "-Location").click(NewEntryForm.addLocation);
            $("#" + NewEntryForm.NAME + "-OK").click(NewEntryForm.submitForm);
            NewEntryForm.isInit = true;
        }
    }

    /**
     * Refresh() doesn't really have much meaning, but just like in sNavbar, we
     * have a refresh() method so that we don't have front-end code calling
     * init().
     */
    public static refresh() {
        NewEntryForm.init();
    }

    /**
     * add location for meessage which return latitude and longtitude 
     */
    private static addLocation(){
        console.log("getting new location");
        mMap.show_2();
    }
    /**
     * Send data to submit the form only if the fields are both valid.  
     * Immediately hide the form when we send data, so that the user knows that 
     * their click was received.
     */
    private static submitForm() {
        var stringFile;
        var fileName = "";
        var key = 0;
        //check if user logout
        if (session_key === "") {
            console.log("ElementList: refresh: user isn't logged in");
            Login.hideMainPage();
            return;
        }

        // get the values of the two fields, force them to be strings, and check 
        // that neither is empty
        let msg = "" + $("#" + NewEntryForm.NAME + "-message").val();
        let link = "" + $("#" + NewEntryForm.NAME + "-link").val();
        console.log("Link: ",link);
        if ( msg === "") {
            window.alert("Error: title or message is not valid");
            return;
        }
        console.log("NewEntryForm: submitting form with msg = " + msg);
        if($("#Upload")[0].files.length === 1){
            var file = $("#Upload")[0].files[0];
            fileName = file.name;
            var reader = new FileReader();
            reader.onload = function () {
                //console.log(reader.result);
                stringFile = reader.result!.toString().split(",")[1];
                // set up an AJAX post.  When the server replies, the result will go to
                // onSubmitResponse
                $.ajax({
                    type: "POST",
                    url: "/messages",
                    dataType: "json",
                    headers: { "Authorization": session_key },
                    data: JSON.stringify({ uid: user_id, mMessage: msg, mlink:link, img: "", mfileID: "", fileName: fileName, file: stringFile, latitude: latitude, longitude: longtitude}),
                    success: NewEntryForm.onSubmitResponse
                });
              };
              reader.onerror = function (error) {
                console.log('Error: ', error);
              };
            reader.readAsDataURL(file);
            }
        else{
            // set up an AJAX post.  When the server replies, the result will go to
            // onSubmitResponse
            $.ajax({
                type: "POST",
                url: "/messages",
                dataType: "json",
                headers: { "Authorization": session_key },
                data: JSON.stringify({ uid: user_id, mMessage: msg, img: "", mlink:link, mfileID: "", fileName: "", file: "", latitude: latitude, longitude: longtitude}),
                success: NewEntryForm.onSubmitResponse
            });
        }
    }

    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a 
     * result.
     * 
     * @param data The object returned by the server
     */
    private static onSubmitResponse(data: any) {

        console.log("NewEntryForm.onSubmitResponse: status = " + data.mStatus);

        if (data.mStatus === "ok") {
            ElementList.refresh();
            $("#" + NewEntryForm.NAME + "-message").val("");
            $("#" + NewEntryForm.NAME + "-link").val("");
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
    }
}
