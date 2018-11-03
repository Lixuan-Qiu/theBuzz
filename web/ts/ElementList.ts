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


    }

    public static refresh() {
        console.log("ElementList: refresh");
        // Make sure the ElementList is initialized
        ElementList.init();

        //check if user logout
        if(user_id === -1 || session_key === -1){
            Login.hideMainPage();
        }
        else {
            // get message list
            $.ajax({
                type: "GET",
                url: "/messages",
                dataType: "json",
                data: JSON.stringify({ uid: user_id, key: session_key }),
                success: ElementList.update
            });
        }
    }

    /**
     * clickDelete is the code we run in response to a click of a delete button
     */
    private static clickDelete() {
        // for now, just print the ID that goes along with the data in the row
        // whose "delete" button was clicked
        let id = $(this).data("value");
        $.ajax({
            type: "DELETE",
            url: "/messages/" + id,
            dataType: "json",
            data: JSON.stringify({ uid: user_id, key: session_key }),
            // TODO: we should really have a function that looks at the return
            //       value and possibly prints an error message.
            success: ElementList.onSubmitResponse
        });
    }

    /**
     * clickEdit is the code we run in response to a click of a delete button
     */
    private static clickEdit() {
        // as in clickDelete, we need the ID of the row
        let id = $(this).data("value");
        $.ajax({
            type: "GET",
            url: "/messages/" + id,
            dataType: "json",
            data: JSON.stringify({ uid: user_id, key: session_key }),
            success: EditEntryForm.show
        });
    }

    /**
     * clickLike is the code we run in response to a click of a like button
     */
    private static clickLike() {

        let id = $(this).data("value");
        $.ajax({
            type: "PUT",
            url: "/messages/" + id + "/like",
            dataType: "json",
            data: JSON.stringify({ uid: user_id, key: session_key }),
            success: ElementList.onSubmitResponse
        });
    }

    /**
     * clickDislike is the code we run in response to a click of a dislike button
     */
    private static clickDislike() {

        let id = $(this).data("value");
        $.ajax({
            type: "PUT",
            url: "/messages/" + id + "/dislike",
            dataType: "json",
            data: JSON.stringify({ uid: user_id, key: session_key }),
            success: ElementList.onSubmitResponse
        });
    }

    private static clickLogout() {

        $.ajax({
            type: "POST",
            url: "/logout",
            dataType: "json",
            data: JSON.stringify({ uid: user_id, key: session_key }),
            success: ElementList.onSubmitResponse
        });

        user_id = -1;
        session_key = -1;
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