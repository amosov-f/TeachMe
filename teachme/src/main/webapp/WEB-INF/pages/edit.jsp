<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="java.util.List" %>
<%@ page import="com.kk.teachme.checker.Checker" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.kk.teachme.model.Tag" %>
<%@ page import="com.kk.teachme.model.Problem" %>
<%@ page import="java.net.URLEncoder" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script type="text/javascript" src="/resources/jquery/js/jquery-2.0.2.js"></script>
    <script type="text/javascript" src="/resources/jquery/js/jquery.form.js"></script>
    <script type="text/javascript" src="/resources/jquery/js/jquery.autocomplete.js"></script>


    <script type="text/javascript" src="/resources/bootstrap/js/bootstrap.js"></script>
    <script type="text/javascript" src="/resources/bootstrap/js/bootstrap.file-input.js"></script>


    <link href="/resources/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css"/>
    <link href="/resources/jquery/css/jquery.autocomplete.css" rel="stylesheet" type="text/css"/>

    <style>
        .left-part {
            float: left;
            width: 48%;
            height: 100%;
            padding-left: 3%;
        }

        .right-part {
            float: right;
            width: 48%;
            height: 100%;
            padding-right: 3%;
        }

        body, html {
            height: 100%;
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

    var splitter = /,\s*/;

    $(document).ready(function() {
<%      for (Tag tag : (List<Tag>)request.getAttribute("tagList")) {    %>
            existTags.push("<%=tag.getName()%>");
<%      }   %>
        existTags.sort();

<%      if (request.getAttribute("problem") != null) {
            Problem problem = (Problem)request.getAttribute("problem"); %>

            $('#problemId').val(<%=problem.getId()%>);
            $('#name').val('<%=problem.getName()%>');

            $('#statement').val(decode('<%=URLEncoder.encode(problem.getStatement(), "UTF-8")%>'));

<%          if (!problem.getFigures().isEmpty()) {  %>
                figureId = "<%=problem.getFigures().get(0)%>";
                updateFigure();
<%          }   %>

            $('#tags').val("<%=problem.getTagsString(false)%>");
            $('#tagsEdit').val('<%=problem.getTagsString(true)%>');

            $('#solution').val("<%=(String)request.getAttribute("solution")%>");
            $('#checkerId').val("<%=(Integer)request.getAttribute("checkerId")%>");
<%      }   %>


        $('#file').filestyle({input: false, classButton: 'btn btn-default',  buttonText: 'Загрузить'});

        $('#tagsEdit').bind('click change paste keyup keydown textchange', updateTags);
        $('#tagsEdit').autocomplete({
            delimiter: splitter,
            maxHeight: 130,
            onSelect: function() {
                $('#tagsEdit').val($('#tagsEdit').val() + ', ');
            }
        });

        connectByEnter('#name', '#statement');
        connectByEnter('#statement', '#solution');
        connectByEnter('#solution', '#tagsEdit');
        connectByEnter('#tagsEdit', '#name');

        updateTags();
    });

    function submitProblem() {
        $('#tags').val(concat(chosenTags));
        $('#newTags').val(concat(newTags));
        $('#problem').submit();
        return false;
    }

    function uploadFigure() {
        if ($('#file')[0].files[0].size > 1000000) {
            alert('Файл слишком большой');
            return false;
        }
        var options = {
            success: function(data) {
                figureId = data;
                updateFigure();
            }
        };
        $('#figure').ajaxSubmit(options);

        return true;
    }

    function updateFigure() {
        $('#figures').val(figureId);
        $('#figureView').html(null);
        $('#file').val(null);
        if (figureId != null && figureId != '') {
            $('#figureView').append("<img src='http://localhost:8080/files/" + figureId + "' style='height: 30%;'/>");
            $('#figureView').append(
                    '<button class="btn btn-mini" type="button" onclick="clearFigure()">&times</button>'
            );
        }
    }

    function clearFigure() {
        figureId = null;
        updateFigure();
    }

    function updateTags() {
        chosenTags = jQuery.unique(trim($('#tagsEdit').val()).split(splitter));
        newTags = сomplement(chosenTags, existTags);
        $('#newTagsView').val(viewConcat(newTags));
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
            result += strArray[i];
        }
        return encodeURIComponent(result);
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
            if (str[i] != ' ' &&  str[i] != ',') {
                if (l == -1) {
                    l = i;
                }
                r = i;
            }
        }
        return str.substr(l, r + 1);
    }

    function decode(str) {
        return decodeURIComponent(str).replace(/\+/g, ' ');
    }

    function connectByEnter(from, to) {
        $(from).keypress(function(e) {
            if (e.which == 13) {
                $(to).focus();
                e.preventDefault();
            }
        });
    }

</script>

<div align="center">
    <h2>Задача</h2>
</div>

<div style="height: 80%;">

    <form class="form-inline left-part" id="problem" method="post" action="/add_problem">
        <input type="hidden" id="problemId" name="problem_id"/>

        <legend>Название</legend>
        <input type="text" id="name" class="form-control" name="name"/>

        <legend>Условие</legend>
        <textarea id="statement" class="form-control" name="statement" style="height: 30%;"></textarea>

        <input type="hidden" id="figures" name="figures"/>
        <input type="hidden" id="tags" name="tags"/>


        <legend>Ответ</legend>
        <textarea id="solution" name="solution" class="form-control" ></textarea>

        <legend>Тип ответа</legend>
        <select id="checkerId" name="checker_id" class="form-control" size="1" style="width: 30%;">
<%          Map<Integer, Checker> checkers = (Map<Integer, Checker>)request.getAttribute("checkerMap"); %>
<%          for (Map.Entry<Integer, Checker> checker : checkers.entrySet()) { %>
                <option value="<%=checker.getKey()%>"><%=checker.getValue().getName()%></option>
<%          }   %>
        </select>

        <input type="hidden" id="newTags" name="newTags" />

    </form>


    <div class="right-part">

        <form class="form-group" id="figure" method="post" action="/files/upload" enctype="multipart/form-data" style="width:100%; height: 15%;">
            <legend>Рисунок</legend>
            <input
                    name="file"
                    id="file"
                    type="file"
                    accept="image/*"
                    onchange="return uploadFigure();"
                    />
            <!--<input
                    id="figureReference"
                    type="text"
                    class="form-control"
                    readonly="true"
                    placeholder="ссылка на рисунок"
                    style="float: right; width: 50%;"
            /> -->

        </form>

        <div class="media" id="figureView" style="max-height: 35%;"></div>

        <div style="height: 50%;">
            <legend>Теги</legend>
            <input id="newTagsView" class="form-control" readonly="true" style="width: 100%" placeholder="новые теги" />
            <br>
            <input
                    id="tagsEdit"
                    type="search"
                    class="form-control"
                    placeholder="введите теги через запятую..."
                    style="width: 100%"
                    />
        </div>

    </div>

</div>

<div align="center" style="height: 7%;">
    <button class="btn btn-default" type="button" onclick="return submitProblem();" style="width: 30%; height: 90%">Отправить</button>
</div>

</body>
</html>