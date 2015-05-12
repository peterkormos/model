<%@page import="java.util.*"%>

<%@page import="servlet.JudgingServlet"%>

<form
	action="../JudgingServlet/<%=JudgingServlet.RequestType.GetJudgingForuma.name()%>"
	method="post">
	<%
	  Map<String, String> judging = (Map<String, String>) session.getAttribute(JudgingServlet.RequestType.GetJudgingRules.name());

	  for (Map.Entry<String, String> entry : judging.entrySet())
	  {
	%>
	<%=entry.getKey()%>
	<input type="radio" name="<%=JudgingServlet.RequestParameter.Category.name()%>" value="<%=entry.getKey()%>" onclick="submit()">
	<br>
		<%
		  }
		%>
	
</form>