<%@ page import="com.kk.teachme.model.Problem" %>
<%@ page import="java.util.List" %>
<%@ page import="com.kk.teachme.model.Tag" %>
<%@ page import="com.kk.teachme.model.Solution" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script type="text/javascript" src="/resources/jquery/js/jquery-2.0.2.js"></script>
    <script type="text/javascript" src="/resources/bootstrap/js/bootstrap.js"></script>
    <script type="text/javascript" src="/resources/jquery/js/jquery.autocomplete.js"></script>

    <link rel="stylesheet" type="text/css" href="/resources/bootstrap/css/bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="/resources/jquery/css/jquery.autocomplete.css"/>

    <style>

        .left-part {
            float: left;
            width: 29%;
        }

        .right-part {
            float: right;
            width: 69%;
        }

        .problem:hover {
            border-color: #66afe9;
            outline: 0;
            -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075), 0 0 8px rgba(102, 175, 233, 0.6);
            box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075), 0 0 8px rgba(102, 175, 233, 0.6);
            cursor: pointer;
        }

        body {
            padding-top: 65px;
        }
    </style>
</head>

<body>

<script>

<%  Map<Integer, Solution> id2solution;  %>

    $(document).ready(function() {

<%      id2solution = (Map<Integer, Solution>)request.getAttribute("solutionMap");   %>

        var existTags = new Array();
<%      for (Tag tag : (List<Tag>)request.getAttribute("tagList")) {    %>
            existTags.push('<%=tag.getName()%>');
<%      }   %>
        existTags.sort();

        $('#tag').bind('change keyup', updateProblems);
        $('#tag').autocomplete({
            maxHeight: 150,
            lookup: existTags
        });

<%      if (request.getAttribute("problemId") != null) {
            for (Problem problem : (List<Problem>)request.getAttribute("problemList")) {
                if (problem.getId() == (Integer)request.getAttribute("problemId")) {
                    Solution solution = id2solution.get(problem.getId());   %>
                    var problem = {
                        id: <%=problem.getId()%>,
                        name: decode('<%=URLEncoder.encode(problem.getName(), "UTF-8")%>'),
                        statement:  decode('<%=URLEncoder.encode(problem.getStatement(), "UTF-8")%>'),
                        figures:  '<%=problem.getFiguresString()%>',
                        tags:  '<%=problem.getTagsString(false)%>',
                        solution:  decode('<%=URLEncoder.encode(solution.getSolutionText())%>'),
                        checker:  '<%=solution.getChecker().getName()%>'
                    };
                    showProblem(problem);
<%              }
            }
        }   %>

        updateProblems();
    });

    function updateProblems() {
        $('#problems').empty();

        var isEmpty = true;
<%      for (Problem problem : (List<Problem>)request.getAttribute("problemList")) {    %>
            var ok = false;
            if ($('#tag').val() == '') {
                ok = true;
            }
<%          for (Tag tag : problem.getTags()) { %>
                if ($('#tag').val() == '<%=tag.getName()%>') {
                    ok = true;
                }
<%          }   %>
            if (ok) {

<%              Solution solution = id2solution.get(problem.getId());   %>

                var problem = {
                    id: <%=problem.getId()%>,
                    name: decode('<%=URLEncoder.encode(problem.getName(), "UTF-8")%>'),
                    statement:  decode('<%=URLEncoder.encode(problem.getStatement(), "UTF-8")%>'),
                    figures:  '<%=problem.getFiguresString()%>',
                    tags:  '<%=problem.getTagsString(false)%>',
                    solution:  decode('<%=URLEncoder.encode(solution.getSolutionText())%>'),
                    checker:  '<%=solution.getChecker().getName()%>'
                };

                $('#problems').append(createProblemItem(problem));

                isEmpty = false;
            }
<%      }   %>

        if (isEmpty) {
            $('#problems').append('<div align="center">Задачи не найдены</div>');
        }
    }


    function editProblem(event) {
        document.location.href = 'http://localhost:8080/edit_problem?problem_id=' + event.data.param;
    }

    function createProblemItem(problem) {
        var problemItem = $('<a class="list-group-item" style="cursor: pointer;"></a>');
        problemItem.click({param: problem}, showProblem);

        problemItem.append('<p class="list-group-item-text">' + problem.name + '</p>');

        if (problem.tags != null && problem.tags != '') {
            var tags = problem.tags.split(/,/);
            for (var i = 0; i < tags.length; ++i) {
                problemItem.append('&nbsp' + '<span class="label label-info">' + tags[i] + '</span>');
            }
        }

        return problemItem;
    }

    function showProblem(problem) {
        if (problem.data != null) {
            problem = problem.data.param;
        }
        $('#problemView').empty();
        $('#problemView').append(createProblemPanel(problem));
    }

    function createProblemPanel(problem) {
        var problemPanel = $('<div class="problem panel panel-info"></div>');
        problemPanel.click({param: problem.id}, editProblem);

        problemPanel.append('<div class="panel-heading">' + problem.name + '</div>');

        var statementWell = $('<div class="well">' + problem.statement.replace(/\n/g, '<br>') + '</div>');

        var figuresDiv = $('<div align="center"></div>');

        if (problem.figures != null && problem.figures != '') {
            var figures = problem.figures.split(/,/);
            figuresDiv.append('<br><br>');
            for (var i = 0; i < figures.length; ++i) {

                figuresDiv.append(
                        '<img src="http://localhost:8080/files/' + figures[i] + '" style="height: 30%; max-width: 90%;"/>'
                );
            }
        }

        statementWell.append(figuresDiv);
        problemPanel.append(statementWell);

        if (problem.tags != null && problem.tags != '') {
            var tags = problem.tags.split(/,/);
            for (var i = 0; i < tags.length; ++i) {
                problemPanel.append('<span class="label label-info">' + tags[i] + '</span>' + '&nbsp');
            }
            problemPanel.append('<br><br>');
        }

        problemPanel.append('Тип ответа: ' + '<span class="label">' + problem.checker + '</span><br>');
        problemPanel.append('Ответ: ' + '<span class="label label-success">' + problem.solution + '</span>');

        return problemPanel;
    }

    function decode(str) {
        return decodeURIComponent(str).replace(/\+/g, ' ');
    }

</script>


<div class="navbar navbar-fixed-top" >
    <div class="container">
        <a href="http://localhost:8080/admin" class="navbar-brand">Админка</a>
        <div class="nav-collapse collapse navbar-responsive-collapse">
            <div class="navbar-form pull-left">
                <input id="tag" type="text" class="form-control col-lg-8" placeholder="поиск по тегу">
            </div>
            <div class="navbar-form pull-right">
                <button class="btn btn-primary" onclick="document.location.href = 'http://localhost:8080/new_problem'">
                    Новая задача
                </button>
            </div>
        </div>
    </div>
</div>

<div class="container">
    <div class="left-part">
        <div id="problems" class="list-group"></div>
    </div>
    <div class="right-part">
        <div id="problemView" class="affix" style="width: 60%;"></div>
    </div>
</div>

</body>
</html>