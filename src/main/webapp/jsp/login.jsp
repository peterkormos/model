<%@page import="datatype.*"%>
<%@page import="servlet.*"%>

<%@page import="java.util.*"%>

<%
  RegistrationServlet servlet = RegistrationServlet.getInstance(config);
  ServletDAO servletDAO = servlet.getServletDAO();

  final String languageCode = ServletUtil.getRequestAttribute(request, "language");
  ResourceBundle language = servlet.getLanguage(languageCode);
%>

<html>
<head>
</head>

<link rel="stylesheet" href="base.css" media="screen" />

<body>
	<form  name="input" 
		action="../RegistrationServlet" method="post">

		<input type="hidden" name="command" value="login">
		<input type="hidden" name="language" value="<%=languageCode%>">

		<table border="0" height="100%" width="100%">
			<tr>
				<td align="center" valign="middle">
					<table border="0">
						<tr>
							<td colspan="2"><FONT COLOR='#ffffff'>Verzi&oacute;:
									<%=servlet.getVersion()%></FONT></td>
						</tr>
						<tr>
							<td colspan="2"><FONT COLOR='#ff0000'><b><%=servlet.getSystemMessage()%></b></FONT></td>
						</tr>
						<tr bgcolor='F6F4F0'>
							<td><%=language.getString("show")%>:</td>
							<td>
								<%
								  final List<String> shows = servletDAO.getShows();
								  for (final String show : shows)
								  {
								%> <input type='radio' name='show' value='<%=StringEncoder.toBase64(show.getBytes())%>'
								<%=(shows.size() == 1 ? " checked='checked'" : "")%> /> <FONT
								COLOR='#ff0000'><b> <%=show%></b></FONT><br> <%
   }
 %>
							</td>
						</tr>
						<tr>
							<td><FONT COLOR='#ff0000'><b><%=language.getString("email")%></b></FONT>:</td>
							<td><input type="text" name="email"></td>
						</tr>
						<tr bgcolor='F6F4F0'>
							<td><%=language.getString("password")%>:</td>
							<td><input type="password" name="password">
								<p>
									<a href="reminder.jsp?language=<%=languageCode%>"><%=language.getString("password.reminder")%></a></td>
						</tr>
						<tr>
							<td colspan="2" align="center"><input name="submit"
								type="submit" value="<%=language.getString("login")%>">
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<%
								  if (!servlet.isPreRegistrationAllowed())
								  {
								%> <strong><font color='#FF0000'>Model
										pre-registration has been closed. You won't be able to add or
										modify your models! <br> - <br> A makett
										el&otilde;nevez&eacute;s lez&aacute;rult. A makettek
										felv&eacute;tele &eacute;s m&oacute;dos&iacute;t&aacute;sa
										ezut&aacute;n m&aacute;r nem lehets&eacute;ges!
								</font></strong> <%
   }
 %>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>

	</form>
</body>
</html>

