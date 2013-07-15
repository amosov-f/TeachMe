<%@ page import="com.kk.teachme.servlet.AdminController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<!-- <jsp:useBean id="adminController" class="com.kk.teachme.servlet.AdminController" /> -->
<form method="post" action="/login_user">
    Log in or sign up
    <br>
    Log  in:
    <input type="text" name="userName"/> <input type = "submit"/>
</form>
<form method="post" action="/reg_user">
    Sign up:
    <input type="text" name="userName"/> <input type = "submit"/>
</form>
</body>
</html>