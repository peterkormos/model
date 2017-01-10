<%@page import="java.util.*"%>

<%@page import="datatype.*"%>
<%@page import="servlet.*"%>

<%
  RegistrationServlet servlet = RegistrationServlet.getInstance(config);
  User user = servlet.getUser(request);

  if (user == null || !"ADMIN".equals(user.language))
  {
		response.sendRedirect("../index.html");
		return;
  }
%>

<html>
<head>
<script type="text/javascript" src="findUser.js"></script>
</head>

<body>
<%
String show = (String)session.getAttribute("show");
if (show == null)
{
  show = "-";
}
%>
Verseny: <FONT COLOR='#ff0000'><b><%=show%></b></FONT>
<p>
<form name="input" id="input" action="../RegistrationServlet"
	method="post">
	<p></p>
	<input type="hidden" id="command" name="command" value=""> <input
		type="hidden" name="language" value="HU"> <a href="#"
		onClick="document.getElementById('command').name='action';document.getElementById('command').value='modifyUser';document.getElementById('input').action='user.jsp';this.parentNode.submit();">Felhaszn&aacute;l&oacute;i
		adatok lek&eacute;rdez&eacute;se/m&oacute;dos&iacute;t&aacute;sa</a> - <a
		href="#"
		onClick="document.getElementById('command').value='logout';this.parentNode.submit();">Kijelentkez&eacute;s</a>
	<hr>
	<p></p>
	<strong><font size="+2"><u>1 makettez&otilde;...</u> </font></strong> <a
		href="#"
		onClick="document.getElementById('command').value='inputForLoginUser';this.parentNode.submit();">nev&eacute;ben
		bel&eacute;p&eacute;s</a> - <a href="#"
		onClick="document.getElementById('command').value='inputForPrint';this.parentNode.submit();">
		nevez&eacute;si lapjainak nyomtat&aacute;sa </a>
	<p>
		<em><strong>Lek&eacute;rdez&eacute;sek...</strong></em>
	</p>
	<input type="hidden" name="withDetailing" value="false"> <a
		href="#"
		onClick="document.getElementById('input').action='listAllModels.jsp';this.parentNode.submit();">&Ouml;sszes
		benevezett makett list&aacute;z&aacute;sa</a> - <a href="#"
		onClick="document.getElementById('command').value='statistics';this.parentNode.submit();"><strong>Statisztika
			k&eacute;sz&iacute;t&eacute;se</strong></a> - <a href="customQuery.jsp">Egyedi
		lek&eacute;rdez&eacute;sek</a>
</form>

<form name="input"  id="input1" action="../RegistrationServlet" method="post">
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

<form name="input"  id="input1a" action="../RegistrationServlet" method="post">
	<input
		type="hidden" name="onlyPhotos" value="true"> 
		<a href="#"
		onClick="document.getElementById('input1a').action='listAllModels.jsp';this.parentNode.submit();">&Ouml;sszes
		benevezett makett list&aacute;z&aacute;sa (csak k&eacute;pekkel)</a> 
</form>

<form name="input" action="../RegistrationServlet" method="post">
	<p>
		<em><strong>Nyomtat&aacute;s...</strong></em>
	</p>
	<input type="hidden" id="command2" name="command" value=""> <input
		type="hidden" name="printPreRegisteredModels" value="true"> <a
		href="#"
		onClick="document.getElementById('command2').value='printAllModels';this.parentNode.submit();">Nevez&eacute;si
		lapok nyomtat&aacute;sa (El&ouml;nevezettek)</a>
</form>

<form name="input" id="input" action="../RegistrationServlet" method="post">
	<p></p>
	<input type="hidden" id="command3" name="command" value=""> <input
		type="hidden" name="printPreRegisteredModels" value="false">
	<!--
    <a href="#" onClick="document.getElementById('command3').value='printAllModels';this.parentNode.submit();">Nevez&eacute;si 
    lapok nyomtat&aacute;sa (Helysz&iacute;nen nevezettek)</a> - 
