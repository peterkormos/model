<%@page import="java.util.*"%>

<%@page import="datatype.*"%>
<%@page import="servlet.*"%>

<%
  RegistrationServlet servlet = RegistrationServlet.getInstance(config);
  ServletDAO servletDAO = servlet.getServletDAO();

  User user = servlet.getUser(request);

  final int rows = Integer.parseInt(ServletUtil.getRequestAttribute(request, "rows"));

  final List<Model> models = new LinkedList<Model>();
  for (int i = 1; i <= rows; i++)
  {
		final String httpParameterPostTag = String.valueOf(i);

		final String modelID = ServletUtil.getOptionalRequestAttribute(request, "modelID" + httpParameterPostTag).trim();
		if (modelID.length() == 0 || "-".endsWith(modelID))
		{
		  continue;
		}

		final Model model = servletDAO.getModel(Integer.parseInt(modelID));
		model.award = ServletUtil.getRequestAttribute(request, "award" + httpParameterPostTag).trim();
		models.add(model);
  }

  session.setAttribute("models", models);
%>

<jsp:include page="listModels.jsp"></jsp:include>

<%
  session.removeAttribute("models");
%>