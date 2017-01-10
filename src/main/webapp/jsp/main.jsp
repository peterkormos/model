<%@page import="java.util.*"%>

<%@page import="datatype.*"%>
<%@page import="servlet.*"%>

<%
	RegistrationServlet servlet = RegistrationServlet.getInstance(config);
	User user = RegistrationServlet.getUser(request);

	if (user.language.length() != 2)
	{
	  response.sendRedirect(user.language + "_main.jsp");
	  return;
	}

	final ResourceBundle language = servlet.getLanguage(user.language);
	String show = RegistrationServlet.getShowFromSession(session);
	if (show == null)
	{
	  show = "-";
	}
%>
<html>
<head>
</head>
<body>

<link href="base.css" rel="stylesheet" type="text/css"/>
<div class="header"></div>

<p>
<FONT COLOR='#ff0000'><b><%=language.getString("show")%>: <%=show%>
<p>
<p>
<%=servlet.getSystemMessage()%>
</b></FONT>

<p></p>
<p></p>

<script type="text/javascript">
// <!--

function checkSubmit()
{
	confirmed = confirm('Are you sure you want to delete?'); 
	
	if(confirmed)
	{
		document.getElementById('command').value='deleteUser';
		document.getElementById('input').submit();
	}
}

//-->
</script>


<form name="input" id="input" onsubmit="myFunction()" action="../RegistrationServlet" method="put">
  <input type="hidden" id="command"  name="command" value="">

  <a href="#" onClick="document.getElementById('command').value='inputForAddModel';this.parentNode.submit();">
  <img src="../icons/add.png" height="30" align="center"> <%=language.getString("add")%></a>

<p></p>
    
  <a href="#" onClick="document.getElementById('command').value='inputForPhotoUpload';this.parentNode.submit();">
  <img src="../icons/photo.png" height="30" align="center"> <%=language.getString("photo")%>
  </a>
 
<p></p>
   
  <a href="#" onClick="document.getElementById('command').value='inputForSelectModelForModify';this.parentNode.submit();">
  <img src="../icons/modify.png" height="30" align="center"> <%=language.getString("modify.model")%></a>

<p></p>
   
  <a href="#" onClick="document.getElementById('command').value='inputForDeleteModel';this.parentNode.submit();">
  <img src="../icons/delete2.png" height="30" align="center"> <%=language.getString("delete")%></a>

<p></p>

<%-- 
  <a href="#" onClick="document.getElementById('command').value='listMyModels';this.parentNode.submit();">
  <img src="../icons/list.png" height="30" align="center"> <%=language.getString("list.models")%></a>

<p></p>
 --%>

  <a href="#" onClick="document.getElementById('command').value='sendEmail';this.parentNode.submit();">
  <img src="../icons/email.png" height="30" align="center"> <%=language.getString("send.email")%></a>

<p></p>
    
  <a href="#" onClick="document.getElementById('command').value='logout';this.parentNode.submit();">
  <img src="../icons/exit.png" height="30" align="center"> <%=language.getString("logout")%></a>

<p></p>
   
  <a href="#" onClick="document.getElementById('command').name='action';document.getElementById('command').value='modifyUser';document.getElementById('input').action='user.jsp';this.parentNode.submit();">
  <img src="../icons/modify2.png" height="30" align="center"> <%=language.getString("modify.user")%></a>

<p></p>

<hr>
	<a href="#" onClick="checkSubmit();" style="{color: rgb(255,0,0);font-weight:bold}">
  <img src="../icons/delete.png" height="30" align="center"> <%=language.getString("delete.user")%></a>

</form>
<hr>
<%
if(servlet.isOnSiteUse())
{
%>
	<hr>
	<a href='../helyi.html'> helyi.html</a>
	<p>
	<hr>
	<form name='input' action='../RegistrationServlet' method='put'>
	<input type='hidden' name='command' value='printMyModels'>
	<input name='printMyModels' type='submit' value='<%=language.getString("print.models")%>'>
	</form>
	<p><hr>
	
	<jsp:include page="modelSelect.jsp">
	  <jsp:param name="action" value="printMyModels"/>
	  <jsp:param name="submitLabel" value='<%=language.getString("print.models")%>'/>
	</jsp:include>

<%
}
%>

<%=language.getString("list.models")%>
<p>

<jsp:include page="listMyModels.jsp"/>

</body></html>
