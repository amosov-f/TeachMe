<%@ page import="com.kk.teachme.model.Problem" %>
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

    <link rel="stylesheet" type="text/css" href="/resources/bootstrap/css/bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="/resources/jquery/css/jquery.autocomplete.css"/>
    <link rel="stylesheet" type="text/css" href="/resources/utility/css/styles.css"/>
</head>

<body style="padding-top: 50px;">

    <div class="navbar navbar-fixed-top" >
        <div class="container">
            <a href="/admin" class="navbar-brand">Админка</a>
            <div class="nav-collapse collapse navbar-responsive-collapse">
                <div class="navbar-form pull-left">
                    <input id="tag" type="text" class="form-control col-lg-8" placeholder="поиск по тегам" size="31">
                </div>
                <p id="loading" class="navbar-text pull-left"></p>
                <div class="navbar-form pull-right">
                    <a class="btn btn-primary" href="/new_problem">
                        Новая задача
                    </a>
                </div>
            </div>
        </div>
    </div>

    <div class="container">
        <form id="left-part" class="left-part col-4">
        </form>
        <form id="right-part" class="right-part col-8">
        </form>
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
