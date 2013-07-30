<%@ page import="com.kk.teachme.model.Problem" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: Mary
  Date: 12.07.13
  Time: 21:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>

<%  for (Problem problem : (List<Problem>)request.getAttribute("problemList")) {    %>
        <%=problem%><br>
<%      for (String figure : problem.getFigures()) {       %>
            <%="<img src='http://localhost:8080/files/" + figure + "' style='width: 30%; height: 30%' />"%><br>
<%      }   %>
        <br>
<%  }   %>

</body>
</html>