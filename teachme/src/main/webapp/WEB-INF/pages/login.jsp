<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <script type="text/javascript" src="/resources/utility/js/utility.js"></script>

    <script type="text/javascript" src="/resources/jquery/js/jquery-2.0.2.js"></script>
    <script type="text/javascript" src="/resources/jquery/js/jquery.form.js"></script>
    <script type="text/javascript" src="/resources/jquery/js/jquery.autocomplete.js"></script>
    <script type="text/javascript" src="/resources/jquery/js/jquery.tags.js"></script>

    <script type="text/javascript" src="/resources/bootstrap/js/bootstrap.js"></script>
    <script type="text/javascript" src="/resources/bootstrap/js/bootstrap.file-input.js"></script>

    <link href="/resources/jquery/css/jquery.autocomplete.css" rel="stylesheet" type="text/css"/>
    <link href="/resources/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css"/>
    <link href="/resources/bootstrap/css/jumbotron-narrow.css" rel="stylesheet" type="text/css"/>
    <link href="/resources/utility/css/styles.css" rel="stylesheet" type="text/css"/>

    <title>TeachMe</title>
</head>

<body>
    <div class="container">
        <div class="header">
           <h3 align="center" class="text-muted">TeachMe</h3>
            <!-- Здесь будет охуенный логотип -->
        </div>
        <div class="jumbotron">
            <h1>Онлайн-тесты.</h1>
            <p class="lead">И это только начало.</p>
            <p><a class="btn btn-primary btn-lg" href="http://oauth.vk.com/authorize?client_id=3810701&redirect_uri=http://<%=(String)request.getAttribute("adress")%>/vklogin&response_type=code" role="button">Войти через VK</a></p>
        </div>
        <div class="footer">
            <p>&copy; TeachMe 2014</p>
        </div>
          </div>

    <%--<form class="container">--%>

        <%--<div class="form-group">--%>
        <%--<%--%>
            <%--if (request.getAttribute("login") != null) {--%>
                <%--String login = (String)request.getAttribute("login");--%>
        <%--%>--%>
                <%--<h4><span style="color: blue;"><%= login %></span>, вы успешно зарегестрированы!</h4>--%>
        <%--<%--%>
            <%--}--%>
        <%--%>--%>
            <%--<input id="login" class="form-control" type="text" name="login" placeholder="Введите логин"/>--%>
        <%--</div>--%>

        <%--<div class="form-horizontal">--%>
            <%--<button id="button1" class="btn btn-primary" type="submit" onclick="logIn()">Войти</button>--%>
            <%--<button id="button2" class="btn" type="submit" onclick="register()">Зарегистрироваться</button>--%>
        <%--</div>--%>
    <%--</form>--%>

    <%--<script type="text/javascript">--%>
        <%--function logIn() {--%>
            <%--$('form').attr('action', '/login_user');--%>
        <%--}--%>
        <%--function register() {--%>
            <%--$('form').attr('action', '/reg_user');--%>
        <%--}--%>
    <%--</script>--%>
    <%----%>


</body>
</html>