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
    <script type="text/javascript" src="/resources/bootstrap/js/bootstrap-select.js"></script>

    <link rel="stylesheet" type="text/css" href="/resources/bootstrap/css/bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="/resources/bootstrap/css/bootstrap-select.css"/>

    <link rel="stylesheet" type="text/css" href="/resources/jquery/css/jquery.autocomplete.css"/>
    <link rel="stylesheet" type="text/css" href="/resources/utility/css/styles.css"/>
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
                <div class="navbar-form navbar-left" role="search">
                    <input id="tag" type="text" class="form-control" placeholder="поиск по тегам" size="30">
                </div>
                <div class="navbar-form pull-left">
                    <select id="filter">
                        <option value="">Без фильтра</option>
                        <option value="unsolved">Нерешенные</option>
                        <option value="read">Прочитанные</option>
                        <option value="solved">Решенные</option>
                        <option value="attempted">Есть попытки</option>
                    </select>
                </div>
                <div class="nav navbar-nav navbar-left">
                    <p id="loading" class="navbar-text"></p>
                </div>
                <jsp:include page="user/user_sign.jsp"></jsp:include>
            </div>
        </div>
    </div>

    <div class="container">
        <div id="left-part" class="left-part col-lg-4">
            <div id="user-problem-list" class="list-group margin-top">
            </div>
        </div>
        <div id="right-part" class="right-part col-lg-8">
        </div>
    </div>

    <script>

        var curProblemId = -1;
        var curChosenTags = null;
        var curFilter = '';

        var onPage = 20;
        var curPages;
        var uploaded = false;
        var uploading = false;


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
            $('#tag').bind('change keyup', createProblemList);

        <%
            if (request.getAttribute("problemId") != null) {
                for (Problem problem : (List<Problem>)request.getAttribute("userProblemList")) {
                    if (problem.getId() == (Integer)request.getAttribute("problemId")) {
        %>
                        showProblem(<%= problem.getId() %>);
        <%
                    }
                }
            }
        %>
            $('#filter').change(function() {
                createProblemList();
            });

            $('#filter').selectpicker();

            createProblemList();

            $('#left-part').scroll(function() {
                if (uploading || uploaded) {
                    return;
                }
                if ($(this).prop('scrollHeight') - $(this).height() <= $(this).scrollTop() + 300) {
                    uploadProblemList(false);
                }
            });
        });

        function showProblem(problemId) {
            $.ajax({
                url: '/user_problem_panel',
                data: 'problem_id=' + problemId,
                beforeSend: function() {
                    $('#' + curProblemId).removeClass('item-active');
                    $('#' + problemId).addClass('item-active');
                    curProblemId = problemId;
                },
                success: function(data) {
                    $('#right-part').html(data);
                    $('#submit').click(submit);
                    $('#solution').focus();
                    $('#solution').keypress(function(e) {
                        if (e.which == 13) {
                            submit();
                        }
                    });
                }
            });

            $.ajax({
                url: '/read',
                data: 'problem_id=' + problemId,
                success: function(data) {
                    $('#' + problemId).html(data);

                }
            });
        }

        function createProblemList() {

            if ($('#tag').tags('newTags').length != 0) {
                return;
            }
            if (
                    curChosenTags != null &&
                    $('#tag').tags('chosenTags').toString() === curChosenTags.toString() &&
                    curFilter === $('#filter').val()
            ) {
                return;
            }

            curPages = 0;
            uploaded = false;

            uploadProblemList(true);
        }

        function uploadProblemList(create) {

            curChosenTags = $('#tag').tags('chosenTags');
            curFilter = $('#filter').val();

            var from = curPages * onPage;
            var to = from + onPage;

            $.ajax({
                url: '/user_problem_list',
                data: 'tags=' + concat(curChosenTags) + '&filter=' + $('#filter').val() + '&from=' + from + '&to=' + to,
                beforeSend: function() {
                    uploading = true;
                    $('#loading').text('Загрузка...');
                },
                success: function(data) {
                    $('#loading').text('');
                    if (data.trim() === '') {
                        uploaded = true;
                    }

                    if (create) {
                        if (data.trim() === '') {
                            $('#user-problem-list').html('<h5>Задачи не найдены</h5>');
                        } else {
                            $('#user-problem-list').html(data);
                        }
                    } else {
                        $('#user-problem-list').append(data);
                    }

                    $('#' + curProblemId).addClass('item-active');
                    $('.list-group-item').click(function() {
                        showProblem($(this).attr('name'));
                    });
                    ++curPages;
                    uploading = false;
                }
            });


        }

        function submit() {
            var problemId =  $('#userProblemPanel').attr('name');
            $.ajax({
                url: '/submit',
                data: 'problem_id=' + problemId + '&solution_text=' + $('#solution').val(),
                beforeSend: function() {
                    $('#solveStatus').html('');
                },
                success: function(data) {
                    $('#solveStatus').html(data);
                    $('#solution').select();

                    $.ajax({
                        url: '/user_problem_item',
                        data: 'problem_id=' + problemId,
                        success: function(data) {
                            $('#' + problemId).html(data);
                        }
                    });
                }
            });
        }

    </script>

</body>
