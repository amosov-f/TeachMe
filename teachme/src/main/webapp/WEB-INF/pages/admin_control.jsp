<%--
  Created by IntelliJ IDEA.
  User: monvir
  Date: 02.04.14
  Time: 0:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <script type="text/javascript" src="/resources/jquery/js/jquery-2.1.0.js"></script>

    <title>Добавление админа</title>
</head>
<body>
    <input id="adminId">
    <button id="submit" onclick="submit()">Отправить</button>
    <p id="output"></p>

    <script>
        function submit() {
            $.ajax({
                url: '/add_admin',
                data: 'admin_id=' + $('#adminId').val(),
                success: function(data) {
                    $('#output').text($('#output').text() + '\n' + data);

                }
            });

        }
    </script>




</body>
</html>
