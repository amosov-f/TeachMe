<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.kk.teachme.model.User" %>
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
    <link href="/resources/utility/css/styles.css" rel="stylesheet" type="text/css"/>
</head>

<body class="under-navbar">

    <div class="navbar navbar-default navbar-fixed-top" role="navigation">
        <div class="container">
            <div href="/admin" class="navbar-header">
                <button class="navbar-toggle" type="button" data-toggle="collapse" data-target=".bs-navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a href="/problems" class="navbar-brand">TeachMe</a>
            </div>
            <div class="collapse navbar-collapse">
                <ul class="nav navbar-nav pull-left">
                    <li>
                        <a href="/problems">
                            Задачи
                        </a>
                    </li>
                </ul>
                <jsp:include page="user/user_sign.jsp"></jsp:include>
            </div>
        </div>
    </div>

<%
    User user = (User)request.getAttribute("user");
    int solved = (Integer)request.getAttribute("solved");
    int all = (Integer)request.getAttribute("all");
%>
    <div class="container">
        <h2>
            <%= user.getFirstName() + " " + user.getLastName() %>
            <button id="admin" class="btn btn-primary" onclick="location.href = '/admin'">Админка</button>
        </h2>
    <%

    %>
        <p>
            Решено <%= solved * 100 / all%>&#37 задач
            (<%= solved %> из <%= all %>)
        </p>
    </div>

    <script>

        $(document).ready(function() {
            $.ajax({
                url: '/is_admin',
                success: function(data) {
                    if (data == false) {
                        $('#admin').remove();
                    }
                }
            });
        });

    </script>

</body>
</html>