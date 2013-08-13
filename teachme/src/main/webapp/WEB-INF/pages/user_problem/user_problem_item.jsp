<%@ page import="com.kk.teachme.model.Tag" %>
<%@ page import="com.kk.teachme.model.UserProblem" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<body>
<%
    UserProblem userProblem = (UserProblem)request.getAttribute("userProblem");
%>

    <a id="<%= userProblem.getProblem().getId() %>" class="list-group-item" style="cursor: pointer;">
        <p class="user-problem-<%= userProblem.getStatus().toString().toLowerCase() %> list-group-item-text">
            <%= userProblem.getProblem().getName() %>
        </p>
    <%
        for (Tag tag : userProblem.getProblem().getTags()) {
    %>
            <span class="label label-info"><%= tag.getName() %></span>&nbsp
    <%
        }
    %>
</a>

</body>
