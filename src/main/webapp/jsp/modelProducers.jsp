  <div id='producers'>
    <input type='text' name='modelproducer' size='10' value='<%= request.getParameter("selectValue") %>''>  
<font color="#FF0000" size="+3">&#8226;</font> 
 -> <%= request.getParameter("frequentlyUsed") %>
<select name="modelproducer_select" onChange="parentNode.getElementsByTagName('input')[0].value=options[selectedIndex].text">
    <option selected></option>
    <option>Academy</option>
    <option>ACE</option>
    <option>Andrea</option>
    <option>Airfix</option>
    <option>AFV Club</option>
    <option>AMT</option>
    <option>Aoshima</option>
    <option>Dragon</option>
    <option>Eduard</option>
    <option>Fine Molds</option>
    <option>Fujimi</option>
    <option>Hasegawa</option>
    <option>Heller</option>
    <option>Hobby Boss</option>
    <option>Italeri</option>
    <option>ICM</option>
    <option>Miniart</option>
    <option>Monogram</option>
    <option>Pegaso</option>
    <option>Roden</option>
    <option>Revell</option>
    <option>Scratch</option>
    <option>Special Hobby</option>
    <option>Tamiya</option>
    <option>Tristar</option>
    <option>Trumpeter</option>
    <option>Verlinden</option>
    <option>UM</option>
    <option>Zvezda</option>
  </select>
</div>