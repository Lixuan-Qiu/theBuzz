<div class="panel panel-default" id="ElementList">
    <div class="panel-heading">
        <h3 class="panel-title">All Data</h3>
        <button id="ElementList-logoutbtn">logout</button>
    </div>
    <table class="table">
        <tbody>
            {{#each mData}}
            <tr>
                <td>{{this.mId}}</td>
                <td>{{this.mMessage}}</td>
                <td><button class="ElementList-editbtn" data-value="{{this.mId}}">Edit</button></td>
                <td><button class="ElementList-delbtn" data-value="{{this.mId}}">Delete</button></td>
                <td>
                    <button class="ElementList-likebtn" data-value="{{this.mId}}">Like</button>
                    <button class="ElementList-dislikebtn" data-value="{{this.mId}}">Dislike</button>
                </td>
            </tr>
            
            {{/each}}
        </tbody>
    </table>
</div>