/// <reference path="EditMessage.ts"/>
/// <reference path="../app.ts"/>

var $: any;
var editMessage: EditMessage;
var msg: Message;
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
        console.log("test from msg");
        if (!Message.isInit) {
            $("body").append('<div id="123"><button id="' + Message.NAME + '-showFormButton">Add Message</button><div id="' + Message.NAME + '-messageList"></div></div>');
            Message.isInit = true;
            $("#" + Message.NAME + "-showFormButton").click(this.addMessage);
        }
    }

    /**
    * refresh() is the public method for updating the Message
    */
    public static refresh() {
        // Make sure the singleton is initialized
        Message.init();
        // Issue a GET, and then pass the result to update()
        $.ajax({
            type: "GET",
            url: "/messages",
            dataType: "json",
            success: Message.update
        });
    }

/**
 * update() is the private method used by refresh() to update the 
 * Message
 */
private static update(data: any) {
    console.log(data);
    console.log(data.mData);
    console.log(data.mStatus);
    console.log(data.echoMessage);
    $("#messages tr").remove();

    // create the new table of data
    let newHTML = '<div id="messages"><table style="width:100%" align="left"><tr><th>Messages</th><th>Likes</th></tr>';
    for (let i = 0; i < data.mData.length; i++) {
        newHTML += "<tr><td>" + data.mData[i].mMessage + "</td>";
        newHTML += "<td>" + data.mData[i].mlikeCount + "</td>";
        newHTML += '<td><input type="button" value="Like" class="Message-likebtn" id="{{this.mId}}"></td></tr>';
        // $("#" + Message.NAME + "-likebtn").click(editMessage.clickLike);
    }
    newHTML += "</table></div>";
    $(".Message-likebtn").click(editMessage.clickLike);
    $("body").append(newHTML);
    // $("body").append($("#Message"));
    // $("#" + Message.NAME + "-showFormButton").click(NewMessage.addMessage);
    // replace the contents of the messageList with our table
    // Find all of the like buttons, and set their behavior
    // $("." + Message.NAME + "-likebtn").click(editMessage.clickLike);

    // Find all of the dislike buttons, and set their behavior
    //$("." + Message.NAME + "-dislikebtn").click(editMessage.clickDislike);

    // // Find all of the delete buttons, and set their behavior
    // $("." + Message.NAME + "-delbtn").click(Message.clickDelete);
    // // Find all of the Edit buttons, and set their behavior
    // $("." + Message.NAME + "-editbtn").click(Message.clickEdit);
}

/**
* Send data to submit the form only if the fields are both valid.  
* Immediately hide the form when we send data, so that the user knows that 
* their click was received.
*/
private static addMessage() {
    // get the values of the two fields, force them to be strings, and check 
    // that neither is empty
    $("#Message").hide();
    $("body").show();
    console.log("addMessage");
    let msg = $("#NewMessage-message").val();
    console.log(msg);
    if (msg === "") {
        window.alert("Error: message is not valid");
        return;
    }
    // clear the input box so the user knows his input was taken
    $("#NewMessage-message").val("");
    // set up an AJAX post.  When the server replies, the result will go to
    // onSubmitResponse
    $.ajax({
        type: "POST",
        url:  "/addMessage",
        dataType: "json",
        data: JSON.stringify({ mMessage: msg }),
        success: Message.onSubmitResponse
    });
}

    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a 
     * result.
     * 
     * @param data The object returned by the server
     */
    private static onSubmitResponse(data: any) {
        console.log("message successfully sent");
        // If we get an "ok" message, clear the form and refresh the main 
        // listing of messages
        if (data.mStatus === "ok") {
            Message.refresh();
        }
        // Handle explicit errors with a detailed popup message
        else if (data.mStatus === "error") {
            window.alert("The server replied with an error:\n" + data.mMessage);
        }
        // Handle other errors with a less-detailed popup message
        else {
            window.alert("Unspecified error");
        }
    }

// /**
//  * buttons() creates 'edit' and 'delete' buttons for an id, and puts them in
//  * a TD
//  */
// private static buttons(id: string): string {
//     return "<td><button class='" + Message.NAME +
//         "-likebtn' data-value='" + id + "'>like</button></td>" +
//         "<td><button class='" + Message.NAME +
//         "-dislikebtn' data-value='" + id + "'>dislike</button></td>";
// }
}