<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>

<h3>Connect to VKontakte</h3>

<form action="<c:url value="/connect/vkontakte" />" method="POST">
	<input type="hidden" name="scope" value="notify,friends,photos,audio,video,notes,pages,wall,messages,offline" />
	<div class="formInfo">
		<p>You aren't connected to VKontakte yet. Click the button to connect Spring Social Showcase with your VKontakte account.</p>
	</div>
	<p><button type="submit"><img src="<c:url value="/resources/social/vkontakte/connect_short.png" />"/></button></p>
</form>