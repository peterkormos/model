<%@ page import="org.msz.servlet.*"%>

<%@page import="org.msz.servlet.util.PollsServletDAO"%>
<%@page import="org.msz.datatype.Record"%>
<%@page import="org.msz.servlet.datatype.*"%>
<%@page import="java.util.*"%>
<%@page import="org.msz.util.WebUtils"%>

<html>
<body>

	<table width="100%" border="1" cellspacing="0" cellpadding="0">

		<jsp:include page="pollHeader.jsp" />

		<jsp:include page="statisticsDetails.jsp" />

		<jsp:include page="voteList.jsp" />

	</table>

</body>
</html>

