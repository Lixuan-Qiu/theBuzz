<div class="panel panel-default" id="ElementList">
    <div class="panel-heading">
        <h3 class="panel-title">All Data</h3>
        <button id="ElementList-logoutbtn">logout</button>
    </div>
    <table class="table">
        <tbody>
            {{#each mData}}
            <tr  id = "{{this.mId}}">
                <td>{{this.username}}</td>
                <td>{{this.mId}}</td>
                <td>{{this.mMessage}}</td>
                {{#if this.mLink}}
                <td><a href="#{{this.mLink}}">@{{this.mLink}}</a></td>
                {{/if}}
                <td><button class="ElementList-editbtn" data-value="{{this.mId}}">Edit</button></td>
                <td><button class="ElementList-delbtn" data-value="{{this.mId}}">Delete</button></td>
                <td>
                    <div class="btn-group" role="group" aria-label="...">
                        <button class="ElementList-likebtn" data-value="{{this.mId}}">Like</button>
                        <button class="ElementList-dislikebtn" data-value="{{this.mId}}">Dislike</button>
                    </div>
                </td>
                {{#if this.mimage}}
                    <td><img src="data:image/jpeg;base64,{{this.mimage}}" height="100" alt= "None"> </td>
                {{/if}}
                {{#if this.mfileid}}
                    <td><a id="{{this.mId}}filelink" href=""></a><td>
                    <td><button class="ElementList-getFilebtn" data-value="{{this.mId}}">Get File</button></td>
                {{/if}}
            </tr>
            {{/each}}
        </tbody>
    </table>
</div>