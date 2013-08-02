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

        .scrollable {
            width: 100%;
            height: 160px;
            overflow-y: auto;
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
    //add an ability to choose suggestedTags and add new
    //submit button
%>

<script type="text/javascript">

    var figureId;

    var allTags = new Array();
    var suggestedTags = new Array();
    var chosenTags = new Array();
    var newTags = new Array();

    function contains(obj, el) {
        return obj.indexOf(el) != -1;
    }

    $(document).ready(function() {
        $('#search').bind('change paste keyup keydown', searchTags);

<%      for (Tag tag : (List<Tag>)request.getAttribute("tagList")) {    %>
            allTags.push("<%=tag.getName()%>");
<%      }   %>

        searchTags();
    });

    function concat(strArray) {
        var result = '';
        for (var i = 0; i < strArray.length; ++i) {
            if (i > 0) {
                result += ',';
            }
            result += strArray[i].replace(/ /g, '_');
        }
        return result;
    }

    function submitProblem() {
        $('#figures').val(figureId);
        $('#tags').val(concat(chosenTags));
        $('#newTags').val(concat(newTags));
        $('#problem').submit();
        return false;
    }

    function uploadFigure() {
        $('#result').html('');

        var options = {
            success: function(data) {
                figureId = data;
                $('#result').html(
                        "<img src='http://localhost:8080/files/" + data + "' style='width: 38%' />"
                );
            }
        };

        $('#figure').ajaxSubmit(options);

        return false;
    }


    function searchTags() {
        clearSuggestedTagsView();
        var substr = $('#search').val().trim();

        for (var i = 0; i < allTags.length; ++i) {
            var tag = allTags[i];
            if ((substr == '' || contains(tag, substr)) && !contains(chosenTags, tag)) {
                suggestedTags.push(tag);
            }
        }

        suggestedTags.sort();

        updateSuggestedTagsView();
        updateChosenTagsView();
    }

    function clearSuggestedTagsView() {
        for (var i = 0; i < suggestedTags.length; ++i) {
            $('#suggestedTags' + i).remove();
        }
        suggestedTags = new Array();
    }

    function updateSuggestedTagsView() {
        for (var i = 0; i < suggestedTags.length; ++i) {
            $('<button />', {
                id: 'suggestedTags'+ i,
                value: suggestedTags[i],
                text: suggestedTags[i],
                class: "btn btn-default"}
            ).appendTo($('#suggestedTagsView'));
            $('#suggestedTags' + i).click({param: '#suggestedTags'+ i}, addToChosenTags);
        }
    }

    function updateChosenTagsView() {
        var str = '';
        for (var i = 0; i < chosenTags.length; ++i) {
            if (i > 0) {
                str += ', ';
            }
            str += chosenTags[i];
        }
        $('#chosenTagsView').val(str);
        $('#chosenTagsView').text(str);
    }

    function addToChosenTags(event) {
        var tag = $(event.data.param).val();
        chosenTags.push(tag);
        searchTags();
    }

    function popFromChosenTags() {
        chosenTags.pop();
        searchTags();
    }

    function addNewTag() {
        var tag = $('#search').val().trim();
        if (tag == '') {
            return;
        }
        if (!contains(allTags, tag)) {
            allTags.push(tag);
            newTags.push(tag);
        }

        searchTags();
    }

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
<%          Map<Integer, Checker> checkers = (Map<Integer, Checker>)request.getAttribute("checkerMap"); %>
<%          for (Map.Entry<Integer, Checker> checker : checkers.entrySet()) { %>
                <option value=<%=checker.getKey()%>>
                    <%=checker.getValue().getName()%>
                </option>
<%          }   %>
            </select>

            <input type="hidden" id="newTags" name="newTags" />

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

            <button class="btn btn-default" type="button" onclick="addNewTag()">Сделать новый тег</button>
            <button class="btn btn-default" type="button" onclick="popFromChosenTags()">Удалить последний тег</button>

            <br><br>

            <div id="chosenTagsView" class="well well-small" ></div>

            <div id="suggestedTagsView" class="scrollable btn-group-horisontal" ></div>
        </form>
    </div>

</div>

<div align="center">
    <button class="btn-submit btn btn-large" type="button" onclick="return submitProblem();">Отправить</button>
</div>

</body>
</html>