-->

	<a href="#"
		onClick="document.getElementById('command3').value='printCardsForAllModels';this.parentNode.submit();">A
		makettek mell&eacute; kisk&aacute;rty&aacute;k nyomtat&aacute;sa</a>

	<p>
		<em><strong>Kateg&oacute;ri&aacute;k...</strong></em>
	</p>
	<p></p>
	<a href="../static/HU_addCategoryGroup.html">&Uacute;j
		kateg&oacute;riacsoport hozz&aacute;ad&aacute;sa</a> (Pl. Feln&otilde;tt,
	mester, gyerek, ifi,...) - <a href="#"
		onClick="document.getElementById('command3').value='inputForDeleteCategoryGroup';this.parentNode.submit()">Kateg&oacute;riacsoportok
		lek&eacute;rdez&eacute;se, t&ouml;rl&eacute;se</a>
	<p></p>
	<a href="#"
		onClick="document.getElementById('command3').value='inputForAddCategory';this.parentNode.submit();">&Uacute;j
		kateg&oacute;ria hozz&aacute;ad&aacute;sa</a> - <a href="#"
		onClick="document.getElementById('command3').value='inputForDeleteCategory';this.parentNode.submit();">Kateg&oacute;ria
		t&ouml;rl&eacute;se</a>

	<p></p>
	Mester Makettez&ocirc; csal&aacute;dneve:  
	<input name="lastname" type="text" value="<%= user == null ? "" : user.lastName %>" id="lastnameID"  onChange="sendRequest();">
    - 
    <select id="selectID" name="userID">
    </select>
	
	Szakoszt&aacute;ly: <select name="modelClass">
		<%
		  for (ModelClass mc : ModelClass.values())
		  {
		%>
		<option value='<%=mc.name()%>'><%=mc.name()%></option>
		<%
		  }
		%>
	</select> <a href="#"
		onClick="document.getElementById('command3').value='saveModelClass';this.parentNode.submit();">Szakoszt&aacute;ly
		ment&eacute;se</a>

	<p></p>
</form>
<p>
	<em><strong>Adatkezel&eacute;s..</strong>.</em>
</p>
<form name="input" action="../RegistrationServlet" method="post">
	<input type="hidden" name="command" value="exportCategoryData">
	<input name="exportData" type="submit"
		value="Csak a kateg&oacute;ri&aacute;k export&aacute;l&aacute;sa">
</form>
<form name="input" action="../RegistrationServlet" method="post">
	<input type="hidden" name="command" value="exportData"> <input
		type="hidden" name="photos" value="yes"> <input
		name="exportData" type="submit"
		value="&Ouml;sszes adat export&aacute;l&aacute;sa">
</form>
<form name="input" action="../RegistrationServlet" method="post">
	<input type="hidden" name="command" value="exportData"> <input
		type="hidden" name="photos" value="no"> <input
		name="exportData" type="submit"
		value="&Ouml;sszes adat export&aacute;l&aacute;sa k&eacute;pek n&eacute;lk&uuml;l">
</form>
<form action="../RegistrationServlet" method="post"
	enctype="multipart/form-data" name="input">
	<input type="hidden" name="command" value="importData"> <input
		type="file" name="zipFile"> <input type="submit"
		value="Adatok import&aacute;l&aacute;sa">
</form>
<p>
	<em><strong>El&otilde;nevez&eacute;s...</strong></em>
</p>

<form name="input" action="../RegistrationServlet" method="post">
	<input type="hidden" id="command4" name="command" value=""> <input
		type="hidden" id="paramName" name="paramName" value=""> <input
		type="hidden" id="paramValue" name="paramValue" value=""> <a
		href="#"
		onClick="document.getElementById('paramName').value='ONSITEUSE';document.getElementById('paramValue').value='0';document.getElementById('command4').value='setSystemParameter';this.parentNode.submit();">Internetes
		m&oacute;d</a> - <a href="#"
		onClick="document.getElementById('paramName').value='REGISTRATION';document.getElementById('paramValue').value='1';document.getElementById('command4').value='setSystemParameter';this.parentNode.submit();">El&ouml;nevez&eacute;s
		kez&eacute;se</a> - <a href="#"
		onClick="document.getElementById('paramName').value='REGISTRATION';document.getElementById('paramValue').value='0';document.getElementById('command4').value='setSystemParameter';this.parentNode.submit();">El&ouml;nevez&eacute;s
		v&eacute;ge</a> - <a href="#"
		onClick="document.getElementById('paramName').value='ONSITEUSE';document.getElementById('paramValue').value='1';document.getElementById('command4').value='setSystemParameter';this.parentNode.submit();">Helysz&iacute;ni
		m&oacute;d</a>
</form>

Rendszer&uuml;zenet: <FONT COLOR='#ff0000'><b><%=servlet.getSystemMessage()%></b></FONT>
<p>
<form name="input" action="../RegistrationServlet" method="post">
	<input type="hidden" name="command" value="setSystemParameter">
	<input type="hidden" name="paramName" value="SYSTEMMESSAGE">
	<textarea name="paramValue" cols="100" rows="5"></textarea>
	<input name="setSystemParameter" type="submit"
		value="Rendszer&uuml;zenet">
