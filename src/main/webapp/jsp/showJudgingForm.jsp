<%@page import="java.util.*"%>
<%@ page import="java.io.*"%>

<%@page import="servlet.*"%>
<%@page import="datatype.*"%>

<%@include file="util.jsp"%>

<%
  highlightStart = 0xEAEAEA;

  List<JudgingCriteria> criteriaList = (List<JudgingCriteria>) session
		  .getAttribute(JudgingServlet.SessionAttribute.JudgingRulesForCategory.name());

  String category = (String) session.getAttribute(JudgingServlet.SessionAttribute.Category.name());
%>
<p>
<form
	action="../JudgingServlet/<%=JudgingServlet.RequestType.SaveJudging.name()%>"
	method="post">

	<input type="hidden"
		name="<%=JudgingServlet.RequestParameter.JudgingCriterias.name()%>"
		value="<%=criteriaList.size()%>">


	<table style="border: 1px solid black;">
		<tr>
			<td colspan="3">Category: <input required="required"
				name="<%=JudgingServlet.RequestParameter.Category.name()%>"
				value="<%=category%>" size="4"> Judge: <input
				required="required"
				name="<%=JudgingServlet.RequestParameter.Judge.name()%>">
			</td>
		</tr>

		<tr>
			<td colspan="3">Person ID: <input type="number" min="1"
				required="required"
				name="<%=JudgingServlet.RequestParameter.ModellerID.name()%>"
				size="3"> Model ID: <input type="number" min="1"
				required="required"
				name="<%=JudgingServlet.RequestParameter.ModelID.name()%>" size="3">
			</td>
		</tr>

		<tr height="20px">
		</tr>

		<tr bgcolor="#ddddff">
			<th>Category</th>
			<th>Description</th>
			<th>Score</th>
		</tr>
		<%
		  for (JudgingCriteria criteria : criteriaList)
		  {
		%>
		<tr bgcolor="<%=highlight()%>">
			<td><%=criteria.getId()%></td>
			<td><%=criteria.getDescription()%></td>
			<td>
				<%
				  for (int i = 0; i < criteria.getMaxScore() + 1; i++)
						{
				%> <input type="radio"
				name="<%=JudgingServlet.RequestParameter.Score.name()%><%=criteria.getId()%>"
				value="<%=i%>"><%=i%> <%
   }
 %>
			</td>
		</tr>
		<%
		  }
		%>

		<tr>
			<td colspan="3" valign="top">Comment: <textarea
					name='<%=JudgingServlet.RequestParameter.Comment.name()%>'
					cols='50' rows='3' placeholder="Max. 1000 char.">
				</textarea></td>
		</tr>
		<tr>
			<td colspan="3" align="center"><input type="submit"></td>
		</tr>
	</table>
</form>

<%!Map<String, String> loadFile(File file) throws IOException
  {
	Map<String, String> props = new LinkedHashMap<String, String>();
	BufferedReader reader = new BufferedReader(new FileReader(file));

	String line = null;
	while ((line = reader.readLine()) != null)
	{
	  String[] kv = line.split("=");
	  props.put(kv[0], kv[1]);
	}

	reader.close();

	return props;
  }%>