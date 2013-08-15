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
    User user = (User)request.getSession().getAttribute("user");
%>
    <div class="navbar navbar-fixed-top" role="navigation">
        <div class="container">
            <div href="/admin" class="navbar-header">
                <button class="navbar-toggle" type="button" data-toggle="collapse" data-target=".bs-navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a href="/admin" class="navbar-brand">Админка</a>
            </div>
            <div class="collapse navbar-collapse">
                <div class="navbar-form navbar-left" role="search">
                    <input id="tag" type="text" class="form-control" placeholder="поиск по тегам" size="30">
                </div>
                <div class="nav navbar-nav navbar-left">
                    <p id="loading" class="navbar-text"></p>
                </div>
                <ul class="nav navbar-nav navbar-right">
                    <li>
                        <button class="btn btn-primary navbar-btn" onclick="location.href = '/new_problem'">
                            Новая задача
                        </button>
                    </li>
                    <li>
                        <a href="/problems">
                            <%= user.getFirstName() + " " + user.getLastName() %>
                        </a>
                    </li>
                    <li class="divider-vertical"></li>
                    <li>

                        <a href="/logout">
                            Выйти
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </div>

    <div class="container">
        <div id="left-part" class="left-part col-lg-4">
        </div>
        <div id="right-part" class="right-part col-lg-8">
        </div>
    </div>

    <script>
        $(document).ready(function() {
            var existTags = new Array();
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
                url: '/problem_' + problemId,
                success: function(data) {
                    $('#right-part').html(data);
                }
            });
        }

        function showProblemList() {
            if ($('#tag').tags('newTags').length != 0) {
                return;
            }

            $.ajax({
                url: '/by_tag_list',
                data: 'tags=' + concat($('#tag').tags('chosenTags')),
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

    </script>

</body>
