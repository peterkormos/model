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
	var child = field.getElementsByTagName("void")[0].getElementsByTagName("string")[0].firstChild;
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
	var child = field.getElementsByTagName("void")[0].getElementsByTagName("int")[0].firstChild;
	
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
			if(fields[j].getElementsByTagName("string")[0] == null)
				continue;
 
			var fieldName = fields[j].getElementsByTagName("string")[0].firstChild.nodeValue;	
			
			
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
		list.add(new Option("-", userID),  null);
		list.options[list.options.length-1].innerHTML = lastName + " " + firstName + " (" + yearOfBirth + " - " + city + ")";
	}
}

function  loginUser(userID)
{
	document.getElementById('input').command.value="directLogin";
	var newCat = document.input.command.cloneNode(true);
	newCat.setAttribute("name", "userID");
	newCat.value = userID;
	document.input.appendChild(newCat);
	
	document.input.submit();
}
