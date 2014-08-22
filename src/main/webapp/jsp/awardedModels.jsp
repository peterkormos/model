<%@page import="java.util.List"%>

<%@page import="datatype.*"%>
<%@page import="servlet.*"%>


<%
  final String CATEGORY_ID = "categoryID";
%>
<body>
<hr>
<p><font size="+1">Kateg&oacute;ri&aacute;k</font></p>
<p>
<%
  List<AwardedModel> awardedModels = RegistrationServlet.servletDAO
      .getAwardedModels();

  List<Category> categories = RegistrationServlet.servletDAO
      .getCategoryList((String)session.getAttribute("show"));

  for (Category category : categories)
  {
%> <a href="awardedModels.jsp?<%=CATEGORY_ID%>=<%=category.categoryID%>"><%=category.categoryCode%>
- <%=category.categoryDescription%></a> <br>
<%
  }

  String categoryID = servlet.RegistrationServlet
      .getOptionalRequestAttribute(request, CATEGORY_ID);

  if (!"-".equals(categoryID))
  {
    categories.clear();
    categories.add(RegistrationServlet.servletDAO.getCategory(Integer
        .parseInt(categoryID)));
  }

  for (Category category : categories)
  {
%>

<hr>
<p><font size="+1">Kateg&oacute;ria: <%=category.categoryCode%>
- <%=category.categoryDescription%></font></p>
<table width="100%" border="1">
	<tr>
		<td>
		<table width="100%" border="0">
			<tr>
				<th width="25%" >[Makett]</th>
				<th width="25%" >[Makettez&otilde;]</th>
				<th width="25%" >[Helyez&eacute;s]</th>
				<%
//				  if (RegistrationServlet.onSiteUse)
				    {
				%>
				<th width="25%" >[K&eacute;p]</th>
				<%
				  }
				%>
			</tr>
			<%
			  for (AwardedModel awardedModel : awardedModels)
			    {
			      if (awardedModel.model.categoryID != category.categoryID)
			        continue;
			%>
			<tr>
				<td align="center"><%=awardedModel.model.name%></td>
				<td align="center"><%=RegistrationServlet.servletDAO.getUser(
                  awardedModel.model.userID).getFullName()%></td>
				<td align="center"><%=awardedModel.award%></td>
				<%
//				  if (RegistrationServlet.onSiteUse)
				      {
				%>
				<td align="center"><a
					href="./gallery/<%=awardedModel.model.modelID%>.jpg"> <img
					src="./gallery/<%=awardedModel.model.modelID%>.jpg" width="128">
				</a></td>
				<%
				  }
				%>
			</tr>
			<%
			  }
			%>
		</table>
		</td>
	</tr>
</table>
<%
  }
%>

</body>
</html>