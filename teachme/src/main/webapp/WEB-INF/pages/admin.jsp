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
    <script type="text/javascript" src="/resources/jquery/jquery.autocomplete.js"></script>

    <script type="text/javascript" src="/resources/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/resources/bootstrap/js/bootstrap.file-input.js"></script>

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

        .autocomplete-suggestions {
            border: 1px solid #999;
            overflow-y: scroll;
            -webkit-box-shadow: 1px 4px 3px rgba(50, 50, 50, 0.64);
            -moz-box-shadow: 1px 4px 3px rgba(50, 50, 50, 0.64);
            box-shadow: 1px 4px 3px rgba(50, 50, 50, 0.64);
            padding: 2px 5px;
            white-space: nowrap;
        }

        .autocomplete-selected {
            background: #F0F0F0;
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

    var existTags = new Array();
    var chosenTags = new Array();
    var newTags = new Array();

    var splitter = /[,;]\s*/;

    $(document).ready(function() {
<%      for (Tag tag : (List<Tag>)request.getAttribute("tagList")) {    %>
            existTags.push("<%=tag.getName()%>");
<%      }   %>
        existTags.sort();

        $('#file').bootstrapFileInput();

        $('#tagsEdit').bind('click change paste keyup keydown textchange', updateTags);
        $('#tagsEdit').autocomplete({
            delimiter: splitter,
            maxHeight: 150,
            onSelect: function() {
                $('#tagsEdit').val($('#tagsEdit').val() + ', ');
            }
        });
        updateTags();
    });

    function submitProblem() {
        $('#figures').val(figureId);
        $('#tags').val(concat(chosenTags));
        $('#newTags').val(concat(newTags));
        $('#problem').submit();
        return false;
    }

    function uploadFigure() {
        $('#figureView').html('');

        var options = {
            success: function(data) {
                figureId = data;
                $('#figureView').html(
                        "<img src='http://localhost:8080/files/" + data + "' style='width: 38%' />"
                );
            }
        };

        $('#figure').ajaxSubmit(options);

        return false;
    }

    function updateTags() {
        chosenTags = trim($('#tagsEdit').val()).split(splitter);
        newTags = сomplement(chosenTags, existTags);
        $('#newTagsView').text(viewConcat(newTags));
        $('#tagsEdit').autocomplete().setOptions({lookup: сomplement(existTags, chosenTags)});
    }

    function contains(obj, el) {
        return obj.indexOf(el) != -1;
    }

    function сomplement(a, b) {
        var result = [];
        for (var i = 0; i < a.length; ++i) {
            if (!contains(b, a[i])) {
                result.push(a[i]);
            }
        }
        return result;
    }

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

    function viewConcat(strArray) {
        var result = '';
        for (var i = 0; i < strArray.length; ++i) {
            if (i > 0) {
                result += ', ';
            }
            result += strArray[i];
        }
        return result;
    }

    function trim(str) {
        var l = -1;
        for (var i = 0; i < str.length; ++i) {
            if (str[i] != ' ' &&  str[i] != ',' && str[i] != ';') {
                if (l == -1) {
                    l = i;
                }
                r = i;
            }
        }
        return str.substr(l, r + 1);
    }

</script>

<div align="center">
    <h2>Придумайте задачу</h2>
</div>

<div class="content-wrapper">

    <div class="left-part">

        <form class="form-inline" id="problem" method="post" action="/add_problem">


            <legend>Название</legend>
            <input type="text" name="name" style="width: 98%" />

            <legend>Условие</legend>
            <textarea name="statement" style="width: 98%; height: 200px" ></textarea>

            <input type="hidden" id="figures" name="figures" />
            <input type="hidden" id="tags" name="tags" />


            <legend>Ответ</legend>
            <textarea name = "solution" style="width: 98%"></textarea>



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
             <input name="file" id="file" type="file" title="Найти рисунок" onchange="return uploadFigure();" />
            <div id="figureView"></div>
        </form>

        <legend>Теги</legend>

        <label>Новые теги</label>
        <div id="newTagsView" class="well well-small" ></div>
        <input id="tagsEdit" type="text" placeholder="введите теги через запятую..." class="autocomplete-suggestions autocomplete-selected" style="width: 98%" />

    </div>

</div>

<div align="center">
    <button class="btn-submit btn btn-large" type="button" onclick="return submitProblem();">Отправить</button>
</div>

</body>
</html>