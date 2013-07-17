<%@ page import="com.kk.teachme.servlet.AdminController" %>
<%@ page import="javafx.util.Pair" %>
<%@ page import="java.util.List" %>
<%@ page import="com.kk.teachme.checker.Checker" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.kk.teachme.model.Tag" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
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

<script>
    function concatTags() {
        var tags = document.getElementsByName('tags');

        var sum = '';
        for (var i = 0; i < tags.length; ++i) {
            if (tags[i].checked) {
                sum += tags[i].value;
            }
            if (i < tags.length) {
                sum += ','
            }
        }

        return sum;
    }

    window.onload = function() {
        document.getElementById("submit").onclick = function() {
            $("tags").val(concatTags())
        }
    }
</script>

<form method="post" action="/add_problem">

    Придумайте задачу<br>

    <br>

    Название:
    <input type = "text" name = "name"/><br>

    <br>

    Условие:<br>
    <textarea name = "statement"></textarea><br>

    <br>

    Теги:<br>
<%  for (Tag tag : (List<Tag>)request.getAttribute("tagList")) {    %>
        <input type="checkbox" name="tags" value=<%=tag.getName().replace(' ', '_')%>><%=tag.getName()%><br>
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

    <input type="submit" name="submit"/>

</form>

</body>
</html>