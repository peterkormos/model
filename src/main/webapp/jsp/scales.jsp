  <div id='scales'>
    <input type='text' name='modelscale' size='5' value='<%= request.getParameter("selectValue") %>'>  
<font color="#FF0000" size="+3">&#8226;</font> 
 -> <%= request.getParameter("frequentlyUsed") %> 
<select id="sID" name="modelscale_select" onChange="parentNode.getElementsByTagName('input')[0].value=options[selectedIndex].text">
      <option></option>
      <option>1:1200</option>
      <option>1:720</option>
      <option>1:700</option>
      <option>1:570</option>
      <option>1:350</option>
      <option>1:225</option>
      <option>1:200</option>
      <option>1:144</option>
      <option>1:87</option>
      <option>1:77</option>
      <option>1:76</option>
      <option>1:72</option>
      <option>1:50</option>
      <option>1:48</option>
      <option>1:35</option>
      <option>1:32</option>
      <option>1:25</option>
      <option>1:24</option>
      <option>1:12</option>
      <option>1:10</option>
      <option>1:9</option>
      <option>1:8</option>
      <option>1:6</option>
      <option>54 mm</option>
      <option>70 mm</option>
      <option>90 mm</option>
    </select>
  </div>  