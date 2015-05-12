<%@page import="datatype.*"%>
<%@page import="servlet.*"%>

<%@page import="java.util.*"%>

<%
  RegistrationServlet servlet = RegistrationServlet.getInstance(config);
  ResourceBundle language = servlet.getLanguage(request.getParameter("language"));
%>

<html>
<head>
</head>

<link rel="stylesheet" href="base.css" media="screen" />

<body>
	<link href="css/base.css" rel="stylesheet" type="text/css" />
	<div class="header"></div>

	<form name="input" action="../RegistrationServlet/reminder"
		method="put">

		<table border="0">
			<tr>
				<td><%=language.getString("email")%>:</td>
				<td><input type="text" name="email"></td>
			</tr>
			<tr>
				<td></td>
				<td><input type="submit"
					value="<%=language.getString("send.reminder")%>"></td>
			</tr>
		</table>

	</form>
</body>
</html>
