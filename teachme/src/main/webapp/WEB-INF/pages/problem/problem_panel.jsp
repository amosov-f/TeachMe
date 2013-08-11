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
        Problem problem = (Problem)request.getAttribute("problem");
        Solution solution = (Solution)request.getAttribute("solution");
%>

        <script>
            $(document).ready(function() {
                $('#problemPanel').click(function() {
                    document.location.href = '/edit_problem?problem_id=<%=problem.getId()%>'
                });
            });
        </script>


        <div id="problemPanel" class="problem panel panel-info affix" value="<%=problem.getId()%>">
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
            <span class="label"><%= solution.getChecker().getName() %></span>
            <span class="label label-success"><%= solution.getSolutionText() %></span>
        </div>
<%
    }
%>
</body>