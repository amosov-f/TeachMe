<%@ page import="com.kk.teachme.model.Problem" %>
<html>
<head>
</head>
<body>
<% Problem problem = (Problem) request.getAttribute("name");%>
<%=problem.getName()%>
Hello!

<script>
    alert("Hello!");

</script>
</body>
</html>
