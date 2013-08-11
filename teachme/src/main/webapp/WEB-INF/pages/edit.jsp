<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page import="java.util.List" %>
<%@ page import="com.kk.teachme.checker.Checker" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.kk.teachme.model.Tag" %>
<%@ page import="com.kk.teachme.model.Problem" %>
<%@ page import="java.net.URLEncoder" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>


<html style="height: 100%;">
<head>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script type="text/javascript" src="/resources/utility/js/utility.js"></script>

    <script type="text/javascript" src="/resources/jquery/js/jquery-2.0.2.js"></script>
    <script type="text/javascript" src="/resources/jquery/js/jquery.form.js"></script>
    <script type="text/javascript" src="/resources/jquery/js/jquery.autocomplete.js"></script>
    <script type="text/javascript" src="/resources/jquery/js/jquery.tags.js"></script>

    <script type="text/javascript" src="/resources/bootstrap/js/bootstrap.js"></script>
    <script type="text/javascript" src="/resources/bootstrap/js/bootstrap.file-input.js"></script>

    <link href="/resources/jquery/css/jquery.autocomplete.css" rel="stylesheet" type="text/css"/>
    <link href="/resources/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css"/>
    <link href="/resources/utility/css/styles.css" rel="stylesheet" type="text/css"/>

</head>

<body style="height: 100%;">

    <script>

        var figureId;

        $(document).ready(function() {
            var existTags = new Array();
        <%
            for (Tag tag : (List<Tag>)request.getAttribute("tagList")) {
        %>
                existTags.push("<%=tag.getName()%>");
        <%
            }
        %>
            existTags.sort();
        <%
            if (request.getAttribute("problem") != null) {
                Problem problem = (Problem)request.getAttribute("problem");
        %>
                $('#title').html(
                        'Задача #<%=problem.getId()%>' +
                        '<button class="btn btn-delete" onclick="deleteProblem()">&#10006</button>'
                );

                $('#problemId').val(<%=problem.getId()%>);
                $('#name').val('<%=problem.getName()%>');

                $('#statement').val(decodeURIComponent('<%=URLEncoder.encode(problem.getStatement(), "UTF-8")%>').replace(/\+/g, ' '));


            <%
                if (!problem.getFigures().isEmpty()) {
            %>
                    figureId = "<%=problem.getFigures().get(0)%>";
                    updateFigure();
            <%
                }
            %>

                $('#tags').val("<%=problem.getTagsString(false)%>");
                $('#tagsEdit').val('<%=problem.getTagsString(true)%>');

                $('#solution').val("<%=(String)request.getAttribute("solution")%>");
                $('#checkerId').val("<%=(Integer)request.getAttribute("checkerId")%>");
        <%
            } else {
        %>
                $('#title').append('<h2>Новая задача</h2>');
        <%
            }
        %>

            $('#tagsEdit').tags({tags: existTags, newTagsOutput: $('#newTagsView')});
            $('#file').filestyle({input: false, classButton: 'btn btn-default', buttonText: 'Загрузить'});

            connectByEnter('#name', '#statement');
            connectByEnter('#solution', '#tagsEdit');
            connectByEnter('#tagsEdit', '#name');
        });

        function submitProblem() {
            if ($('#problemId').val() === '' && $('#statement').val() === '') {
                alert("Не бывает задач без условия!");
                return false;
            }

            $('#tags').val(concat($('#tagsEdit').tags('chosenTags')));
            $('#newTags').val(concat($('#tagsEdit').tags('newTags')));

            $('#problem').submit();

            return true;
        }

        function cancel() {
        <%
            if (request.getAttribute("problem") == null) {
        %>
                document.location.href = '/admin'
        <%
            } else {
        %>
                document.location.href = '/admin?problem_id=<%=((Problem)request.getAttribute("problem")).getId()%>'
        <%
            }
        %>
        }

        function deleteProblem() {
            if (confirm("Вы точно хотите удалить задачу?")) {
                document.location.href = 'delete_problem?problem_id=' + $('#problemId').val();
            }
        }

        function uploadFigure() {
            if ($('#file')[0].files[0].size > 1000000) {
                alert('Файл слишком большой');
                return false;
            }
            $('#figure').ajaxSubmit({
                success: function(data) {
                    figureId = data;
                    updateFigure();
                }
            });

            return true;
        }

        function updateFigure() {
            $('#figures').val(figureId);
            $('#figureView').html(null);
            $('#file').val(null);
            if (figureId != null && figureId != '') {
                $('#figureView').append(
                        '<img src="http://localhost:8080/files/' + figureId + '" style="max-height: 30%; max-width: 90%;"/>'
                );

                $('#figureView').append(
                        '<button class="btn btn-delete left-top" type="button" onclick="clearFigure()">&#10006</button>'
                );
            }
        }

        function clearFigure() {
            figureId = null;
            updateFigure();
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

    <div align="center" style="height: 8%;">
        <h2 id="title"></h2>
    </div>

    <div style="height: 80%;">

        <form class="form-inline edit-left-part" id="problem" method="post" action="/add_problem">
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


        <div class="edit-right-part">

            <form class="form-group" id="figure" method="post" action="/files/upload" enctype="multipart/form-data" style="width:100%; height: 15%;">
                <legend>Рисунок</legend>
                <input
                        name="file"
                        id="file"
                        type="file"
                        accept="image/*"
                        onchange="return uploadFigure();"
                />

            </form>

            <div class="media" id="figureView" style="max-height: 35%; position: relative;"></div>

            <div style="height: 50%;">
                <legend>Теги</legend>
                <input id="newTagsView" class="form-control" readonly="true" style="width: 100%" placeholder="новые теги"/>
                <br>
                <input
                        id="tagsEdit"
                        type="text"
                        class="form-control"
                        placeholder="введите теги через запятую..."
                        style="width: 100%"
                />
            </div>

        </div>

    </div>

    <div align="center" style="height: 7%;">
        <button class="btn btn-default" type="button" onclick="return submitProblem();" style="width: 30%; height: 90%">
            Сохранить
        </button>
        <button class="btn" type="button" onclick="cancel()" style="width: 10%; height: 90%">
            Отмена
        </button>
    </div>

</body>
</html>