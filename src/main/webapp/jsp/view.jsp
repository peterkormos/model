<%@page import="java.util.*"%>

<%@page import="datatype.*"%>
<%@page import="servlet.*"%>

<html>
<body>
	<%
	  String show = (String) session.getAttribute("show");
	  if (show == null)
	  {
			RegistrationServlet servlet = RegistrationServlet.getInstance(config);
			ServletDAO servletDAO = servlet.getServletDAO();

			List<String> shows = servletDAO.getShows();
			if (shows.isEmpty())
			  show = "-";
			else
			  show = shows.get(0);
	  }

	  User user = new User("HU");
	  session = request.getSession(true);
	  session.setAttribute("userID", user);
	%>
	Verseny:
	<FONT COLOR='#ff0000'><b><%=show%></b></FONT>
	<p>
	<hr>
	<p></p>
	<em><strong>Lek&eacute;rdez&eacute;sek...</strong></em>
	</p>

	<form name="input" id="input" action="../RegistrationServlet"
		method="post">
		<input type="hidden" id="command" name="command" value=""> <input
			type="hidden" name="withDetailing" value="false"> <a href="#"
			onClick="document.getElementById('input').action='listAllModels.jsp';this.parentNode.submit();">&Ouml;sszes
			benevezett makett list&aacute;z&aacute;sa</a> - <a href="#"
			onClick="document.getElementById('command').value='statistics';this.parentNode.submit();"><strong>Statisztika
				k&eacute;sz&iacute;t&eacute;se</strong></a> - <a href="customQuery.jsp">Egyedi
			lek&eacute;rdez&eacute;sek</a>
	</form>

	<form name="input" id="input1" action="../RegistrationServlet"
		method="post">
		<input type="hidden" id="command1" name="command" value=""> <input
			type="hidden" name="withDetailing" value="true"> <a href="#"
			onClick="document.getElementById('input1').action='listAllModels.jsp';this.parentNode.submit();">&Ouml;sszes
			benevezett makett list&aacute;z&aacute;sa (r&eacute;szletes)</a> - <a
			href="#"
			onClick="document.getElementById('command1').value='listCategories';this.parentNode.submit();"><strong>&Ouml;sszes
				kateg&oacute;ria list&aacute;z&aacute;sa</strong></a> - <a href="#"
			onClick="document.getElementById('command1').value='listUsers';this.parentNode.submit();">&Ouml;sszes
			felhaszn&aacute;l&oacute; list&aacute;z&aacute;sa</a>
	</form>

	<form name="input" id="input1a" action="../RegistrationServlet"
		method="post">
		<input type="hidden" name="onlyPhotos" value="true"> <a
			href="#"
			onClick="document.getElementById('input1a').action='listAllModels.jsp';this.parentNode.submit();">&Ouml;sszes
			benevezett makett list&aacute;z&aacute;sa (csak k&eacute;pekkel)</a>
	</form>

	<p>
		<em><strong>Nyomtat&aacute;s...</strong></em>
	</p>
	<form name="input" action="../RegistrationServlet" method="post">
		<input type="hidden" id="command2" name="command" value=""> <input
			type="hidden" name="printPreRegisteredModels" value="true"> <a
			href="#"
			onClick="document.getElementById('command2').value='printAllModels';this.parentNode.submit();">Nevez&eacute;si
			lapok nyomtat&aacute;sa (El&ouml;nevezettek)</a>
	</form>

	<p></p>
	<form name="input" id="input" action="../RegistrationServlet"
		method="post">
		<input type="hidden" id="command3" name="command" value=""> <input
			type="hidden" name="printPreRegisteredModels" value="false">
		<!--
    <a href="#" onClick="document.getElementById('command3').value='printAllModels';this.parentNode.submit();">Nevez&eacute;si 
    lapok nyomtat&aacute;sa (Helysz&iacute;nen nevezettek)</a> - 
-->

		<a href="#"
			onClick="document.getElementById('command3').value='printCardsForAllModels';this.parentNode.submit();">A
			makettek mell&eacute; kisk&aacute;rty&aacute;k nyomtat&aacute;sa</a>
	</form>
</body>
</html>
