<%@page import="servlet.*"%>

<a href="../JudgingServlet/<%=JudgingServlet.RequestType.GetJudgingRules.name()%>">Judge</a>
<p>
<a href="../JudgingServlet/<%=JudgingServlet.RequestType.ListJudgings.name()%>">List judgings</a>
<p>
<a href="../JudgingServlet/<%=JudgingServlet.RequestType.ListJudgingSummary.name()%>">List judging summary</a>
<p>
<hr>
<p>
<a href="../JudgingServlet/<%=JudgingServlet.RequestType.DeleteJudgings.name()%>">Delete judgings</a>
