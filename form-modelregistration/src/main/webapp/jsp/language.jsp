<select name="language" >
  <option value="<%= request.getParameter("selectValue") %>" selected><%= request.getParameter("selectLabel") %></option>
  <option value="" >-------</option>
  <option value="HU">Magyar</option>
  <option value="EN">English</option>
  <option value="SK">Slovensk&aacute;</option>
  <option value="CZ">Cesk&aacute;</option>
  <option value="DE">Deutsch</option>
  <!--
          <option value="AT">Austria</option>
          <option value="PL">Poland</option>
		  //-->
  <option value="" >-------</option>
</select>
