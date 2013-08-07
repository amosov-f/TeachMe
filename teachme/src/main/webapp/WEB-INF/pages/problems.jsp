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
        .problem:hover {
            border-color: #66afe9;
            outline: 0;
            -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075), 0 0 8px rgba(102, 175, 233, 0.6);
            box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075), 0 0 8px rgba(102, 175, 233, 0.6);
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
            deferRequestBy: 300,
            lookup: existTags
        });

        updateProblems();
    });

    function updateProblems() {
        $('#problems').empty();

        var isEmpty = true;
<%      for (Problem problem : (List<Problem>)request.getAttribute("problemList")) {    %>
            var ok = false;
<%          for (Tag tag : problem.getTags()) { %>
                if ($('#tag').val() == '' || $('#tag').val() == '<%=tag.getName()%>') {
                    ok = true;

                }
<%          }   %>
            if (ok) {
<%              Solution solution = id2solution.get(problem.getId());   %>

                $('#problems').append(createProblemPanel({
                    id: <%=problem.getId()%>,
                    name: decode('<%=URLEncoder.encode(problem.getName(), "UTF-8")%>'),
                    statement:  decode('<%=URLEncoder.encode(problem.getStatement(), "UTF-8")%>'),
                    figures:  '<%=problem.getFiguresString()%>',
                    tags:  '<%=problem.getTagsString(false)%>',
                    solution:  decode('<%=URLEncoder.encode(solution.getSolutionText())%>'),
                    checker:  '<%=solution.getChecker().getName()%>'
                }));

                isEmpty = false;
            }
<%      }   %>

        if (isEmpty) {
            $('#problems').append('<div align="center">Задачи не найдены</div>');
        }
    }


    function editProblem(event) {
        document.location.href = 'http://localhost:8080/admin?problem_id=' + event.data.param;
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
                        '<img src="http://localhost:8080/files/' + figures[i] + '"/>'
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
        }

        problemPanel.append('<br><br>');
        problemPanel.append(problem.checker);
        problemPanel.append('&nbsp' + '<span class="label label-success">' + problem.solution + '</span>');

        return problemPanel;
    }

    function decode(str) {
        return decodeURIComponent(str).replace(/\+/g, ' ');
    }

</script>


<div align="center">
    <h2>Список всех задач</h2>
    <input id="tag" type="search" placeholder="по тегу" style="width: 15%;"/>
    <button class="btn btn-primary" onclick="document.location.href = 'http://localhost:8080/admin'">
        Создать новую задачу
    </button>
    <br><br>

</div>


<div id="problems" class="container"></div>

</body>
</html>