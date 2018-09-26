/**
 * EditMessage encapsulates all of the code for the form for editing an entry
 */
/// <reference path="NewMessage.ts"/>
/// <reference path="Message.ts"/>
var $: any;
var editMessage: EditMessage;
var message: Message;
var newMessage: NewMessage;
class EditMessage {
    $: any;
    /**
     * To initialize the object, we say what method of EditMessage should be
     * run in response to each of the form's buttons being clicked.
     */
    constructor() {
        $("#editLike").click(this.clickLike);
        //$("#editDislike").click(this.clickDislike);
    }

    /**
     * init() is called from an AJAX GET, and should populate the form if and 
     * only if the GET did not have an error
     */
    init(data: any) {
        if (data.mStatus === "ok") {
            //$("#editTitle").val(data.mData.mTitle);
            $("#editMessage").val(data.mData.mMessage);
            $("#editId").val(data.mData.mId);
            $("#editLike").val(data.mData.mlikeCount);
            //$("#editDislike").val(data.mData.mdislikeCount);
            // show the edit form
            // $("#addElement").hide();
            // $("#editElement").show();
            // $("#showElements").hide();
        }
        else if (data.mStatus === "error") {
            window.alert("Error: " + data.mMessage);
        }
        else {
            window.alert("An unspecified error occurred");
        }
    }

    /**
     * Invoked when user clicks "like" button
     */
    clickLike() {
        // as in clickDelete, we need the ID of the row
        let id = $(this).data("value");
        $.ajax({
            type: "PUT", 
            url: "/like/" + id,
            dataType: "json",
            success: Message.refresh()
        });
    }


    // /**
    //  * Invoked when user clicks "dislike" button
    //  */
    // clickDislike() {
    //     // as in clickDelete, we need the ID of the row
    //     let id = $(this).data("value");
    //     $.ajax({
    //         type: "PUT", 
    //         url: backendUrl + "/dislike/" + id,
    //         dataType: "json",
    //         success: message.refresh()
    //     });
    // }


    // /**
    //  * Clear the form's input fields
    //  */
    // clearForm() {
    //     $("#editTitle").val("");
    //     $("#editMessage").val("");
    //     $("#editId").val("");
    //     $("#editCreated").text("");
    //     // reset the UI
    //     $("#addElement").hide();
    //     $("#editElement").hide();
    //     $("#showElements").show();
    // }

    // /**
    //  * Check if the input fields are both valid, and if so, do an AJAX call.
    //  */
    // submitForm() {
    //     // get the values of the two fields, force them to be strings, and check 
    //     // that neither is empty
    //     let title = "" + $("#editTitle").val();
    //     let msg = "" + $("#editMessage").val();
    //     // NB: we assume that the user didn't modify the value of #editId
    //     let id = "" + $("#editId").val();
    //     if (title === "" || msg === "") {
    //         window.alert("Error: title or message is not valid");
    //         return;
    //     }
    //     // set up an AJAX post.  When the server replies, the result will go to
    //     // onSubmitResponse
    //     $.ajax({
    //         type: "PUT",
    //         url: backendUrl + "/messages/" + id,
    //         dataType: "json",
    //         data: JSON.stringify({ mTitle: title, mMessage: msg }),
    //         success: EditMessage.onSubmitResponse
    //     });
    // }

    // /**
    //  * onSubmitResponse runs when the AJAX call in submitForm() returns a 
    //  * result.
    //  * 
    //  * @param data The object returned by the server
    //  */
    // private onSubmitResponse(data: any) {
    //     // If we get an "ok" message, clear the form and refresh the main 
    //     // listing of messages
    //     if (data.mStatus === "ok") {
    //         EditMessage.clearForm();
    //         mainList.refresh();
    //     }
    //     // Handle explicit errors with a detailed popup message
    //     else if (data.mStatus === "error") {
    //         window.alert("The server replied with an error:\n" + data.mMessage);
    //     }
    //     // Handle other errors with a less-detailed popup message
    //     else {
    //         window.alert("Unspecified error");
    //     }
    // }
} // end class EditMessage