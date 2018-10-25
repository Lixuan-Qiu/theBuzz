/// <reference path="../app.ts"/>
/// <reference path="Navbar.ts"/>
/// <reference path="EditMessage.ts"/>
/// <reference path="NewMessage.ts"/>
/// <reference path="Login.ts"/>
/**
 * The MessageList Singleton provides a way of displaying all of the data 
 * stored on the server as an HTML table.
 */
var messageList: MessageList;

class MessageList {
    /**
     * The name of the DOM entry associated with MessageList
     */
    private static readonly NAME = "MessageList";

    /**
     * Track if the Singleton has been initialized
     */
    private static isInit = false;

    /**
     * Initialize the MessageList singleton by creating its element in the DOM.
     * This needs to be called from any public static method, to ensure that the 
     * Singleton is initialized before use.
     */
    private static init() {
        if (!MessageList.isInit) {
            $("body").append('<div id="' + MessageList.NAME +
                '"><h3>All Messages</h3><button id="' + MessageList.NAME +
                '-showFormButton">Add Message</button><div id="' +
                MessageList.NAME + '-messageList"></div></div>');
                MessageList.isInit = true;
        }
    }

    public static getUserId():number{
        return Login.userid;
    }

    public static getSessionKey():number{
        return Login.userkey;
    }

    /**
 * refresh() is the public method for updating the MessageList
 */
public static refresh() {
    // Make sure the singleton is initialized
    MessageList.init();
    console.log("message list refresh");
    //console.log(sample);
    // Issue a GET, and then pass the result to update()
    $.ajax({
        type: "GET",
        url: "/messages",
        dataType: "json",
        data: {uid: MessageList.getUserId(), key: MessageList.getSessionKey()},
        success: MessageList.update
    });
}

/**
 * update() is the private method used by refresh() to update the 
 * MessageList
 */
private static update(data: any) {
    console.log("messagelist update function");
    // create the new table of data
    let newHTML = "<table>";
    for (let i = 0; i < data.mData.length; ++i) {
        newHTML += "<tr><td>" + data.mData[i].mTitle + "</td>" +
        MessageList.buttons(data.mData[i].mId) + "</tr>";
    }
    newHTML += "</table>";
     // replace the contents of the messageList with our table
     $("#" + MessageList.NAME + "-messageList").html(newHTML);
    /*// replace the contents of the messageList with our table
    $("#" + MessageList.NAME).remove();
    // Use a template to re-generate the table, and then insert it
    $("body").append(Handlebars.templates[MessageList.NAME + ".hb"](data));
    */
    // Find all of the delete buttons, and set their behavior
    $("." + MessageList.NAME + "-delbtn").click(MessageList.clickDelete);
    // Find all of the Edit buttons, and set their behavior
    $("." + MessageList.NAME + "-editbtn").click(MessageList.clickEdit);
    // Find all of the like buttons, and set their behavior
    $("." + MessageList.NAME + "-likebtn").click(MessageList.clickLike);
    // Find all of the dislike buttons, and set their behavior
    $("." + MessageList.NAME + "-dislikebtn").click(MessageList.clickDislike);
}

/**
 * buttons() creates 'edit', 'delete', like, and dislike buttons for an id, and puts them in
 * a TD
 */
private static buttons(id: string): string {
    return "<td><button class='" + MessageList.NAME +
        "-editbtn' data-value='" + id + "'>Edit</button></td>" +
        "<td><button class='" + MessageList.NAME +
        "-delbtn' data-value='" + id + "'>Delete</button></td>" + 
        "-likebtn' data-value='" + id + "'>Like</button></td>" +
        "-dislikebtn' data-value='" + id + "'>Dislike</button></td>";
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
        url: "/messages/:" + id,
        dataType: "json",
        // TODO: we should really have a function that looks at the return
        //       value and possibly prints an error message.
        success: MessageList.refresh
    });
}

/**
 * clickEdit is the code we run in response to a click of a edit button
 */
private static clickEdit() {
    // as in clickDelete, we need the ID of the row
    let id = $(this).data("value");
    $.ajax({
        type: "GET",
        url: "/messages/:" + id,
        dataType: "json",
        success: editMessage.init
    });
}

private static clickLike() {
    // increase the count for likes with this request once user clicks the like button
    let id = $(this).data("value");
    $.ajax({
        type: "PUT",
        url: "/messages/:" + id + "/like",
        dataType: "json",
        //data:JSON.stringify({uId: username, key: userkey}),
        // TODO: we should really have a function that looks at the return
        //       value and possibly prints an error message.
        success: MessageList.refresh
    });
}

private static clickDislike() {
    // increase the count for dislikes with this request once user clicks the dislike button
    let id = $(this).data("value");
    $.ajax({
        type: "PUT",
        url: "/messages/:" + id + "/dislike",
        dataType: "json",
        // TODO: we should really have a function that looks at the return
        //       value and possibly prints an error message.
        success: MessageList.refresh
    });
}
}