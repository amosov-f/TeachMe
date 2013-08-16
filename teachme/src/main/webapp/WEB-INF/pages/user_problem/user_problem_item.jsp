<%@ page import="com.kk.teachme.model.Tag" %>
<%@ page import="com.kk.teachme.model.UserProblem" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<body>
<%
    UserProblem userProblem = (UserProblem)request.getAttribute("userProblem");
%>
    <p class="user-problem-<%= userProblem.getStatus().toString().toLowerCase() %>">
        <%= userProblem.getProblem().getName() %>
    <%
        if (userProblem.getAttempts() != 0) {
    %>
            <span id="attempts<%= userProblem.getProblem().getId() %>" class="badge pull-right">
                <%= userProblem.getAttempts()%>
            </span>
    <%
        }
    %>
    </p>
<%
    for (Tag tag : userProblem.getProblem().getTags()) {
%>
        <span class="label label-info"><%= tag.getName() %></span>&nbsp
<%
    }
%>

</body>
