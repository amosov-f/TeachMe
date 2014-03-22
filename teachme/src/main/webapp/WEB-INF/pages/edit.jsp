<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page import="java.util.List" %>
<%@ page import="com.kk.teachme.checker.Checker" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.kk.teachme.model.Tag" %>
<%@ page import="com.kk.teachme.model.Problem" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>


<html lang="en">
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
    <script type="text/javascript" src="/resources/bootstrap/js/bootstrap-select.js"></script>

    <link href="/resources/jquery/css/jquery.autocomplete.css" rel="stylesheet" type="text/css"/>
    <link href="/resources/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css"/>
    <link href="/resources/bootstrap/css/bootstrap-select.css" rel="stylesheet" type="text/css"/>

    <link href="/resources/utility/css/styles.css" rel="stylesheet" type="text/css"/>

</head>

<body>

<%
    Problem problem = new Problem();
    String solutionText = "";
    if (request.getAttribute("problem") != null) {
        problem = (Problem)request.getAttribute("problem");
        solutionText = (String)request.getAttribute("solutionText");
    }
%>


    <div align="center">
        <h2 id="title"></h2>
    </div>

    <div class="container">

        <form class="form-inline col-lg-6" id="problem" method="post" action="/add_problem">
            <input type="hidden" id="problemId" name="problem_id" value="<%=problem.getId() %>"/>

            <legend>Название</legend>
            <input type="text" id="name" class="form-control" name="name" value="<%= problem.getName() %>"/>

            <legend>Условие</legend>
            <textarea id="statement" class="form-control" name="statement" rows="11"><%= problem.getStatement() %></textarea>

            <input type="hidden" id="figures" name="figures" value="<%= problem.getFiguresString() %>"/>


            <input type="hidden" id="tags" name="tags" value="<%= problem.getTagsString(false) %>"/>


            <legend>Ответ</legend>
            <input id="solution" name="solution" type="text" class="form-control" value="<%= solutionText %>"/>

                <div class="col-lg-6" style="padding-left: 0px; padding-right: 0px;">
                    <legend>Тип ответа</legend>
                    <select id="checkerId" name="checker_id">
            <%          Map<Integer, Checker> checkers = (Map<Integer, Checker>)request.getAttribute("checkerMap"); %>
            <%          for (Map.Entry<Integer, Checker> checker : checkers.entrySet()) { %>
                            <option value="<%=checker.getKey()%>"><%=checker.getValue().getName()%></option>
            <%          }   %>
                    </select>
                </div>

                <div class="col-lg-6" style="padding-left: 0px; padding-right: 0px;">
                    <legend>Сложность</legend>
                    <div class="col-lg-4 col-xs-4" style="padding-left: 0px;">
                        <input type="number" class="form-control" id="complexity" name="complexity" min="1" max="10" value="<%= problem.getComplexity() %>" autocomplete="off" />
                    </div>
                    <div class="checkbox col-lg-8 col-xs-8">
                        <label>
                            <input type="checkbox" id="inMind" name="in_mind" value="true" /> Решается в уме
                        </label>
                    </div>
                </div>


            <input type="hidden" id="newTags" name="new_tags" />

        </form>


        <div class="col-lg-6">

            <form class="form-group" id="figure" method="post" action="/files/upload" enctype="multipart/form-data">
                <legend>Рисунок</legend>
                <input
                        name="file"
                        id="file"
                        type="file"
                        accept="image/*"
                        onchange="return uploadFigure();"
                />
            </form>

            <div class="media" id="figureView" style=" position: relative;"></div>

            <div>
                <legend>Теги</legend>
                <input id="newTagsView" class="form-control" readonly="true" placeholder="новые теги"/>
                <br>
                <input
                        id="tagsEdit"
                        type="text"
                        class="form-control"
                        placeholder="введите теги через запятую..."
                        value="<%= problem.getTagsString(true) %>"
                />
            </div>

        </div>

    </div>

    <div class="container col-lg-12 col-xs-12" style="margin-top: 15px;">
        <div class="col-lg-7 col-xs-7">
            <button class="btn btn-primary btn-lg col-lg-6 col-xs-12 col-lg-offset-6" type="button" onclick="return submitProblem();">
                Сохранить
            </button>
        </div>
        <div class="col-lg-5 col-xs-5">
            <button class="btn btn-lg" type="button" onclick="cancel()">
                Отмена
            </button>
        </div>
    </div>


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

            $('#title').html('<h2>Новая задача</h2>');

        <%
            if (request.getAttribute("problem") != null) {

                problem = (Problem)request.getAttribute("problem");
        %>
                $('#title').html(
                        'Задача #<%= problem.getId() %>' +
                        '<button class="btn btn-delete" onclick="deleteProblem()">&#10006</button>'
                );

            <%
                if (!problem.getFigures().isEmpty()) {
            %>
                    figureId = "<%= problem.getFigures().get(0) %>";
                    updateFigure();
            <%
                }
            %>
                $('#checkerId').val("<%=(Integer)request.getAttribute("checkerId")%>");
        <%
            }
        %>

        <%
            if (problem.isInMind()) {
        %>
                $('#inMind').attr('checked', true);
        <%
            }
        %>

            $('#tagsEdit').tags({tags: existTags, newTagsOutput: $('#newTagsView')});
            $('#file').filestyle({input: false, classButton: 'btn btn-primary', buttonText: 'Загрузить'});

            connectByEnter('#name', '#statement');
            connectByEnter('#solution', '#tagsEdit');
            connectByEnter('#tagsEdit', '#name');

            $('#checkerId').selectpicker();
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
                document.location = '/admin'
        <%
            } else {
        %>
                document.location = '/admin?problem_id=<%=((Problem)request.getAttribute("problem")).getId()%>'
        <%
            }
        %>
        }

        function deleteProblem() {
            if (confirm("Вы точно хотите удалить задачу?")) {
                document.location = 'delete_problem?problem_id=' + $('#problemId').val();
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
                        '<img src="/files/' + figureId + '" class="img-rounded" style="max-width: 100%; max-height: 30%;"/>'
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

</body>
</html>