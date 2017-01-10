<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@page import="datatype.*"%>
<%@page import="servlet.*"%>

<%@page import="java.util.*"%>

<%
  RegistrationServlet servlet = RegistrationServlet.getInstance(config);
  ServletDAO servletDAO = servlet.getServletDAO();

  final String languageCode = "ADMIN";

  final ResourceBundle language = servlet.getLanguage(languageCode);

  final User user = new User(languageCode);
  user.setUserID(servletDAO.getNextID("USERS", "USER_ID"));
  user.setEmail("admin");
  user.setPassword("secret");
  user.setFirstName("Peter");
  user.setLastName("Kormos");

  servletDAO.registerNewUser(user);

  servletDAO.saveCategoryGroup(new CategoryGroup(servletDAO.getNextID("CATEGORY_GROUP", "CATEGORY_group_ID"), "-", "-"));

  response.sendRedirect("../index.html");
%>