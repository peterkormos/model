<%@page import="datatype.*"%>
<%@page import="servlet.*"%>

<%@page import="java.util.*"%>

<%
	RegistrationServlet servlet = RegistrationServlet.getInstance();
	ServletDAO servletDAO = servlet.getServletDAO();

	final String languageCode = servlet.getRequestAttribute(request, "language");
	ResourceBundle language = servlet.getLanguage(languageCode);
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>

<body>
<link href="css/base.css" rel="stylesheet" type="text/css" />

Verzi&oacute;: <%= servlet.getVersion() %>

<p>
<p>

<div class="header"></div>
<form accept-charset="UTF-8" name="input" action="../RegistrationServlet" method="put">

<input type="hidden" name="command" value="login"> 
<input type="hidden" name="language" value="<%= languageCode %>">

<p><FONT COLOR='#ff0000'><b><%= servlet.getSystemMessage() %></b></FONT></p>

<table border="1">
	<tr>
		<td>
		<table border="0">
			<tr bgcolor='F6F4F0'>
				<td><%= language.getString("show") %>:</td>
				<td>
<%
	final List<String> shows = servletDAO.getShows();
	for (final String show : shows)
	{
%>	
	  <input type='radio' name='show' value='<%= show %>' <%= (shows.size() == 1 ? " checked='checked'" : "") %> />
	  <FONT COLOR='#ff0000'><b> <%= show %></b></FONT><br>
<%	}

%>
				</td>
			</tr>
			<tr>
				<td><FONT COLOR='#ff0000'><b><%= language.getString("email") %></b></FONT>:</td>
				<td><input type="text" name="email"></td>
			</tr>
			<tr bgcolor='F6F4F0'>
				<td><%= language.getString("password") %>:</td>
				<td><input type="password" name="password">
				<p>
				<a href="reminder.jsp?language=<%= languageCode %>"><%= language.getString("password.reminder") %></a>
				</td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<input name="submit" type="submit" value="<%= language.getString("login") %>">
				</td>
			</tr>
		</table>
		</td>
	</tr>
</table>
<%
	if (!servlet.isPreRegistrationAllowed())
	{
%>
	  <p> 
	  <strong><font color='#FF0000'>Model pre-registration has been closed. 
	  You won't be able to add or modify your models!
	  <br> 
	  - 
	  <br> 
	  A makett el&otilde;nevez&eacute;s lez&aacute;rult. 
	  A makettek felv&eacute;tele &eacute;s m&oacute;dos&iacute;t&aacute;sa ezut&aacute;n m&aacute;r nem lehets&eacute;ges! 
	  </font></strong>
	  <p>
<%
	}
%>

<p><FONT COLOR='#ff0000'><b><%= servlet.getSystemMessage() %></b></FONT></p>
<p>
</p>
</form>
</body>
</html>

