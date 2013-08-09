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



        body {
            padding-top: 65px;
        }
    </style>
</head>

<body>

<script>

<%  Map<Integer, Solution> id2solution;  %>

    $(document).ready(function() {

    <%
        id2solution = (Map<Integer, Solution>)request.getAttribute("solutionMap");
    %>
        var existTags = new Array();
    <%
        for (Tag tag : (List<Tag>)request.getAttribute("tagList")) {
    %>
            existTags.push('<%=tag.getName()%>');
    <%
        }
    %>
        existTags.sort();

        $('#tag').bind('change keyup', showProblemList);
        $('#tag').autocomplete({
            maxHeight: 150,
            lookup: existTags
        });

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
        $.ajax({
            url: '/by_tag?tag=' + $('#tag').val(),
            success: function(data) {
                $('#left-part').html(data);
            }
        });
    }

</script>


<div class="navbar navbar-fixed-top" >
    <div class="container">
        <a href="/admin" class="navbar-brand">Админка</a>
        <div class="nav-collapse collapse navbar-responsive-collapse">
            <div class="navbar-form pull-left">
                <input id="tag" type="text" class="form-control col-lg-8" placeholder="поиск по тегу">
            </div>
            <div class="navbar-form pull-right">
                <a class="btn btn-primary" href="/new_problem">
                    Новая задача
                </a>
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

</body>
</html>