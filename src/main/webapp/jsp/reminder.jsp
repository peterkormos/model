<%@page import="datatype.*"%>
<%@page import="servlet.*"%>

<%@page import="java.util.*"%>

<%
	RegistrationServlet servlet = RegistrationServlet.getInstance();
	ResourceBundle language = servlet.getLanguage(request.getParameter("language"));
%>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>

<body><html>
<link href="css/base.css" rel="stylesheet" type="text/css"/>
<div class="header"></div>

<form accept-charset="UTF-8" name="input" action="../RegistrationServlet" method="put">

<input type="hidden" name="command" value="reminder">

  <table border="0">
    <tr> 
      <td><%= language.getString("email") %>:</td>
      <td><input type="text" name="email"> </td>
    </tr>
    <tr> 
      <td></td>
      <td><input type="submit" value="<%= language.getString("send.reminder") %>"></td>
    </tr>
  </table>

</form>
</body></html>
