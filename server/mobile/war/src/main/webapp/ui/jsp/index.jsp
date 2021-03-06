<%@ page import="org.openjst.server.mobile.web.UIResources" %>
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
    <%= UIResources.jspDefault() + UIResources.jsDefault() %>
</head>

<body class="yui3-skin-sam">

<div style="background: transparent;"></div>

<script>
    YUI(OJST.framework).use(OJST.libs.Bootstrap, OJST.ns.apps.ManagementConsole, function (Y) {
        "use strict";

        new OJST.ui.apps.ManagementConsole({ viewContainer: 'body > div', html5: false })
                .render().dispatch();
    });
</script>

</body>
</html>