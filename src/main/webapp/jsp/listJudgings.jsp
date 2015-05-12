<%@page import="java.util.*"%>

<%@page import="datatype.*"%>
<%@page import="servlet.*"%>

<%@include file="util.jsp"%>

<%
  highlightStart = 0xEAEAEA;
%>

<table style="border: 1px solid black;">

	<tr bgcolor="#ddddff">
		<th>Kateg&oacute;ria</th>
		<th>Zs&utilde;ri</th>
		<th>SZEM&Eacute;LY SORSZ&Aacute;MA</th>
		<th>MAKETT SORSZ&Aacute;MA</th>
		<th>Krit&eacute;ria</th>
		<th>Pont</th>
		<th>Megjegyz&eacute;s</th>
	</tr>


	<%
	List<Record> judgings = (List<Record>)session.getAttribute(JudgingServlet.SessionAttribute.Judgings.name());
	for(Record record : judgings)
	{
	  JudgingScore judgingScore = (JudgingScore)record; 
%>
	<tr bgcolor="<%=highlight()%>">
		<td><%= judgingScore.getCategory()%></td>
		<td><%= judgingScore.getJudge()%></td>
		<td><%= judgingScore.getModellerID()%></td>
		<td><%= judgingScore.getModelID()%></td>
		<td><%= judgingScore.getCriteriaID()%></td>
		<td><%= judgingScore.getScore()%></td>
		<td><%= judgingScore.getComment()%></td>
	</tr>

	<%	  
	}
%>

</table>