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
                        <div class="form-group">
                            <input id="solution" name="solution" class="form-control col-lg-12 col-xs-12" type="text" placeholder="Ваш ответ" />
                        </div>
                        <div class="form-group">
                            <button id="submit" class="btn btn-primary btn-lg col-lg-12 col-xs-12">
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
            $('#solution').focus();
            $('#solution').keypress(function(e) {
                if (e.which == 13) {
                    submit();
                }
            });

            $.ajax({
                url: '/read',
                data: 'problem_id=' + <%= ((Problem)request.getAttribute("problem")).getId() %>
            });
        });

        function submit() {
            var problemId =  $('#userProblemPanel').attr('name');
            $.ajax({
                url: '/submit',
                data: 'problem_id=' + problemId + '&solution_text=' + $('#solution').val(),
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