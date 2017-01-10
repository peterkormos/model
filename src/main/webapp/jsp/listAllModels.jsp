<%@page import="java.util.*"%>

<%@page import="datatype.*"%>
<%@page import="servlet.*"%>

<%
  RegistrationServlet servlet = RegistrationServlet.getInstance(config);
  ServletDAO servletDAO = servlet.getServletDAO();

  User user = servlet.getUser(request);

  final List<Model> models = servletDAO.getModels(ServletDAO.INVALID_USERID);

	final String show = RegistrationServlet.getShowFromSession(session);
	final Iterator<Model> it = models.iterator();
	while (it.hasNext())
	{
	  final Model model = it.next();

	  if (show != null && !servletDAO.getCategory(model.categoryID).group.show.equals(show))
	  {
		it.remove();
	  }
	}

  session.setAttribute("models", models);
%>

<jsp:include page="listModels.jsp"></jsp:include>

<%
  session.removeAttribute("models");
%>