<%@page import="datatype.*"%>
<%@page import="servlet.*"%>

<%@page import="java.util.*"%>

<%
	RegistrationServlet servlet = RegistrationServlet.getInstance(config);
	final String languageCode = ServletUtil.getRequestAttribute(request, "language");
	ResourceBundle language = servlet.getLanguage(languageCode);
%>

<%=language.getString("select.another.email")%>
<p>
<a href="jsp/reminder.jsp?language=<%= languageCode %>"><%= language.getString("password.reminder") %></a>
<p>
