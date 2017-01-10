<%@page import="java.util.*"%>

<%@page import="datatype.*"%>
<%@page import="servlet.*"%>

<%
  //input parameters
  String selectedValue = request.getParameter("selectedValue");
  String selectedLabel = request.getParameter("selectedLabel");
  boolean mandatory = Boolean.parseBoolean(request.getParameter("mandatory"));

  final String show = RegistrationServlet.getShowFromSession(session);
  RegistrationServlet servlet = RegistrationServlet.getInstance(config);
  ServletDAO servletDAO = servlet.getServletDAO();

  User user = RegistrationServlet.getUser(request);
%>

<div id='categories'>
	<select name='categoryID'>

		<option value='<%=selectedValue%>'><%=selectedLabel%></option>

		<%
		List<Category> categoryList = servletDAO.getCategoryList(show);
		
		  for (final CategoryGroup group : servletDAO.getCategoryGroups())
		  {
				if (show != null && !group.show.equals(show))
				{
				  // System.out.println(group.show + " " + show);
				  continue;
				}
		%>
		<optgroup label='<%=group.show%> - <%=group.name%>'>
			<%
			  for (final Category category : categoryList)
					{
					  if (category.group.categoryGroupID != group.categoryGroupID)
					  {
						continue;
					  }

					  if (user.getModelClass().contains(category.getModelClass()) && !category.isMaster())
					  {
						continue;
					  }

					  if (!category.getAgeGroup().contains(AgeGroup.get(user.getAge())))
					  { 
						continue;
					  }
			%>
			<option value='<%=category.categoryID%>'>
				<%=category.categoryCode + " - " + category.categoryDescription%>
			</option>

			<%
			  }
			%>
		</optgroup>
		<%
		  }
		%>
	</select>

	<%
	  if (mandatory)
	  {
	%>
	<font color='#FF0000' size='+3'>&#8226;</font>
	<%
	  }
	%>
</div>
