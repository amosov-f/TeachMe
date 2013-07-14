<%@ page import="com.kk.teachme.servlet.AdminController" %>
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

    name of problem:
    <input type = "text" name = "name" value="test"/>
    <br>

    problem statement:
    <input type = "text" name = "statement" value="test"/>
    <br>

    problem solution:
    <input type ="text" name = "solution" value="test"/>
    <br>

    checker_id:
    <input type = "number" name = "checker_id" value="1"/>
    <br>

    <input type = "submit"/>


</form>

<% //adminController.addProblem(name, statement) />
    //AdminController controller = new AdminController();
    //controller.addProblem()

%>

</body>
</html>