<%@ page import="com.kk.teachme.model.Problem" %>
<%@ page import="com.kk.teachme.model.Tag" %>
<%@ page import="com.kk.teachme.model.Solution" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <link href="/resources/utility/css/styles.css" rel="stylesheet" type="text/css"/>
</head>

<body>
<%
    if (request.getAttribute("problem") != null) {
        Problem problem = (Problem) request.getAttribute("problem");
        Solution solution = (Solution) request.getAttribute("solution");
%>
        <div id="problemPanel" class="panel panel-info panel-edit margin-top" value="<%=problem.getId()%>">
            <div class="panel-heading"><%= problem.getName()%></div>
            <div class="panel-body">
                <div class="well">
                    <%= problem.getStatement().replaceAll("\n", "<br>") %>
                <%
                    if (!problem.getFigures().isEmpty()) {
                %>
                        <div align="center">
                        <%
                            for (String figure : problem.getFigures()) {
                        %>
                                <img src="/files/<%= figure %>" class="img-rounded img-panel" />
                        <%
                            }
                        %>
                        </div>
                <%
                    }
                %>
                </div>

            <%
                if (!problem.getTags().isEmpty()) {
                    for (Tag tag : problem.getTags()) {
            %>
                        <span class="label label-info"><%= tag.getName() %></span>&nbsp
                <%
                    }
                %>
                    <br><br>
            <%
                }
            %>
                <div>
                    Сложность
                    <div class="label label-default"><%= problem.getComplexity() %></div>
                <%
                    if (problem.isInMind()) {
                %>
                        , решается в уме
                <%
                    }
                %>
                </div>
                <div>
                    Ответом является
                    <div class="label label-default"><%= solution.getChecker().getName() %></div>
                    <div class="label label-success"><%= solution.getSolutionText() %></div>
                </div>

            </div>
        </div>

        <script>
            $(document).ready(function() {
                $('#problemPanel').click(function() {
                    document.location = '/edit_problem?problem_id=<%=problem.getId()%>'
                });
            });
        </script>
<%
    }
%>
</body>