<%@ page import="static com.kk.teachme.checker.Checker.*" %>
<%@ page %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<body>
<%
    SolveStatus solveStatus = (SolveStatus) request.getAttribute("solveStatus");
%>
<%
    if (solveStatus.equals(SolveStatus.CORRECT)) {
%>
        <div class="alert alert-success">
            Поздравляем! Ответ верный!
        </div>
<%

    }
%>
<%
    if (solveStatus.equals(SolveStatus.INCORRECT)) {
%>
        <div class="alert alert-danger">
            К сожалению, ответ неверный.
        </div>
<%
    }
%>
<%
    if (solveStatus.equals(SolveStatus.INVALID)) {
%>
        <div class="alert alert-warning">
            Неверный формат ответа.
        </div>
<%
    }
%>
    <input id="itemClass" value="<%= (String)request.getAttribute("itemClass") %>" type="hidden"/>

</body>