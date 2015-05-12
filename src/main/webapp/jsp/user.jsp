<%@page import="datatype.*"%>
<%@page import="servlet.*"%>

<%@page import="java.util.*"%>

<%
	//input parameters	
 	boolean directRegister = Boolean.parseBoolean(request.getParameter("directRegister"));
 	String action = request.getParameter("action");
 		
	RegistrationServlet servlet = RegistrationServlet.getInstance(config);
 		
	String languageCode = null;
	User user = null;
	try
	{
		user = RegistrationServlet.getUser(request);
		languageCode = user.language;
	}
	catch(Exception e)
	{
		languageCode = ServletUtil.getRequestAttribute(request, "language");
	}	

	ResourceBundle language = servlet.getLanguage(languageCode);
%>

<html>
<head>
<script type="text/javascript" src="findUser.js"></script>

</head>

<link rel="stylesheet" href="base.css" media="screen" />

<body>

<link href="css/base.css" rel="stylesheet" type="text/css"/>
<div class="header"></div>
<form name="input" id="input" action="../RegistrationServlet/<%= action %>" method="put">
	<input type="hidden" name="language" value="<%= languageCode %>">
  <p> 
  </p>
  <table width="47%" border="0">

<%
if(!directRegister)
{
%>
    <tr bgcolor='F6F4F0'> 
      <td><%= language.getString("email") %>: </td>
      <td><input name="email" type="text" value="<%= user == null ? "" : user.email %>"> <font color="#FF0000" size="+3">&#8226;</font>  
        </td>
    </tr>
    <tr> 
      <td width="32%"><strong><%= language.getString("password") %>: </strong></td>
      <td width="68%"><input name="password" type="password" value="<%= user == null ? "" : user.password %>"> 
        <font color="#FF0000" size="+3">&#8226;</font>  </td>
    </tr>
    <tr bgcolor='F6F4F0'> 
      <td><strong><%= language.getString("password.again") %>: </strong></td>
      <td><input name="password2" type="password" value="<%= user == null ? "" : user.password %>"> <font color="#FF0000" size="+3">&#8226;</font>  
        </td>
    </tr>
<%
}
%>
    <tr> 
      <td> <%= language.getString("last.name") %>: </td>
      <td>
      <div id='lastnames'>
      <input name="lastname" type="text" value="<%= user == null ? "" : user.lastName %>" id="lastnameID"  onChange="sendRequest();"> 
      	<font color="#FF0000" size="+3">&#8226;</font>  
<%
if(directRegister)
{
%>
          - 
          <select id="selectID" name="lastname_select" onChange="loginUser(options[selectedIndex].value);">
          </select>
<%
}
%>
          </div>
          </td>    
      </tr>
    <tr bgcolor='F6F4F0'> 
      <td><%= language.getString("first.name") %>: </td>
      <td><input name="firstname" type="text" value="<%= user == null ? "" : user.firstName %>"> <font color="#FF0000" size="+3">&#8226;</font>  
        </td>
    </tr>
    <tr> 
      <td><%= language.getString("year.of.birth") %>: </td>
      <td>
      <%
      String yearOfBirth = "";
      
      if(user == null)
      {
      	if(directRegister)
      		yearOfBirth = "2014";      	
      }
      else
      	yearOfBirth = String.valueOf(user.yearOfBirth);
      %>
<jsp:include page="year.jsp">
  <jsp:param name="selectLabel" value="<%= yearOfBirth %>"/>
  <jsp:param name="selectValue" value="<%= yearOfBirth %>"/>
</jsp:include>

 <font color="#FF0000" size="+3">&#8226;</font>  
 </td>
    </tr>
    <tr bgcolor='F6F4F0'> 
      <td><%= language.getString("language") %>: </td>
      <td> 
      <%
      languageCode = "";
      
      if(user == null)
      {
      	if(directRegister)
      		languageCode = "HU";      	
      }
      else
      	languageCode = user.language;
      %>

 <jsp:include page="language.jsp">
  <jsp:param name="selectLabel" value="<%= languageCode %>"/>
  <jsp:param name="selectValue" value="<%= languageCode %>"/>
</jsp:include>
 <font color="#FF0000" size="+3">&#8226;</font>  </td>
    </tr>
    <tr> 
      <td><%= language.getString("country") %></td>
      <td> 
      <%
      String country = "";
      
      if(user == null)
      {
      	if(directRegister)
      		country = "HU";      	
      }
      else
      	country = user.country;
      %>

<jsp:include page="countries.jsp">
  <jsp:param name="defaultSelectedLabel" value="<%= country %>"/>
  <jsp:param name="defaultSelectedValue" value="<%= country %>"/>
  <jsp:param name="selectName" value="country"/>
</jsp:include>
<font color="#FF0000" size="+3">&#8226;</font>
	</td>
    </tr>
    <tr bgcolor='F6F4F0'> 
      <td><%= language.getString("city") %>: </td>
      <td><input name="city" type="text" value="<%= user == null ? "" : user.city %>"></td>
    </tr>
    <tr> 
      <td><%= language.getString("address") %>: </td>
      <td><input name="address" type="text" value="<%= user == null ? "" : user.address %>"></td>
    </tr>
    <tr bgcolor='F6F4F0'> 
      <td><%= language.getString("telephone") %>:</td>
      <td> <input name="telephone" type="text" value="<%= user == null ? "" : user.telephone %>"></td>
    </tr>
    <tr> 
      <td>&nbsp;</td>
      <td><input type="submit" value="<%= language.getString("save") %>"></td>
    </tr>
  </table>
  <p><font color="#FF0000" size="+3">&#8226;</font> <%= language.getString("mandatory.fields") %></p>
</form>
</body></html>
