<%@ page import="com.kk.teachme.servlet.AdminController" %>
<%@ page import="javafx.util.Pair" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: Mary
  Date: 12.07.13
  Time: 21:00
  To change this template use File | Settings | File Templates.
--%>
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

<!-- <jsp:useBean id="adminController" class="com.kk.teachme.servlet.AdminController" /> -->

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
<%  for (Pair<Integer, String> checkerName : (List<Pair<Integer, String>>)request.getAttribute("checkerNameList")) { %>
        <option value=<%=checkerName.getKey()%>>
            <%=checkerName.getValue()%>
        </option>
<%  }   %>
    </select><br>

    <br>

    <input type = "submit"/>

</form>


</body>
</html>