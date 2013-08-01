<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="java.util.List" %>
<%@ page import="com.kk.teachme.checker.Checker" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.kk.teachme.model.Tag" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script type="text/javascript" src="/resources/jquery/jquery-1.9.1.js"></script>
    <script type="text/javascript" src="/resources/jquery/jquery.form.js"></script>

    <script type="text/javascript" src="/resources/bootstrap/js/bootstrap.min.js"></script>

    <link href="/resources/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" media="screen">
    <link href="/resources/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet">

    <style>
        .left-part {
            float: left;
            width: 44%;
            height: 100%;
            padding-left: 3%;
            padding-right: 3%;
        }

        .right-part {
            float: right;
            width: 44%;
            height: 100%;
            padding-left: 3%;
            padding-right: 3%;
        }

        .btn-submit {
            width: 51%;
        }

    </style>

    <title></title>
</head>

<body>

<%
    //show text box (name)
    //show text box (statement)
    //list of checkers
    //text box for answer
    //add an ability to choose tags and add new
    //submit button
%>

<script type="text/javascript">

    var figureId;
    var tags = new Array();
    var numberOnPage;

    function collectTags() {
        return $('#resultTags').val().replace(/, /g, ',').replace(/ /g, '_');
    }

    function submitProblem() {
        $('#figures').val(figureId);

        $('#tags').val(collectTags());
        $('#problem').submit();
        return false;
    }

    function uploadFigure() {
        $('#result').html('');

        var options = {
            success: function(data) {
                figureId = data;
                $('#result').html(
                        "<img src='http://localhost:8080/files/" + data + "' style='width: 30%; height: 30%' />"
                );
            }
        };

        $('#figure').ajaxSubmit(options);

        return false;
    }


    function searchTags() {
        var quest = $('#search').val().trim();

        clearTagList();

<%      for (Tag tag : (List<Tag>)request.getAttribute("tagList")) {    %>
            var tag = "<%=tag.getName()%>";

            if ((quest == '' || tag.indexOf(quest) != -1) && $('#resultTags').val().indexOf(tag) == -1) {
                tags.push(tag);
            }
<%      }   %>

        updateTagList();

        //$('#searchResult').val(result.substr(0, result.length - 1));
    }

    function clearTagList() {
        for (var i = 0; i < tags.length; ++i) {
            $('#tags' + i).remove();
        }
        tags = new Array();
    }

    function updateTagList() {
        for (var i = 0; i < tags.length; ++i) {
            $('<button />', {id: 'tags'+ i, value: tags[i], text: tags[i], class: "btn btn-default"}).appendTo($('#tagList'));
            $('#tags' + i).click({param: '#tags'+ i}, pushTagList);

        }

    }


    function pushTagList(event) {
        var id = event.data.param;
        var tag = $(id).val();
        //alert(tag);
        if ($('#resultTags').val() == '') {
            $('#resultTags').val(tag);
        }
        else {
            $('#resultTags').val($('#resultTags').val() + ', ' + tag);
        }
        $('#resultTags').text($('#resultTags').val());
        $(id).remove();
    }

    function popTagList() {
        var lastDiv = $('#resultTags').val().lastIndexOf(',');
        if (lastDiv == -1) {
            $('#resultTags').val('');
        }
        else {
            $('#resultTags').val($('#resultTags').val().substr(0, lastDiv));
        }
        $('#resultTags').text($('#resultTags').val());
    }



    //$('#search').bind('textchange', searchTags());

</script>

<div align="center">
    <h2>Придумайте задачу</h2>
</div>

<div class="content-wrapper">

    <div class="left-part">

        <form class="form-inline" id="problem" method="post" action="/add_problem">


            <legend>Название</legend>
            <input type="text" name="name"/>

            <legend>Условие</legend>
            <textarea name="statement" style="width: 98%"></textarea>

            <input type="hidden" id="figures" name="figures" />

            <input type="hidden" id="tags" name="tags" />


            <legend>Ответ</legend>
            <textarea name = "solution"></textarea>



            <legend>Чекер</legend>
            <select  name="checker_id" size="1">
        <%  Map<Integer, Checker> checkers = (Map<Integer, Checker>)request.getAttribute("checkerMap"); %>
        <%  for (Map.Entry<Integer, Checker> checker : checkers.entrySet()) { %>
                <option value=<%=checker.getKey()%>>
                    <%=checker.getValue().getName()%>
                </option>
        <%  }   %>
            </select>

        </form>
    </div>

    <div class="right-part">
        <form class="form-inline" id="figure" method="post" action="/files/upload" enctype="multipart/form-data">
            <legend>Рисунок</legend>
            <input name="file" id="file" type="file" />
            <button  class="btn btn-default" value="submit" onclick="return uploadFigure();" >прикрепить</button>
            <div id="result"></div>

            <legend>Теги</legend>
            <input id="search" type="text" class="search-query" placeholder="Search" />
            <button class="btn btn-default" type="button" onclick="searchTags()">Искать</button>
            <button class="btn btn-default" type="button" onclick="popTagList()">Удалить последний тег</button>

            <br><br>

            <div class="well well-small" id="resultTags" value=""></div>

            <div class="btn-group-vertical" id="tagList" ></div>
        </form>
    </div>

</div>

<div align="center">
    <button class="btn-submit btn btn-large" type="button" onclick="return submitProblem();">Отправить</button>
</div>

</body>
</html>