<%@page import="datatype.*"%>
<%@page import="servlet.*"%>

<%@page import="java.util.*"%>

<%
  User user = RegistrationServlet.getUser(request);
  RegistrationServlet servlet = RegistrationServlet.getInstance(config);
  final ResourceBundle language = servlet.getLanguage(user.language);
%>

<html>
<body>
	<form name='input' action='listSelectedModels.jsp' method='put'>
		<table border='0'>

			<tr>
				<td><%=language.getString("category.code")%> :</td>

				<td>
					<%
					  String categoryLabel = language.getString("select");
					  String categoryLabelValue = "";
					%> <jsp:include page="categories.jsp">
						<jsp:param name="selectedLabel" value="<%=categoryLabel%>" />
						<jsp:param name="selectedValue" value="<%=categoryLabelValue%>" />
						<jsp:param name="mandatory" value="true" />
					</jsp:include>

				</td>
			</tr>

			<tr>
				<td><%=language.getString("userID")%> :</td>
				<td><input type='text' name='userID'></td>
			</tr>

			<tr>
				<td><%=language.getString("modelID")%> :</td>
				<td><input type='text' name='modelID'></td>
			</tr>

			<tr>
				<td><%=language.getString("models.name")%> (kis- nagybetu
					szamit):</td>
				<td><input type='text' name='modelname'></td>
			</tr>

			<td><%=language.getString("models.markings")%> :</td>
			<td>
				<%
				  String markingsLabel = language.getString("select");
				  String markingsLabelValue = "";
				%> <jsp:include page="countries.jsp">
					<jsp:param name="defaultSelectedLabel" value="<%=markingsLabel%>" />
					<jsp:param name="defaultSelectedValue"
						value="<%=markingsLabelValue%>" />
					<jsp:param name="selectName" value="markings" />
				</jsp:include>
			</td>
			</tr>

			<tr>
				<td><%=language.getString("models.producer")%> :</td>
				<td><jsp:include page="modelProducers.jsp">
						<jsp:param name="selectValue" value="" />
						<jsp:param name="frequentlyUsed"
							value='<%=language.getString("frequently.used")%>' />
					</jsp:include></td>
			</tr>

			<tr>
				<td></td>
				<td><input name='selectModel' type='submit'
					value='<%=language.getString("select.models")%>'></td>
			</tr>
		</table>
	</form>
</body>
</html>
