<%@page import="datatype.*"%>
<%@page import="servlet.*"%>

<%@page import="java.util.*"%>

<%
	//input parameters	
	String submitLabel = (String)session.getAttribute("submitLabel");
	String action = (String)session.getAttribute("action");
	Integer modelID = (Integer)session.getAttribute("modelID");
 		
	User user = RegistrationServlet.getUser(request);
	
	RegistrationServlet servlet = RegistrationServlet.getInstance();
	final ResourceBundle language = servlet.getLanguage(user.language);
	ServletDAO servletDAO = servlet.getServletDAO();

	Model model = null;
	if(modelID != null)
		model = servletDAO.getModel(modelID);
		
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<form accept-charset="UTF-8" name='input' action='../RegistrationServlet' method='put'>
<input type='hidden' name='command' value='<%= action %>'>

	<!-- modelID for modify...-->
<%
	if (model != null)
	{
%>
		<input type='hidden' name='modelID' value='<%=model.modelID%>'>
<%
	}
%>
<p><font color='#FF0000' size='+3'>&#8226;</font> <%=language.getString("mandatory.fields")%></p>

<table border='0'>

	<!--	scale-->
<tr>
<td>
<%=language.getString("scale")%>
: 
</td>
<td>
<%
String scale = model == null ? "" : model.scale;
%>
<jsp:include page="scales.jsp">
  <jsp:param name="selectValue" value="<%=scale%>"/>
  <jsp:param name="frequentlyUsed" value='<%=language.getString("frequently.used")%>'/>
</jsp:include>
</td>
</tr>

	<!--	models.name-->
<tr bgcolor='F6F4F0'>
<td>
<%=language.getString("models.name")%>
: 
</td>
<td>
<%
String modelname = model == null ? "" : model.name;
%>
	<jsp:include page="textInput.jsp">
	  <jsp:param name="name" value="modelname"/>
	  <jsp:param name="value" value="<%=modelname%>"/>
	  <jsp:param name="mandatory" value="true"/>
	</jsp:include>
</td>
</tr>

	<!--	models.producer-->
<tr>
<td>
<%=language.getString("models.producer")%>
: 
</td>
<td>
<%
String modelproducer = model == null ? "" : model.producer;
%>
<jsp:include page="modelProducers.jsp">
  <jsp:param name="selectValue" value="<%=modelproducer%>"/>
  <jsp:param name="frequentlyUsed" value='<%=language.getString("frequently.used")%>'/>
</jsp:include>
</td>
</tr>

	<!--	glued.to.base-->
<tr bgcolor='F6F4F0'>
<td>
<%=language.getString("glued.to.base")%>
: 
</td>

<td>
<%=language.getString("yes")%>
 <input name='gluedToBase' type='radio' value='on' <%=(model == null || !model.gluedToBase ? "" : "checked='checked'")%> >
<br>
<%=language.getString("no")%>
 <input name='gluedToBase' type='radio' value='off' <%=(model == null || !model.gluedToBase ? "checked='checked'" : "")%> >
 <font color='#FF0000' size='+3'>&#8226;</font> </td>
</tr>

	<!--	category-->
<tr>
<td>
<%= language.getString("category") %>:
</td>

<td>
<%
	final Category category = model == null ? null : servletDAO.getCategory(model.categoryID);

	String categoryLabel = model == null ? language.getString("select") : category.categoryCode + " - "
	    + category.categoryDescription;
	   
	String categoryLabelValue = model == null ? "" : String.valueOf(category.categoryID);
%>
<jsp:include page="categories.jsp">
  <jsp:param name="selectedLabel" value="<%= categoryLabel %>"/>
  <jsp:param name="selectedValue" value="<%= categoryLabelValue %>"/>
  <jsp:param name="mandatory" value="true"/>
</jsp:include>
</td>
</tr>

	<!--	comment-->
<tr bgcolor='F6F4F0'>
<td>
<%=language.getString("comment")%>
: 
</td>
<td><textarea name='modelcomment' cols='50' rows='10'>

<%=  (model != null) ? model.comment : "" %>

</textarea></td>
</tr>

	<!--	submit-->
<tr>
<td></td>
<td><input name='<%= action %>' type='submit' value='<%= language.getString(submitLabel) %>'></td>
</tr>

	<!--	models.markings-->
<tr>
<td>
<%=language.getString("models.markings")%>
: 
</td>
<td>
<%
String markingsLabel =  model == null ? language.getString("select") : model.markings;
String markingsLabelValue = model == null ? "-" : model.markings;
%>
<jsp:include page="countries.jsp">
  <jsp:param name="defaultSelectedLabel" value="<%= markingsLabel %>"/>
  <jsp:param name="defaultSelectedValue" value="<%= markingsLabelValue %>"/>
  <jsp:param name="selectName" value="markings"/>
</jsp:include>
</td>
</tr>

	<!--	models.identification-->
<tr bgcolor='F6F4F0'>
<td>
<%=language.getString("models.identification")%>
: 
</td>
<td>
<%
String modelidentification = model == null ? "" : model.identification;
%>
	<jsp:include page="textInput.jsp">
	  <jsp:param name="name" value="identification"/>
	  <jsp:param name="value" value="<%= modelidentification %>"/>
	  <jsp:param name="mandatory" value="false"/>
	</jsp:include>
</td>
</tr>

	<!--	models.detailing-->
<tr>
<td>
<%=language.getString("models.detailing")%>
: 
</td>

<td><table cellpadding='5' border='1'>
<tr>
<td>&nbsp;</td>
<%
	for (int i = 0; i < Detailing.DETAILING_GROUPS.length; i++)
	{
%>
<td>
	  <%=language.getString("detailing." + Detailing.DETAILING_GROUPS[i])%>
</td>
<%
	}
%>
</tr>
<%
	for (int i = 0; i < Detailing.DETAILING_CRITERIAS.length; i++)
	{
%>
<tr>
<td>
	  <%=language.getString("detailing." + Detailing.DETAILING_CRITERIAS[i])%>
</td>
<%
	  for (int j = 0; j < Detailing.DETAILING_GROUPS.length; j++)
	  {
%>
	<td><input name='<%= "detailing." + Detailing.DETAILING_GROUPS[j] + "." + Detailing.DETAILING_CRITERIAS[i] %>' 
	type='checkbox' value='on' <%= (model == null || !model.detailing[j].criterias[i] ? "" : "checked='checked'")%> ></td>
<%	
	  }
%>
</tr>
<%
	}
%>
</table></td>
</tr>

	<!--	submit -->
<tr>
<td></td>
<td><input name='<%= action %>' type='submit' value='<%= language.getString(submitLabel) %>'></td>
</tr>

</table>

</form></body></html>


