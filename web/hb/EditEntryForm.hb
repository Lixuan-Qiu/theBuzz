<div id="EditEntryForm" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Edit an Entry</h4>
            </div>
            <div class="modal-body">
                
                <label for="EditEntryForm-message">Message</label>
                <textarea class="form-control" id="EditEntryForm-message">{{data.mMessage}}</textarea>
                <td><input id="Edit-Upload" type="file" accept="application/pdf" /></td>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" id="EditEntryForm-OK">OK</button>
                <button type="button" class="btn btn-default" id="EditEntryForm-Close">Close</button>
            </div>
        </div>
    </div>
</div>