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
                <td>
                    {{this.mMessage}}
                    {{#if this.mLink}}
                    <a href="#{{this.mLink}}">@{{this.mLink}}</a>
                    {{/if}}
                </td>
                <td>
                    Likes:{{this.mlikeCount}}
                    <div class="btn-group" role="group" aria-label="...">
                        <button class="ElementList-likebtn" data-value="{{this.mId}}">Like</button>
                        Dislikes:{{this.mdislikeCount}}
                        <button class="ElementList-dislikebtn" data-value="{{this.mId}}">Dislike</button>
                    </div>
                </td>
                {{#if this.mimage}}
                    <td><img src="data:image/jpeg;base64,{{this.mimage}}" height="100" alt= "None"> </td>
                {{/if}}
                {{#if this.mfileid}}
                    <td>
                        <a id="{{this.mId}}filelink" href=""></a>
                        <button class="ElementList-getFilebtn" data-value="{{this.mId}}">Get File</button>
                    </td>
                {{/if}}
                {{#equaluId this.uId}}
                    <td>
                    <button class="ElementList-editbtn" data-value="{{this.mId}}">Edit</button>
                    <button class="ElementList-delbtn" data-value="{{this.mId}}">Delete</button>
                    </td>
                {{/equaluId}}
                <td>
                <button class="ElementList-locationbtn" data-value="{{this.mId}}">Location</button>
                </td>
            </tr>
            {{/each}}
        </tbody>
    </table>
</div>