class Message {
     /**
     * The name of the DOM entry associated with Message
     */
    private static readonly NAME = "Message";

    /**
     * Track if the Singleton has been initialized
     */
    private static isInit = false;

    /**
     * Initialize the Message singleton by creating its element in the DOM.
     * This needs to be called from any public static method, to ensure that the 
     * Singleton is initialized before use.
     */
    private static init() {
        if (!Message.isInit) {
            $("body").append('<div id="' + Message.NAME +
                '"><h3>All Messages</h3><button id="' + Message.NAME +
                '-showFormButton">Add Message</button><div id="' +
                Message.NAME + '-messageList"></div></div>');
            Message.isInit = true;
        }
        $("#" + Message.NAME + "-showFormButton").click(NewMessage.addMessage());

    }

    /**
 * refresh() is the public method for updating the Message
 */
public refresh() {
    // Make sure the singleton is initialized
    Message.init();
    // Issue a GET, and then pass the result to update()
    $.ajax({
        type: "GET",
        url: backendUrl + "/messages",
        dataType: "json",
        success: Message.update
    });
}

/**
 * update() is the private method used by refresh() to update the 
 * Message
 */
private static update(data: any) {
    // create the new table of data
    let newHTML = "<table><tr><th>" + "Messages" + "</th>" + "<th>" + "Likes" + "</th></tr><tr>";
    for (let i = 0; i < data.mData.length; ++i) {
        newHTML += "<td>" + Message.buttons(data.mData[i].mMessage) + "</td>";
    }
    newHTML += "</tr><tr>"
    for (let i = 0; i < data.mData.length; ++i) {
        newHTML += "<td>" + Message.buttons(data.mData[i].mlikeCount) + "</td>";
    }
    newHTML += "</tr></table>";
    // replace the contents of the messageList with our table
    $("#" + Message.NAME + "-messageList").html(newHTML);
    // Find all of the like buttons, and set their behavior
    $("." + Message.NAME + "-likebtn").click(editMessage.clickLike);
    // Find all of the dislike buttons, and set their behavior
    //$("." + Message.NAME + "-dislikebtn").click(editMessage.clickDislike);

    // // Find all of the delete buttons, and set their behavior
    // $("." + Message.NAME + "-delbtn").click(Message.clickDelete);
    // // Find all of the Edit buttons, and set their behavior
    // $("." + Message.NAME + "-editbtn").click(Message.clickEdit);

}

/**
 * buttons() creates 'edit' and 'delete' buttons for an id, and puts them in
 * a TD
 */
private static buttons(id: string): string {
    return "<td><button class='" + Message.NAME +
        "-likebtn' data-value='" + id + "'>like</button></td>" +
        "<td><button class='" + Message.NAME +
        "-dislikebtn' data-value='" + id + "'>dislike</button></td>";
}
}