<%@ page import="com.kk.teachme.model.Problem" %>
<%@ page import="com.kk.teachme.model.Tag" %>
<%@ page import="com.kk.teachme.model.Solution" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<style>
    .panel {
        width: 60%;
    }
    .problem:hover {
        border-color: #66afe9;
        outline: 0;
        -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075), 0 0 8px rgba(102, 175, 233, 0.6);
        box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075), 0 0 8px rgba(102, 175, 233, 0.6);
        cursor: pointer;
    }
</style>

<body>

<%
    if (request.getAttribute("problem") != null) {
        Problem problem = (Problem)request.getAttribute("problem");
        Solution solution = (Solution)request.getAttribute("solution");
%>

        <div class="problem panel panel-info affix" value="<%=problem.getId()%>" onclick="document.location.href = '/edit_problem?problem_id=<%=problem.getId()%>'">
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
                            <img src="/files/<%=figure%>" style="height: 30%; max-width: 90%;"/>
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
            Тип ответа: <span class="label"><%= solution.getChecker().getName() %></span><br>
            Ответ: <span class="label label-success"><%= solution.getSolutionText() %></span>
        </div>
<%
    }
%>

</body>