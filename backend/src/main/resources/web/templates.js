(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['ElementList.hb'] = template({"1":function(container,depth0,helpers,partials,data) {
    var alias1=container.lambda, alias2=container.escapeExpression;

  return "            <tr>\r\n                <td>"
    + alias2(alias1((depth0 != null ? depth0.username : depth0), depth0))
    + "</td>\r\n                <td>"
    + alias2(alias1((depth0 != null ? depth0.mMessage : depth0), depth0))
    + "</td>\r\n                <td><button class=\"ElementList-editbtn\" data-value=\""
    + alias2(alias1((depth0 != null ? depth0.mId : depth0), depth0))
    + "\">Edit</button></td>\r\n                <td><button class=\"ElementList-delbtn\" data-value=\""
    + alias2(alias1((depth0 != null ? depth0.mId : depth0), depth0))
    + "\">Delete</button></td>\r\n                <td>\r\n                    <div class=\"btn-group\" role=\"group\" aria-label=\"...\">\r\n                        <button class=\"ElementList-likebtn\" data-value=\""
    + alias2(alias1((depth0 != null ? depth0.mId : depth0), depth0))
    + "\">Like</button>\r\n                        <button class=\"ElementList-dislikebtn\" data-value=\""
    + alias2(alias1((depth0 != null ? depth0.mId : depth0), depth0))
    + "\">Dislike</button>\r\n                    </div>\r\n                </td>\r\n            </tr>\r\n            <tr>\r\n                <td><img src=\"data:image/jpeg,"
    + alias2(alias1((depth0 != null ? depth0.mimage : depth0), depth0))
    + "\" alt= \"None\"> </td>\r\n                \r\n            </tr>\r\n            \r\n";
},"compiler":[7,">= 4.0.0"],"main":function(container,depth0,helpers,partials,data) {
    var stack1;

  return "<div class=\"panel panel-default\" id=\"ElementList\">\r\n    <div class=\"panel-heading\">\r\n        <h3 class=\"panel-title\">All Data</h3>\r\n        <button id=\"ElementList-logoutbtn\">logout</button>\r\n    </div>\r\n    <table class=\"table\">\r\n        <tbody>\r\n"
    + ((stack1 = helpers.each.call(depth0 != null ? depth0 : (container.nullContext || {}),(depth0 != null ? depth0.mData : depth0),{"name":"each","hash":{},"fn":container.program(1, data, 0),"inverse":container.noop,"data":data})) != null ? stack1 : "")
    + "        </tbody>\r\n    </table>\r\n</div>";
},"useData":true});
})();
(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['EditEntryForm.hb'] = template({"compiler":[7,">= 4.0.0"],"main":function(container,depth0,helpers,partials,data) {
    var stack1;

  return "<div id=\"EditEntryForm\" class=\"modal fade\" role=\"dialog\">\r\n    <div class=\"modal-dialog\">\r\n        <div class=\"modal-content\">\r\n            <div class=\"modal-header\">\r\n                <h4 class=\"modal-title\">Edit an Entry</h4>\r\n            </div>\r\n            <div class=\"modal-body\">\r\n                \r\n                <label for=\"EditEntryForm-message\">Message</label>\r\n                <textarea class=\"form-control\" id=\"EditEntryForm-message\">"
    + container.escapeExpression(container.lambda(((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.mMessage : stack1), depth0))
    + "</textarea>\r\n            </div>\r\n            <div class=\"modal-footer\">\r\n                <button type=\"button\" class=\"btn btn-default\" id=\"EditEntryForm-OK\">OK</button>\r\n                <button type=\"button\" class=\"btn btn-default\" id=\"EditEntryForm-Close\">Close</button>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</div>";
},"useData":true});
})();
(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['NewEntryForm.hb'] = template({"compiler":[7,">= 4.0.0"],"main":function(container,depth0,helpers,partials,data) {
    return "<nav class=\"navbar navbar-default navbar-fixed-bottom\" id=\"NewEntryForm\">\r\n  <div class=\"container\">\r\n    <table class = \"NewEntryForm-table\">\r\n        <tr>\r\n            <td><textarea class=\"form-control\" id=\"NewEntryForm-message\" placeholder = \"write a message :D\"></textarea></td>\r\n            <td><input id=\"Upload\" type=\"file\" accept=\"application/pdf\" onchange=\"onChange(event)\"/></td>\r\n            <td><button type=\"button\" class=\"btn btn-default\" id=\"NewEntryForm-OK\">OK</button></td>\r\n\r\n        </tr>\r\n    \r\n    \r\n  </div>\r\n</nav>";
},"useData":true});
})();
(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['Login.hb'] = template({"compiler":[7,">= 4.0.0"],"main":function(container,depth0,helpers,partials,data) {
    return "<div class=\"panel panel-default\" id=\"Login\">\r\n    <div class=\"panel-heading\">\r\n        <h3 class=\"panel-title\">Login</h3>\r\n    </div>\r\n    <div class=\"panel-body\">\r\n        <textarea class=\"Login-panel\" id=\"Login-username\" placeholder = \"username\"></textarea>\r\n        <textarea class=\"Login-panel\" id=\"Login-password\" placeholder = \"password\"></textarea>\r\n        <button type=\"button\" class=\"Login-button\" id=\"Login-Login\">LOGIN</button>\r\n        <p>you can sign in with Google!</p>\r\n        \r\n    </div>\r\n</div>\r\n\r\n";
},"useData":true});
})();
