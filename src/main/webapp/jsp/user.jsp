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
		languageCode = servlet.getRequestAttribute(request, "language");
	}	

	ResourceBundle language = servlet.getLanguage(languageCode);
%>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script type="text/javascript">

function sendRequest()
{
	var url = "../RegistrationServlet?command=getSimilarLastNames&lastname=" + document.getElementById('lastnameID').value;
	var req = false;

	if (window.XMLHttpRequest) 
	{ // Mozilla, Safari,...
   		req = new XMLHttpRequest();

	if (req.overrideMimeType)
    	req.overrideMimeType('text/xml');
  	} 
	else if (window.ActiveXObject) 
	{ // IE
   		try 
		{
		    req = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) 
		{
      		try 
			{
       			req = new ActiveXObject("Microsoft.XMLHTTP");
      		} catch (e) 
			{}
     	}
   }
   
	req.open("GET", url, true);
	req.onreadystatechange= function()
	{   
		if (req.readyState == 4)
			parseXML(req.responseText);
	}

	req.send(null);
}

function getStringValue(field)
{
	var child = field.getElementsByTagName("string")[0].firstChild;
	if(child != null)
	{
		var str = child.nodeValue;
		
		while(str.search("&amp;") > -1)
			str.replace("&amp;", "&");
			
		return  str;
	}
	else
		return "";
}

function getIntValue(field)
{
	var child = field.getElementsByTagName("int")[0].firstChild;
	
	if(child != null)
		return  child.nodeValue;
	else
		return "";
}

function parseXML(xmlstring)
{
	var xmlobject = (new DOMParser()).parseFromString(xmlstring, "text/xml");
	var root = xmlobject.getElementsByTagName('object')[0];
	var users = root.getElementsByTagName("void");
	for (var i = 0 ; i < users.length ; i++) 
	{
		if(users[i].getAttribute("method") != "add")
			continue;
			
		var fields = users[i].getElementsByTagName("object")[0].getElementsByTagName("void");

		var userID = "";
		var lastName = "";
		var firstName = "";
		var yearOfBirth = "";
		var language = "";
		var country = "";
		var city = "";
		var email = "";

		for (var j = 0 ; j < fields.length ; j++) 
		{	
			var fieldName = fields[j].getAttribute("property");	
			
			
			if(fieldName == 'userID')
				userID = getIntValue(fields[j]);

			if(fieldName == 'lastName')
				lastName = getStringValue(fields[j])
					
			if(fieldName == 'email')
				email = getStringValue(fields[j]);

			if(fieldName == 'firstName')
				firstName = getStringValue(fields[j])
			
			if(fieldName == 'yearOfBirth')
				yearOfBirth = getIntValue(fields[j]);
			
			if(fieldName == 'language')
				language = getStringValue(fields[j]);
			
			if(fieldName == 'country')
				country = getStringValue(fields[j])
			
			if(fieldName == 'city')
				city = getStringValue(fields[j])
		}
		
		var list = document.getElementById('selectID');
		if(list.options.length == 0)
			list.add(new Option("-", userID),  null);
		list.add(new Option("-", userID),  null);
		list.options[list.options.length-1].innerHTML = lastName + " " + firstName + " (" + yearOfBirth + " - " + city + ")";
	}
}

function  loginUser(userID)
{
	document.input.command.value="directLogin";
	var newCat = document.input.command.cloneNode(true);
	newCat.setAttribute("name", "userID");
	newCat.value = userID;
	document.input.appendChild(newCat);
	
	document.input.submit();
}

</script>
</head>

<body><html>

<link href="css/base.css" rel="stylesheet" type="text/css"/>
<div class="header"></div>
<p>
<form accept-charset="UTF-8" name="input" action="../RegistrationServlet" method="put">
	<input type="hidden" name="command" value="<%= action %>">
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
      <td><p><%= language.getString("language") %>: </p></td>
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
