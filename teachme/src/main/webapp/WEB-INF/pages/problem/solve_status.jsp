<%@ page import="com.kk.teachme.checker.SolveStatus" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<body>
<%
    SolveStatus solveStatus = (SolveStatus)request.getAttribute("solveStatus");
%>
<%
    if (solveStatus.equals(SolveStatus.CORRECT)) {
%>
        <div class="alert alert-success">
            Ответ верный!
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
        <div class="alert">
            Неверный формат ответа.
        </div>
<%
    }
%>
</body>