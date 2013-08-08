<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.kk.teachme.servlet.UserController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>

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

<form name="vk_signin" id="vk_signin" action="<c:url value="/signin/vkontakte"/>" method="POST">
    <button type="submit">Sign In with VKontakte</button>
</form>

</body>
</html>