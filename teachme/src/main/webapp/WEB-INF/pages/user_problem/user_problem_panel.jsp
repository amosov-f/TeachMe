<%@ page import="com.kk.teachme.model.Problem" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <link href="/resources/utility/css/styles.css" rel="stylesheet" type="text/css"/>
</head>

<body>
<%
    if (request.getAttribute("problem") != null) {
        Problem problem = (Problem)request.getAttribute("problem");
%>
        <div id="userProblemPanel" name="<%= problem.getId() %>" class="panel panel-info margin-top" value="<%=problem.getId()%>">
            <div class="panel-heading"><%= problem.getName()%></div>
            <div class="well">
                <%= problem.getStatement().replaceAll("\n", "<br>") %>

                <div align="center">
                    <%
                        if (!problem.getFigures().isEmpty()) {
                    %>
                    <br><br>
                    <%
                        for (String figure : problem.getFigures()) {
                    %>
                    <img src="/files/<%= figure %>" style="height: 30%; max-width: 90%;"/>
                    <%
                            }
                        }
                    %>
                </div>
            </div>

            <div class="container">
                <h5>Ваш ответ:</h5>
                <input id="solution" name="solution" class="form-control" type="text" />

                <button class="btn btn-primary" onclick="submit()" style="margin-top: 10px; margin-bottom: 10px">
                    Отправить
                </button>
                <div id="solveStatus"></div>
            </div>
        </div>


<%
    }
%>

</body>