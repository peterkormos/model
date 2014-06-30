<%@page import="java.util.*"%>

<%@page import="datatype.*"%>
<%@page import="servlet.*"%>

<form name='input' action='../RegistrationServlet' method='put'>
<input type='hidden' name='command' value='<%= request.getParameter("action") %>'>
<%
	RegistrationServlet servlet = RegistrationServlet.getInstance(config);
	User user = servlet.getUser(request);

	for (final Model model : servlet.getServletDAO().getModels(user.userID))
	{
%>
		<input type='radio' name='modelID' value='<%=model.modelID%>'/>
		<%=model.scale%>
		-
		<%=model.producer%> 
		-
		<%=model.name%>
		<br>
<%
	}
%>
<p>
<input name='<%= request.getParameter("action") %>' type='submit' value='<%= request.getParameter("submitLabel") %>'>
</form>
