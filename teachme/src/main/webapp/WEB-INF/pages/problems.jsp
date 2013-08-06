<%@ page import="com.kk.teachme.model.Problem" %>
<%@ page import="java.util.List" %>
<%@ page import="com.kk.teachme.model.Tag" %>
<%@ page import="com.kk.teachme.model.Solution" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.ArrayList" %>
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



        $('#tag').bind('click change paste keyup keydown textchange', updateProblems);
        $('#tag').autocomplete({
            maxHeight: 130,
            deferRequestBy: 300,
            lookup: existTags
        });
        console.log('!!!');
        updateProblems();
    });

    function updateProblems() {
        $('#problems').empty();

<%      for (Problem problem : (List<Problem>)request.getAttribute("problemList")) {    %>
            var ok = false;
<%          for (Tag tag : problem.getTags()) { %>
                if ($('#tag').val() == '' || $('#tag').val() == '<%=tag.getName()%>') {
                    ok = true;

                }
<%          }   %>
            if (ok) {
<%              Solution solution = id2solution.get(problem.getId()); %>

                $('#problems').append(createProblem({
                    name: escape('<%=problem.getName()%>'),
                    statement: escape('<%=problem.getStatement()%>'),
                    figures: escape('<%=problem.getFiguresString()%>'),
                    tags: escape('<%=problem.getTagsString(false)%>'),
                    solution: escape('<%=solution.getSolutionText()%>'),
                    checker: escape('<%=solution.getChecker().getName()%>')
                }));
            }
<%      }   %>
    }

    function createProblem(problem) {
        var problemPanel = $('<div class="panel panel-info"></div>');

        problemPanel.append('<div class="panel-heading">' + problem.name + '</div>');


        var statementWell = $('<div class="well">' + problem.statement + '</div>');

        var figuresDiv = $('<div align="center"></div>');

        if (problem.figures != null && problem.figures != '') {
            var figures = problem.figures.split(/,/);
            figuresDiv.append('<br><br>');
            for (var i = 0; i < figures.length; ++i) {

                figuresDiv.append(
                        '<img src="http://localhost:8080/files/' + figures[i] + '" style="width: 30%; height: 30%;"/>'
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

</script>


<div align="center">
    <h2>Список всех задач</h2>
    <input id="tag" type="search" placeholder="по тегу"/><br><br>
</div>


<div id="problems" class="container"></div>

</body>
</html>