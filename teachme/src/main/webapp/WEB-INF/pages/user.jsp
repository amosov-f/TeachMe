<%@ page import="com.kk.teachme.model.Problem" %>
<%@ page import="java.util.List" %>
<%@ page import="com.kk.teachme.model.Tag" %>
<%@ page import="com.kk.teachme.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script type="text/javascript" src="/resources/utility/js/utility.js"></script>

    <script type="text/javascript" src="/resources/jquery/js/jquery-2.0.2.js"></script>
    <script type="text/javascript" src="/resources/jquery/js/jquery.autocomplete.js"></script>
    <script type="text/javascript" src="/resources/jquery/js/jquery.tags.js"></script>

    <script type="text/javascript" src="/resources/bootstrap/js/bootstrap.js"></script>

    <link rel="stylesheet" type="text/css" href="/resources/bootstrap/css/bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="/resources/jquery/css/jquery.autocomplete.css"/>
    <link rel="stylesheet" type="text/css" href="/resources/utility/css/styles.css"/>
</head>

<body style="padding-top: 50px;">
<%
    if (request.getSession().getAttribute("user") == null) {
%>
        <jsp:forward page="login.jsp"/>
<%

    }

    User user = (User)request.getSession(true).getAttribute("user");
%>
    <div class="navbar navbar-fixed-top" >
        <div class="container">
            <a href="/user" class="navbar-brand">TeachMe</a>
            <div class="nav-collapse collapse navbar-responsive-collapse">
                <div class="navbar-form pull-left">
                    <input id="tag" type="text" class="form-control col-lg-8" placeholder="поиск по тегам" size="31">
                </div>
                <p id="loading" class="navbar-text pull-left"></p>
                <div class="navbar-form pull-right">
                    |
                    <a class="btn" href="/logout_user">
                        Выйти
                    </a>
                </div>
                <div class="navbar-text pull-right" href="">
                    <%= user.getLogin() %>
                </div>
            </div>
        </div>
    </div>

    <div class="container">
        <div id="left-part" class="left-part">
        </div>
        <div id="right-part" class="right-part">
        </div>
    </div>

    <script>

        var existTags = new Array();

        $(document).ready(function() {
        <%
            for (Tag tag : (List<Tag>)request.getAttribute("tagList")) {
        %>
                existTags.push('<%=tag.getName()%>');
        <%
            }
        %>
            existTags.sort();

            $('#tag').tags({tags: existTags});
            $('#tag').bind('change keyup', showProblemList);

        <%
            if (request.getAttribute("problemId") != null) {
                for (Problem problem : (List<Problem>)request.getAttribute("problemList")) {
                    if (problem.getId() == (Integer)request.getAttribute("problemId")) {
        %>
                        showProblem(<%= problem.getId() %>);
        <%
                    }
                }
            }
        %>

            showProblemList();
        });

        function showProblem(problemId) {
            $.ajax({
                url: '/user_problem',
                data: 'problem_id=' + problemId,
                success: function(data) {
                    $('#right-part').html(data);
                }
            });

            $.ajax({
                url: '/read',
                data: 'problem_id=' + problemId,
                success: function(data) {
                    setItemClass(problemId, data);
                }
            });
        }

        function setItemClass(problemId, className) {
            $('#name' + problemId).removeClass();
            $('#name' + problemId).addClass(className);
        }

        function showProblemList() {
            if ($('#tag').tags('newTags').length != 0) {
                return;
            }

            $.ajax({
                url: '/user_problems_by_tag_list',
                data: 'user_id=' + <%=user.getId()%> + '&tags=' + concat($('#tag').tags('chosenTags')),
                beforeSend: function() {
                    $('#loading').text('Загрузка...');
                },
                success: function(data) {
                    $('#left-part').html(data);
                    $('#loading').text('');
                    $('.list-group-item').click(function() {
                        showProblem($(this).attr('name'));
                    });
                }
            });
        }

        function submit() {
            var problemId =  $('#userProblemPanel').attr('name');
            $.ajax({
                url: '/submit',
                data: 'problem_id=' + problemId + '&solution_text=' + $('#solution').val(),
                success: function(data) {
                    $('#solveStatus').html(data);

                    setItemClass(problemId, $('#itemClass').val());
                }
            });
        }

    </script>

</body>
