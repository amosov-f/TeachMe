<%@ page import="com.kk.teachme.model.Problem" %>
<%@ page import="com.kk.teachme.checker.Checker" %>
<%@ page import="com.kk.teachme.checker.RadioChecker" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <link href="/resources/utility/css/styles.css" rel="stylesheet" type="text/css"/>
</head>

<body>
<%
    if (request.getAttribute("problem") != null && request.getAttribute("checker") != null) {
        Problem problem = (Problem) request.getAttribute("problem");
        Checker checker = (Checker) request.getAttribute("checker");
%>
        <div id="userProblemPanel" name="<%= problem.getId() %>" class="panel panel-info margin-top" value="<%=problem.getId()%>">
            <div class="panel-heading">
                <%= problem.getName()%>
                <div class="pull-right" style="margin-top: 4px;">
                    <span class="label label-primary"><%= problem.getComplexity() %></span>
                </div>
                <div class="hidden-xs pull-right">
                    Сложность&nbsp
                </div>
            </div>
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

                <div class="container">
                    <div class="col-lg-4 col-md-4 col-sm-5 col-xs-12">
                    <%
                        if (!(checker instanceof RadioChecker)) {
                    %>
                            <div class="form-group">
                                <input id="solution" name="solution" class="form-control col-lg-12 col-xs-12" type="text" placeholder="Ваш ответ" />
                            </div>
                    <%
                        }
                    %>
                        <div class="form-group">
                            <button id="submit" class="btn btn-primary col-lg-12 col-xs-12">
                                Отправить
                            </button>
                        </div>
                    </div>
                    <div class="col-lg-8 col-md-8 col-sm-7 col-xs-12">
                        <div id="status"></div>
                    </div>

                </div>
            </div>
        </div>
<%
    }
%>

    <script>

        $(document).ready(function() {

            $('#submit').click(submit);
            //$('#solution').focus();
            $('#solution').keypress(function(e) {
                if (e.which == 13) {
                    submit();
                }
            });

            $.ajax({
                url: '/read',
                data: 'problem_id=' + <%= ((Problem) request.getAttribute("problem")).getId() %>
            });
        });

        function submit() {
            var problemId =  $('#userProblemPanel').attr('name');

            var solution;
            var $solution = $('#solution');
            if ($solution.length > 0) {
                solution = $solution.val();
            } else {
                solution = $("input[type=radio]:checked").val();
                if (solution == null) {
                    solution = '';
                }
            }

            $.ajax({
                url: '/submit',
                data: 'problem_id=' + problemId + '&solution_text=' + solution,
                beforeSend: function() {
                    $('#status').html('');
                    //$('#status').visible(false);
                },
                success: function(data) {
                    $('#status').html(data);
                    $('#solution').select();

                    $.ajax({
                        url: '/user_problem_item',
                        data: 'problem_id=' + problemId,
                        success: function(data) {
                            $('#' + problemId).html(data);
                        }
                    });
                }
            });
        }

    </script>

</body>