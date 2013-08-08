<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ page session="false" %>

<h3>Connected to VKontakte</h3>

<form id="disconnect" method="post">
	<div class="formInfo">
		<p>
			Spring Social Showcase is connected to your VKontakte account.
			Click the button if you wish to disconnect.
		</p>
	</div>
	<button type="submit">Disconnect</button>
	<input type="hidden" name="_method" value="delete" />
</form>

<a href="<c:url value="/vkontakte"/>">View your VKontakte profile</a>