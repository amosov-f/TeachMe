<%@ page import="com.kk.teachme.model.Problem" %>
<%@ page import="java.util.List" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<body>
<%
    if (request.getAttribute("problemList") == null || ((List<Problem>)request.getAttribute("problemList")).isEmpty()) {
%>
        <div align="center">Задачи не найдены</div>
<%
    } else {
        List<Problem> problems = (List<Problem>)request.getAttribute("problemList");
%>
        <div class="list-group margin-top">
        <%
            for (Problem problem : problems) {
                request.setAttribute("problem", problem);
        %>
                <jsp:include page="problem_item.jsp"></jsp:include>
        <%
            }
        %>
        </div>
<%
    }
%>
</body>