</form>

<form name="input" action="../RegistrationServlet" method="post">
	<input type="hidden" name="command" id="command5"
		value="deletedirectUsers">
	<p>
		<em><strong>Verseny eredm&eacute;nyek... </strong></em>
	</p>

	<a href="#"
		onClick="document.getElementById('command5').value='getawardedModelsPage';this.parentNode.submit();">D&iacute;jazott
		makettek megad&aacute;sa, nyomtat&aacute;s, prezent&aacute;ci&oacute;
		k&eacute;sz&iacute;t&eacute;s</a>
	<p></p>

	<a href="#"
		onClick="document.getElementById('command5').value='inputForModifyModel';this.parentNode.submit();">Makett
		&aacute;tsorol&aacute;sa. Sorsz&aacute;m: </a> <input type="text"
		name="modelID">

	<p>
		<a href="awardedModels.jsp">Eredm&eacute;nyek
			lek&eacute;rdez&eacute;se </a>- <a href="deleteAwardedModel.jsp">D&iacute;jazott
			makett t&ouml;rl&eacute;se</a>
	</p>

</form>

<p>
	<em><strong>Hibakezel&eacute;s..</strong>.</em>
</p>
<form name="input" action="../RegistrationServlet" method="post">
	<input type="hidden" name="command" value="exceptionHistory"> 
	<input name="exceptionHistory" type="submit" value="Exception history">
</form>
<p>
<p></p>
<hr>
<p>&nbsp;</p>
<p>
	<em><strong><font color="#FF0000">VIGY&Aacute;ZAT!!!!</font></strong></em>
</p>
<p>&nbsp;</p>
<form name="input" action="../RegistrationServlet" method="post">
	<input type="hidden" name="command" value="inputForDeleteUsers">
	<input name="inputForDeleteUsers" type="submit"
		value="1  felhaszn&aacute;l&oacute; t&ouml;rl&eacute;se az adatb&aacute;zisb&oacute;l">
</form>
<p>
<form name="input" action="../RegistrationServlet" method="post">
	<input type="hidden" name="command" value="deleteDataForShow">
	<input name="deleteData" type="submit"
		value="Aktu&aacute;lis verseny &ouml;sszes makettj&eacute;nek, kateg&oacute;ri&aacute;j&aacute;nak t&ouml;rl&eacute;se az adatb&aacute;zisb&oacute;l"
		style="color: rgb(255, 0, 0); font-weight: bold">
</form>
<p>
<form name="input" action="../RegistrationServlet" method="post">
	<input type="hidden" name="command" value="deletedirectUsers">
	<input name="deletedirectUsers" type="submit"
		value="&Ouml;sszes helysz&iacute;nen regisztr&aacute;lt felhaszn&aacute;l&oacute; t&ouml;rl&eacute;se"
		style="color: rgb(255, 0, 0); font-weight: bold">
</form>
<p>------------------------------------------------------</p>
<form name="input" action="../RegistrationServlet" method="post">
	<input type="hidden" name="command" value="newUserIDs"> <input
		name="newUserIDs" type="submit"
		value="&Uacute;j regisztr&aacute;ci&oacute;s sz&aacute;mok kioszt&aacute;sa">
</form>
<p>
<form name="input" action="../RegistrationServlet" method="post">
	<input type="hidden" name="command" value="newUserIDsFromOne">
	<input name="newUserIDsFromOne" type="submit"
		value="&Uacute;j regisztr&aacute;ci&oacute;s sz&aacute;mok kioszt&aacute;sa 1-tol">
</form>
</p>
<form name="input" action="../RegistrationServlet" method="post">
	<p>SQL update:</p>
	<p>
		<input type="hidden" name="command" value="sql"> <input
			name="sql" type="text" value="" size="150">
	</p>
	<p>update MAK_SYSTEM set PARAM_VALUE='1'</p>
	<p>update MAK_USERS set USER_password='secret' where email='admin'</p>
	<p>update MAK_USERS set USER_LANGUAGE='CATEGORY' where
		USER_NAME='category'</p>
	<p>
	<p>alter table MAK_USERS modify EMAIL varchar(100)
	<p>update MAK_USERS set EMAIL='admin' where USER_NAME='admin'
	<p>alter table MAK_USERS drop column USER_NAME
</form>
</body>
</html>
