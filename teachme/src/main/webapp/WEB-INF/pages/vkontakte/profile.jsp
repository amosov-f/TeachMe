<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>

<h3>Your VK Profile</h3>
<p>Hello, <c:out value="${profile.firstName}"/>!</p>
<dl>
	<dt>VKontakte ID:</dt>
	<dd><c:out value="${profile.uid}"/></dd>
	<dt>Name:</dt>
	<dd><c:out value="${profile.screenName}"/></dd>
</dl>

<c:url value="/connect/vkontakte" var="disconnectUrl"/>
<form id="disconnect" action="${disconnectUrl}" method="post">
	<button type="submit">Disconnect from VK</button>
	<input type="hidden" name="_method" value="delete" />
</form>