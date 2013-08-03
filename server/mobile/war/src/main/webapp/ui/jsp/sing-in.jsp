<%@ page import="org.openjst.server.mobile.web.UIAssets" %>
<%@ page import="static org.openjst.server.mobile.I18n.*" %>
<%--
  ~ Copyright (C) 2013 OpenJST Project
  ~
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU Affero General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU Affero General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Affero General Public License
  ~ along with this program.  If not, see <http://www.gnu.org/licenses/>.
  --%>

<%--
  @author Sergey Grachev
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <%=UIAssets.jspDefault() + UIAssets.jsDefault()%>
</head>

<body class="sam">

<div class="container">

    <form class="form-signin" method='post' action='j_security_check'>
        <h2 class="form-signin-heading">Please sign in</h2>
        <input type="text" class="input-block-level"
               placeholder="<%=getString("web.ui.label.id")%>"
               id='j_username' name='j_username'>
        <input type="password" class="input-block-level"
               placeholder="<%=getString("web.ui.label.password")%>" id='j_password'
               name='j_password'>
        <button class="btn btn-large btn-primary" type="submit">
            <%=getString("web.ui.label.sing-in")%>
        </button>
    </form>

</div>

<script>
    YUI(OJST.framework).use(OJST.ns.pages.SingInCss, OJST.libs.BootstrapCss, function (Y) {
    });
</script>

</body>
</html>