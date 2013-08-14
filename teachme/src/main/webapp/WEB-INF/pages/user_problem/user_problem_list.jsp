<%@ page import="com.kk.teachme.model.Problem" %>
<%@ page import="java.util.List" %>
<%@ page import="com.kk.teachme.model.UserProblem" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<body>
<%
    Object object = request.getAttribute("userProblemList");
    if (object == null || ((List<UserProblem>)object).isEmpty()) {
%>
        <div align="center">Задачи не найдены</div>
<%
    } else {
%>
        <div class="list-group margin-top">
        <%
            for (UserProblem userProblem : (List<UserProblem>)object) {
                request.setAttribute("userProblem", userProblem);
        %>
                <jsp:include page="user_problem_item.jsp"></jsp:include>
        <%
            }
        %>
        </div>
<%
    }
%>
</body>