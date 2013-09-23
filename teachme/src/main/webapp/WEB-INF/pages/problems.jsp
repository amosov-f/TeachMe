<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
            <nav class="collapse navbar-collapse bs-navbar-collapse">
                <div class="navbar-form navbar-left" role="search">
                    <div class="form-group">
                        <input id="tag" type="text" class="form-control" placeholder="поиск по тегам"/>
                    </div>
                    <div class="form-group">
                        <select id="filter">
                            <option value="">Без фильтра</option>
                            <option value="unsolved">Нерешенные</option>
                            <option value="read">Прочитанные</option>
                            <option value="solved">Решенные</option>
                            <option value="attempted">Есть попытки</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <div class="checkbox">
                            <label>
                                <input id="inMind" type="checkbox" style="margin-left: 10px;"
                                <%
                                    if (request.getAttribute("inMind") != null && (Boolean)request.getAttribute("inMind")) {
                                %>
                                        checked="checked"
                                <%
                                    }
                                %>
                                /> В уме
                            </label>
                        </div>

                    </div>
                </div>
                <p id="loading" class="hidden-xs navbar-text"></p>
                <jsp:include page="user/user_sign.jsp"></jsp:include>
            </nav>
        </div>
    </div>

    <div id="container" class="container scroll">
        <div id="user-problem-list" class="list-group margin-top">
        </div>
    </div>

    <script>

        var curProblemId = -1;
        var curChosenTags = null;
        var curFilter = '';
        var curInMind = false;

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
            if (request.getAttribute("tags") != null && !((String)request.getAttribute("tags")).isEmpty()) {
        %>

                $('#tag').val('<%= (String)request.getAttribute("tags") %>'.replace(/,\s*/g, ', ') + ', ');
        <%
            }
        %>

        <%
            if (request.getAttribute("problemId") != null) {
        %>
                curProblemId = <%= (Integer)request.getAttribute("problemId") %>
        <%
            }
        %>
            $('#filter').change(function() {
                createProblemList();
            });

            $('#filter').selectpicker();

            $('#inMind').change(function() {
                createProblemList();
            });

            createProblemList();

            $('#container').scroll(function() {
                if (uploading || uploaded) {
                    return;
                }
                if ($(this).prop('scrollHeight') - $(this).height() <= $(this).scrollTop() + 300) {
                    uploadProblemList(false);
                }
            });
        });

        function showProblem(problemId) {
            document.location = '/user_problem_' + problemId +
                    '?tags=' + concat(curChosenTags) +
                    '&in_mind=' + $('#inMind').is(':checked');
        }

        function createProblemList() {

            if ($('#tag').tags('newTags').length != 0) {
                return;
            }
            if (
                    curChosenTags != null &&
                    $('#tag').tags('chosenTags').toString() === curChosenTags.toString() &&
                    curFilter === $('#filter').val() &&
                    curInMind == $('#inMind').is(':checked')
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
            curInMind = $('#inMind').is(':checked');

            var from = curPages * onPage;
            var to = from + onPage;

            $.ajax({
                url: '/user_problem_list',
                data: 'tags=' + concat(curChosenTags) +
                      '&filter=' + $('#filter').val() +
                      '&in_mind=' + $('#inMind').is(':checked') +
                      '&from=' + from + '&to=' + to,
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

    </script>

</body>
