<%@ page import="com.kk.teachme.model.Problem" %>
<%@ page import="com.kk.teachme.model.Tag" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<body>

<%
    Problem problem = (Problem)request.getAttribute("problem");
%>
    <a name="<%= problem.getId() %>" class="list-group-item" style="cursor: pointer;">
        <p class="list-group-item-text"><%= problem.getName() %></p>
    <%
        for (Tag tag : problem.getTags()) {
    %>
            <span class="label label-info"><%= tag.getName() %></span>&nbsp
    <%
        }
    %>
    </a>

</body>
