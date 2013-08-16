<%@ page import="com.kk.teachme.model.User" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<body>
    <ul class="nav navbar-nav navbar-right">
    <%
        if (request.getSession().getAttribute("user") != null) {
            User user = ((User)request.getSession().getAttribute("user"));
    %>
                <li>
                    <a href="/user_<%= user.getId() %>">
                        <%= user.getName() %>
                    </a>
                </li>
                <li class="divider-vertical"></li>
                <li>
                    <a href="/logout">
                        Выйти
                    </a>
                </li>
    <%
        } else {
    %>
            <li>
                <a href="/login">
                    Войти
                </a>
            </li>
    <%
        }
    %>
    </ul>
</body>