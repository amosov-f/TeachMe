<%@ page import="java.util.List" %>
<%@ page import="com.kk.teachme.model.Tag" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script type="text/javascript" src="/resources/utility/js/utility.js"></script>

    <script type="text/javascript" src="/resources/jquery/js/jquery-2.0.2.js"></script>
    <script type="text/javascript" src="/resources/jquery/js/jquery.autocomplete.js"></script>
    <script type="text/javascript" src="/resources/jquery/js/jquery.tags.js"></script>

    <script type="text/javascript" src="/resources/bootstrap/js/bootstrap.js"></script>
    <script type="text/javascript" src="/resources/bootstrap/js/bootstrap-select.js"></script>

    <link rel="stylesheet" type="text/css" href="/resources/bootstrap/css/bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="/resources/bootstrap/css/bootstrap-select.css"/>
    <link rel="stylesheet" type="text/css" href="/resources/bootstrap/css/bootstrap-theme.css"/>


    <link rel="stylesheet" type="text/css" href="/resources/jquery/css/jquery.autocomplete.css"/>
    <link rel="stylesheet" type="text/css" href="/resources/utility/css/styles.css"/>
</head>

<body class="under-navbar">
    <div class="navbar navbar-default navbar-fixed-top bs-docs-nav" role="banner">
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
            <p id="loading" class="hidden-xs navbar-text"></p>
            <nav class="collapse navbar-collapse  bs-navbar-collapse" role="navigation" >
                <jsp:include page="user/user_sign.jsp"/>
            </nav>
        </div>
    </div>

    <div id="container" class="container">
        <%

            boolean inMind = request.getAttribute("inMind") != null && (Boolean)request.getAttribute("inMind");
            boolean tagList = request.getAttribute("tagList") != null && !((List<Tag>) request.getAttribute("tagList")).isEmpty();
            if (inMind || tagList) {
        %>
                <blockquote style="margin-top: 15px;">
                    Сейчас Вы решаете задачи
        <%
            }
            if (inMind) {
        %>
                <b>в уме</b>

        <%
                if (!tagList) {
        %>
                    </blockquote>
        <%
                }
            }

            if (tagList) {
                List<Tag> tags = (List<Tag>) request.getAttribute("tagList");
                if (tags.size() == 1) {
        %>
                    по тегу
            <%
                } else {
            %>
                    по тегам
            <%
                }
                for (Tag tag : (List<Tag>) request.getAttribute("tagList")) {
            %>
                    <span class="label label-info"><%= tag.getName() %></span>&nbsp
        <%
                }
        %>
                </blockquote>
        <%
            }
        %>

        <div id="panel">
            <jsp:include page="user_problem/user_problem_panel.jsp"/>
        </div>
        <div class="col-xs-6 col-sm-3 col-lg-3" style="padding-bottom: 15px;">
            <button type="button" class="btn btn-default col-xs-12 col-lg-12" onclick="goBack()">
                <span class="glyphicon glyphicon-arrow-left"></span>
                К списку
            </button>
        </div>
        <div class="col-xs-6 col-sm-3 col-lg-3" style="padding-bottom: 15px;">
            <button type="button" class="btn btn-default col-xs-12 col-lg-12" onclick="easierProblem()">
                Проще
                <span class="glyphicon glyphicon-chevron-down"></span>
            </button>
        </div>
        <div class="col-xs-6 col-sm-3 col-lg-3" style="padding-bottom: 15px;">
            <button type="button" class="btn btn-default col-xs-12 col-lg-12" onclick="similarProblem()">
                Так же
                <span class="glyphicon glyphicon-chevron-right"></span>
            </button>
        </div>
        <div class="col-xs-6 col-sm-3 col-lg-3" style="padding-bottom: 15px;">
            <button type="button" class="btn btn-default col-xs-12 col-lg-12" onclick="harderProblem()">
                Сложнее
                <span class="glyphicon glyphicon-chevron-up"></span>
            </button>
        </div>
    </div>

    <script>

        function goBack() {
            document.location = '/problems?' +
                    'problem_id=' + $('#userProblemPanel').attr('name') +
                    '&tags=<%= (String) request.getAttribute("tags") %>' +
                    '&in_mind=<%= request.getAttribute("inMind") %>';
        }

        function changeProblem(adress) {
            $.ajax({
                url: '/' + adress + '_user_problem',
                data: 'problem_id=' + $('#userProblemPanel').attr('name') +
                        '&tags=<%= (String)request.getAttribute("tags") %>' +
                        '&in_mind=' +  <%= request.getAttribute("inMind") %>,
                beforeSend: function() {
                    $('#loading').html('Загрузка...');
                    $('#notFound').html('');
                    $('#status').html('');
                },

                success: function(data) {
                    if (data.trim() === '') {
                        $('#status').html('<div class="alert alert-warning">Задача не найдена</div>');
                    } else {
                        $('#panel').html(data);
                    }
                    $('#loading').html('');
                }
            });
        }

        function easierProblem() {
            return changeProblem('easier');
        }

        function similarProblem() {
            return changeProblem('similar')
        }


        function harderProblem() {
            return changeProblem('harder');
        }

    </script>

</body>
