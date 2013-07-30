<%@ page import="java.util.List" %>
<%@ page import="com.kk.teachme.checker.Checker" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.kk.teachme.model.Tag" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <script type="text/javascript" src="/resources/jquery/jquery-1.9.1.js"></script>
    <script type="text/javascript" src="/resources/jquery/jquery.form.js"></script>
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

    function concatTags() {
        var tags = document.getElementsByName('tags');

        var sum = '';
        for (var i = 0; i < tags.length; ++i) {
            if (tags[i].checked) {
                sum += tags[i].value;
            }
            if (i < tags.length) {
                sum += ',';
            }
        }

        return sum;
    }

    var figureId;

    function submitProblem() {
        $('#figures').val(figureId);
        $('tags').val(concatTags());
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

</script>

<form id="problem" method="post" action="/add_problem">
    <h1>Придумайте задачу</h1>

    Название:
    <input type="text" name="name"/><br>

    <br>

    Условие:<br>
    <textarea name="statement"></textarea><br>

    <br>

    <input type="hidden" id="figures" name="figures" />

    Теги:<br>
<%  for (Tag tag : (List<Tag>)request.getAttribute("tagList")) {    %>
        <input type="checkbox" name="tags" value="<%=tag.getName().replace(' ', '_')%>"><%=tag.getName()%><br>
<%  }   %>

    <br>

    Ответ:<br>
    <textarea name = "solution"></textarea><br>

    <br>

    Чекер:
    <select name="checker_id" size="1">
<%  Map<Integer, Checker> checkers = (Map<Integer, Checker>)request.getAttribute("checkerMap"); %>
<%  for (Map.Entry<Integer, Checker> checker : checkers.entrySet()) { %>
        <option value=<%=checker.getKey()%>>
            <%=checker.getValue().getName()%>
        </option>
<%  }   %>
    </select><br>

    <br>

    <input type="submit" value="отправить" onclick="submitProblem()"/>

</form>

<form id="figure" method="post" action="/files/upload" enctype="multipart/form-data">
    Рисунок:
    <input name="file" id="file" type="file" /><br/>
    <button value="submit" onclick="return uploadFigure();" >прикрепить</button>
    <div id="result"></div>
</form>

</body>
</html>