<%@ page import="com.kk.teachme.servlet.AdminController" %>
<%@ page import="javafx.util.Pair" %>
<%@ page import="java.util.List" %>
<%@ page import="com.kk.teachme.checker.Checker" %>
<%@ page import="java.util.Map" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>

<%
    //show text box (name)
    //show text box (statement)
    //list of checkers
    //text box for answer
    //add an ability to choose tags and add new
    //submit button
%>

<form method="post" action="/add_problem">

    Create your problem<br>

    <br>

    Name:
    <input type = "text" name = "name" value="name"/><br>

    <br>

    Statement:<br>
    <textarea name = "statement">statement</textarea><br>

    <br>

    Solution:<br>
    <textarea name = "solution">solution</textarea><br>

    <br>

    Checker:
    <select name="checker_id" size="1">
<%  Map<Integer, Checker> checkers = (Map<Integer, Checker>)request.getAttribute("checkerMap"); %>
<%  for (Map.Entry<Integer, Checker> checker : checkers.entrySet()) { %>
        <option value=<%=checker.getKey()%>>
            <%=checker.getValue().getName()%>
        </option>
<%  }   %>
    </select><br>

    <br>

    <input type = "submit"/>

</form>

</body>
</html>