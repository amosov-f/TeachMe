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
            <div class="panel-body">
                <div class="well">
                    <p><%= problem.getStatement().replaceAll("\n", "<br>") %></p>
                <%
                    if (!problem.getFigures().isEmpty()) {
                %>
                        <div align="center">
                        <%
                            for (String figure : problem.getFigures()) {
                        %>
                                <img src="/files/<%= figure %>" style="height: 30%; max-width: 90%;"/>
                        <%
                            }
                        %>
                        </div>
                <%
                    }
                %>
                </div>

                <div class="container">
                    <div class="col-lg-4">
                        <div class="form-group col-lg-12">
                            <input id="solution" name="solution" class="form-control col-lg-12" type="text" placeholder="Ваш ответ" />
                        </div>
                        <div class="form-group col-lg-12">
                            <button id="submit" class="btn btn-primary col-lg-12">
                                Отправить
                            </button>
                        </div>
                    </div>
                    <div class="col-lg-8">
                        <div id="solveStatus"></div>
                    </div>

                </div>
            </div>
        </div>
<%
    }
%>

</body>