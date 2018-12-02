/**
 * The ElementList Singleton provides a way of displaying all of the data 
 * stored on the server as an HTML table.
 */
class ElementList {
    /**
     * The name of the DOM entry associated with ElementList
     */
    public static readonly NAME = "ElementList";

    /**
     * Track if the Singleton has been initialized
     */
    private static isInit = false;

    /**
    * Initialize the ElementList singleton.  
    * This needs to be called from any public static method, to ensure that the 
    * Singleton is initialized before use.
    */
    private static init() {
        if (!ElementList.isInit) {
            ElementList.isInit = true;
        }
    }

    /**
     * update() is the private method used by refresh() to update the 
     * ElementList
     */
    private static update(data: any) {
        console.log("ElementList: update");
        // Remove the table of data, if it exists
        $("#" + ElementList.NAME).remove();
        // Use a template to re-generate the table, and then insert it
        $("body").append(Handlebars.templates[ElementList.NAME + ".hb"](data));
        // Find all of the delete buttons, and set their behavior
        $("." + ElementList.NAME + "-delbtn").click(ElementList.clickDelete);
        // Find all of the Edit buttons, and set their behavior
        $("." + ElementList.NAME + "-editbtn").click(ElementList.clickEdit);
        // Find all of the Location buttons, and set their behavior
        $("." + ElementList.NAME + "-locationbtn").click(ElementList.clickLocation);
        // Find all of the delete buttons, and set their behavior
        $("." + ElementList.NAME + "-likebtn").click(ElementList.clickLike);
        // Find all of the Edit buttons, and set their behavior
        $("." + ElementList.NAME + "-dislikebtn").click(ElementList.clickDislike);

        $("#" + ElementList.NAME + "-logoutbtn").click(ElementList.clickLogout);

        $("." + ElementList.NAME + "-getFilebtn").click(ElementList.clickGetFile);

    }

    public static refresh() {
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

    }

    /**
     * clickDelete is the code we run in response to a click of a delete button
     */
    private static clickDelete() {

        //check if user logout
        if (session_key === "") {
            console.log("ElementList: clickDelete: user isn't logged in");
            Login.hideMainPage();
            return;
        }

        let id = $(this).data("value");
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


    }

    /**
     * clickLocation is the code we run in response to a click of a location button
     */
    private static clickLocation() {
        
                if (session_key === "") {
                    console.log("ElementList: refresh: user isn't logged in");
                    Login.hideMainPage();
                    return;
                }
        
                let id = $(this).data("value");
                $.ajax({
                    type: "GET",
                    url: "/messages/" + id,
                    dataType: "json",
                    headers: { "Authorization": session_key },
                    success: EditEntryForm.show
                });
        
            }
        

    /**
     * clickEdit is the code we run in response to a click of a delete button
     */
    private static clickEdit() {

        if (session_key === "") {
            console.log("ElementList: refresh: user isn't logged in");
            Login.hideMainPage();
            return;
        }

        let id = $(this).data("value");
        $.ajax({
            type: "GET",
            url: "/messages/" + id,
            dataType: "json",
            headers: { "Authorization": session_key },
            success: EditEntryForm.show
        });

    }

    /**
     * clickLike is the code we run in response to a click of a like button
     */
    private static clickLike() {

        if (session_key === "") {
            console.log("ElementList: refresh: user isn't logged in");
            Login.hideMainPage();
            return;
        }

        let id = $(this).data("value");
        $.ajax({
            type: "PUT",
            url: "/messages/" + id + "/like",
            dataType: "json",
            headers: { "Authorization": session_key },
            data: JSON.stringify({ uid: user_id }),
            success: ElementList.onSubmitResponse
        });

    }

    /**
     * clickDislike is the code we run in response to a click of a dislike button
     */
    private static clickDislike() {

        if (session_key === "") {
            console.log("ElementList: refresh: user isn't logged in");
            Login.hideMainPage();
            return;
        }

        let id = $(this).data("value");
        $.ajax({
            type: "PUT",
            url: "/messages/" + id + "/dislike",
            dataType: "json",
            headers: { "Authorization": session_key },
            data: JSON.stringify({ uid: user_id }),
            success: ElementList.onSubmitResponse
        });
    }
    
    /**
     * When the clickGetFile is clicked, a get is called to get the download link. Then the link is put into the 
     * message row.
     * //TODO get this function to run without clicking the button, it would looke better
     */
    public static clickGetFile() {

        if (session_key === "") {
            console.log("ElementList: refresh: user isn't logged in");
            Login.hideMainPage();
            return;
        }

        console.log("Getting File");    
        let id = $(this).data("value");
        $.ajax({
            type: "GET",
            url: "/messages/" + id + "/file",
            dataType: "json",
            headers: { "Authorization": session_key },
            success: function(data:any){
                console.log(data); 
                var link = document.getElementById(id + "filelink");
                link!.innerHTML = "Dowload Link";
                link!.setAttribute('href', data.mData);
                console.log("Link: ", data.mData);
                $("." + ElementList.NAME + "-getFilebtn").hide();
            },
        });

        

    }

    private static clickLogout() {

        var auth2 = gapi.auth2.getAuthInstance();
        auth2.signOut();

        console.log('User signed out.');

        test = -1;
        session_key = "";
        Login.hideMainPage();
    }

    private static onSubmitResponse(data: any) {

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
    }
}