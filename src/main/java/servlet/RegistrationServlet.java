package servlet;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import datatype.AwardedModel;
import datatype.Category;
import datatype.CategoryGroup;
import datatype.Detailing;
import datatype.Model;
import datatype.User;

public class RegistrationServlet extends HttpServlet
{
  public String VERSION = "2014.04.08.";
  public static Logger logger = Logger.getLogger(RegistrationServlet.class);

  private String smtpServer;
  private String emailFrom;
  private boolean debugSMTP;

  Map<String, ResourceBundle> languages; // key: HU, EN, ...

  public static ServletDAO servletDAO;
  StringBuffer printBuffer;
  StringBuffer batchAddModelBuffer;
  StringBuffer awardedModelsBuffer;
  StringBuffer cerificateOfMeritBuffer;
  StringBuffer presentationBuffer;
  StringBuffer printCardBuffer;

  public static boolean preRegistrationAllowed;
  private boolean onSiteUse;
  private String systemMessage = "";

  private List<ExceptionData> exceptionHistory;

  private static RegistrationServlet instance;

  private static final String DIRECT_USER = "_LOCAL_";

  Properties servletConfig = new Properties();

  public RegistrationServlet() throws Exception
  {
	instance = this;

	try
	{
	  String baseDir = getClass().getClassLoader().getResource("/").getFile() + "../conf/";
	  DOMConfigurator.configure(baseDir + "log4j.xml");

	  servletConfig.load(new FileInputStream(baseDir + "servlet.ini"));

	  logger.fatal("************************ Logging restarted ************************");
	  System.out.println("VERSION: " + VERSION);
	  logger.fatal("VERSION: " + VERSION);

	  DriverManager.registerDriver((Driver) Class.forName(getServerConfigParamter("DB_Driver")).newInstance());

	  if (servletDAO == null)
	  {
		final Connection dbConnection = DriverManager.getConnection(getServerConfigParamter("DB_URL"),
		    getServerConfigParamter("DB_Username"), getServerConfigParamter("DB_Password"));
		dbConnection.setAutoCommit(true);
		servletDAO = new ServletDAO(this, dbConnection);
	  }

	  this.smtpServer = getServerConfigParamter("smtpServer");
	  this.debugSMTP = Boolean.parseBoolean(getServerConfigParamter("debugSMTP"));
	  this.emailFrom = getServerConfigParamter("emailFrom");

	  languages = new HashMap<String, ResourceBundle>();

	  printBuffer = loadFile(baseDir + "print.html");
	  printCardBuffer = loadFile(baseDir + "printCard.html");
	  batchAddModelBuffer = loadFile(baseDir + "batchAddModel.html");
	  awardedModelsBuffer = loadFile(baseDir + "awardedModels.html");
	  cerificateOfMeritBuffer = loadFile(baseDir + "cerificateOfMerit.html");
	  presentationBuffer = loadFile(baseDir + "presentation.html");

	  exceptionHistory = new LinkedList<ExceptionData>();

	  System.out.println("OK.....");
	}
	catch (final Exception e)
	{
	  if (logger != null)
	  {
		logger.fatal("!!! init(): ", e);
	  }
	  else
	  {
		e.printStackTrace();
	  }
	  throw e;
	}
  }

  public static RegistrationServlet getInstance() throws Exception
  {
	if (instance == null)
	{
	  instance = new RegistrationServlet();
	}

	return instance;
  }

  @Override
  public void init(final ServletConfig config) throws UnavailableException, ServletException
  {
  }

  private StringBuffer loadFile(final String file) throws FileNotFoundException, IOException
  {
	final BufferedReader br = new BufferedReader(new FileReader(file));
	final StringBuffer buffer = new StringBuffer();

	String line = null;
	while ((line = br.readLine()) != null)
	{
	  buffer.append("\r\n");
	  buffer.append(line);
	}

	br.close();

	return buffer;
  }

  private String getServerConfigParamter(final String parameter) throws Exception
  {
	final String value = servletConfig.getProperty(parameter);

	if (value == null)
	{
	  throw new Exception("parameter: " + parameter + " not found in web.xml");
	}

	if (logger != null)
	{
	  logger.debug("getServerConfigParamter(): parameter " + parameter + " value: " + value);
	}

	return value;
  }

  protected void checkHTTPRequest(final HttpServletRequest req)
  {
	try
	{
	  logger.fatal("!!! checkHTTPRequest(): queryString: " + req.getQueryString());
	  final Enumeration<String> e = req.getParameterNames();
	  while (e.hasMoreElements())
	  {
		final String param = e.nextElement();
		logger.fatal("!!! checkHTTPRequest(): parameter: " + param + " value: " + req.getParameter(param));
	  }

	  final BufferedReader in = new BufferedReader(new InputStreamReader(req.getInputStream()));
	  String inputLine;
	  if (in.ready())
	  {
		while ((inputLine = in.readLine()) != null)
		{
		  logger.fatal("!!! checkHTTPRequest(): inputLine: " + inputLine);
		}
	  }
	}
	catch (final Exception ex)
	{
	  ex.printStackTrace();
	}
  }

  public static String getOptionalRequestAttribute(final HttpServletRequest req, final String name)
  {
	try
	{
	  final String value = getRequestAttribute(req, name, false);

	  return "".equals(value) ? "-" : value;
	}
	catch (final Exception e)
	{
	  e.printStackTrace();
	  return "-";
	}
  }

  public String getRequestAttribute(final HttpServletRequest req, final String name) throws Exception
  {
	return getRequestAttribute(req, name, true);
  }

  public static String getRequestAttribute(final HttpServletRequest req, final String name, final boolean throwException)
	  throws Exception
  {
	String value;
	value = req.getParameter(name);
	if (value == null || value.trim().length() == 0)
	{
	  if (throwException)
	  {
		throw new Exception(name + " is not set!");
	  }
	  else
	  {
		return "-";
	  }
	}

	if (logger.isDebugEnabled())
	{
	  logger.debug("HTTP parameter: " + name + " value: " + value);
	}

	return value.trim();
  }

  @Override
  public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
  {
	doPost(request, response);
  }

  @Override
  public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
  {
	try
	{
	  final long startTime = System.currentTimeMillis();

	  if (request.getCharacterEncoding() == null)
	  {
		request.setCharacterEncoding("UTF-8");
	  }

	  if (ServletFileUpload.isMultipartContent(request))
	  {
		process_importDataRequest(request, response);
		return;
	  }

	  final String command = getRequestAttribute(request, "command");

	  getClass().getMethod("process_" + command + "Request", new Class[] { HttpServletRequest.class, HttpServletResponse.class })
		  .invoke(this, new Object[] { request, response });

	  String email = null;
	  try
	  {
		email = getUser(request).email;
	  }
	  catch (final Exception e)
	  {
	  }

	  if (logger.isTraceEnabled())
	  {
		logger.trace("doPost() request arrived from " + request.getRemoteAddr() + " email: " + email + " command: " + command
		    + " processTime: " + (System.currentTimeMillis() - startTime));
	  }

	}
	catch (Exception e)
	{
	  logger.fatal("!!!doPost(): ", e);

	  if (e instanceof InvocationTargetException)
	  {
		e = (Exception) ((InvocationTargetException) e).getCause();
	  }

	  final String message = e.getMessage();

	  addExceptionToHistory(System.currentTimeMillis(), e, request);

	  writeErrorResponse(response, "Server error: <b>" + message + "</b>");
	}
  }

  private void writeErrorResponse(final HttpServletResponse response, final String message) throws IOException
  {
	final StringBuffer buff = new StringBuffer();

	buff.append("<html><body>");

	buff.append(message);
	buff.append("</body></html>");

	writeResponse(response, buff);
  }

  public void writeResponse(final HttpServletResponse response, final StringBuffer message) throws IOException
  {
	response.setContentType("text/html");
	response.getOutputStream().write(message.toString().getBytes());
  }

  public void process_directLoginRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	final Enumeration parameters = request.getParameterNames();
	while (parameters.hasMoreElements())
	{
	  final String param = (String) parameters.nextElement();

	  if (param.startsWith("userID"))
	  {
		final int userID = Integer.parseInt(getRequestAttribute(request, param));

		loginSuccessful(request, response, servletDAO.getUser(userID));
		return;
	  }
	}
  }

  public void process_directPrintModelsRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final Enumeration parameters = request.getParameterNames();
	while (parameters.hasMoreElements())
	{
	  final String param = (String) parameters.nextElement();

	  if (param.startsWith("userID"))
	  {
		final int userID = Integer.parseInt(getRequestAttribute(request, param));

		printModels(request, response, getLanguage(getRequestAttribute(request, "language")), userID);
		showPrintDialog(response);
		return;
	  }
	}
  }

  public void process_getModelInfoRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	final String modelID = getRequestAttribute(request, "modelID");

	final Model model = servletDAO.getModel(Integer.parseInt(modelID));

	final StringBuffer buff = new StringBuffer();

	buff.append(servletDAO.getCategory(model.categoryID).categoryCode);
	buff.append(" - ");
	buff.append(model.scale);
	buff.append(" - ");
	buff.append(model.name);

	writeResponse(response, buff);
  }

  public void process_getSimilarLastNamesRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final String lastname = getRequestAttribute(request, "lastname");

	final StringBuffer buff = new StringBuffer();

	final ByteArrayOutputStream bout = new ByteArrayOutputStream();
	final XMLEncoder e = new XMLEncoder(bout);
	e.writeObject(servletDAO.getSimilarLastNames(lastname));
	e.close();

	buff.append(bout.toString());

	writeResponse(response, buff);
  }

  public void process_loginRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	updateSystemSettings();

	final String email = getRequestAttribute(request, "email");
	final ResourceBundle language = getLanguage(getRequestAttribute(request, "language"));

	try
	{
	  final User user = servletDAO.getUser(email);

	  if (user.password.equals(servletDAO.encodeString(getRequestAttribute(request, "password"))) && user.enabled)
	  {
		loginSuccessful(request, response, user);
	  }
	  else
	  {
		logger.info("process_loginRequest(): Authentication failed. email: " + email + " user.password: [" + user.password
		    + "] HTTP password: [" + getRequestAttribute(request, "password") + "] user.enabled: " + user.enabled);

		writeErrorResponse(response, language.getString("authentication.failed") + " " + language.getString("email") + ": ["
		    + email + "]");
	  }
	}
	catch (final Exception ex)
	{
	  logger.info("process_loginRequest(): Authentication failed. email: " + email, ex);

	  writeErrorResponse(response, language.getString("authentication.failed") + " " + language.getString("email") + ": ["
		  + email + "]");
	}

  }

  private void loginSuccessful(final HttpServletRequest request, final HttpServletResponse response, final User user)
	  throws IOException, Exception
  {
	String show;

	try
	{
	  show = servletDAO.encodeString(getRequestAttribute(request, "show"));
	}
	catch (final Exception e)
	{
	  show = null;
	}

	logger.info("process_loginRequest(): login successful. email: " + user.email + " user.language: " + user.language + " show: "
	    + show);

	final HttpSession session = request.getSession(true);
	session.setAttribute("userID", user);
	session.setAttribute("show", show);

	response.sendRedirect("jsp/main.jsp");
  }

  public void process_sqlRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	final StringBuffer buff = servletDAO.execute(getRequestAttribute(request, "sql"));

	if (buff == null)
	{
	  response.sendRedirect("jsp/main.jsp");
	}
	else
	{
	  final User user = getUser(request);
	  final ResourceBundle language = getLanguage(user.language);

	  writeResponse(response, buff);
	}
  }

  public void process_reminderRequest(final HttpServletRequest request,

  final HttpServletResponse response) throws Exception
  {
	final User user = servletDAO.getUser(getRequestAttribute(request, "email"));
	sendEmail(user, true);

	final ResourceBundle language = getLanguage(user.language);

	final StringBuffer buff = new StringBuffer();
	buff.append("<html><body>");

	buff.append(language.getString("email.was.sent"));
	buff.append("<p>");
	buff.append("<a href='index.html'>" + language.getString("proceed.to.login") + "</a></body></html>");

	writeResponse(response, buff);
  }

  public void process_batchAddModelRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	final String languageCode = getRequestAttribute(request, "language");
	final ResourceBundle language = getLanguage(languageCode);

	final int rows = Integer.parseInt(getRequestAttribute(request, "rows"));

	final List<Model> models = new LinkedList<Model>();
	final List<User> users = new LinkedList<User>();
	User user = null;
	for (int i = 1; i <= rows; i++)
	{
	  final String httpParameterPostTag = String.valueOf(i);

	  if (getRequestAttribute(request, "firstname" + httpParameterPostTag).trim().length() == 0)
	  {
		continue;
	  }

	  if (getRequestAttribute(request, "lastname" + httpParameterPostTag).trim().length() == 0)
	  {
		continue;
	  }

	  // register model for new user...
	  if (i == 1
		  || !(getRequestAttribute(request, "firstname" + httpParameterPostTag) + getRequestAttribute(request, "lastname"
		      + httpParameterPostTag))
		      .equals((getRequestAttribute(request, "firstname" + String.valueOf(i - 1)) + getRequestAttribute(request,
		          "lastname" + String.valueOf(i - 1)))))
	  {
		if (user != null && user.email != null && i > 1)
		{
		  sendEmail(user, true);
		}

		user = directRegisterUser(request, language, httpParameterPostTag);
		users.add(user);
	  }

	  final Model model = createModel(servletDAO.getNextID("MODEL", "MODEL_ID"), user.userID, request, httpParameterPostTag);

	  servletDAO.saveModel(model);

	  models.add(model);
	}

	if (user != null && user.email != null)
	{
	  sendEmail(user, true);
	}

	if (!users.isEmpty())
	{
	  for (final User user1 : users)
	  {
		writeResponse(response, printModels(language, user1.userID, request));
	  }
	}
	showPrintDialog(response);
  }

  private void showPrintDialog(final HttpServletResponse response) throws IOException
  {
	response.getOutputStream().write("<script>window.print();</script>".getBytes());
  }

  public void process_directRegisterRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	// set language library
	final String languageCode = getRequestAttribute(request, "language");
	final ResourceBundle language = getLanguage(languageCode);

	final User user = directRegisterUser(request, language, "");

	final HttpSession session = request.getSession(true);
	session.setAttribute("userID", user);

	response.sendRedirect("jsp/main.jsp");
  }

  public void process_exportDataRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {

	final List data = new ArrayList(4);
	data.add(servletDAO.getUsers());
	data.add(servletDAO.getCategoryGroups());
	data.add(servletDAO.getCategoryList(null));
	data.add(servletDAO.getModels(ServletDAO.INVALID_USERID));

	response.setContentType("application/zip");
	final XMLEncoder e = new XMLEncoder(new GZIPOutputStream(response.getOutputStream()));
	e.writeObject(data);
	e.close();
  }

  public void process_exportCategoryDataRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final String show = (String) request.getSession().getAttribute("show");

	final List data = new ArrayList(4);
	data.add(new LinkedList<User>());

	final List<CategoryGroup> categoryGroups = servletDAO.getCategoryGroups();
	final Iterator<CategoryGroup> iterator = categoryGroups.iterator();
	while (iterator.hasNext())
	{
	  final CategoryGroup categoryGroup = iterator.next();
	  if (!categoryGroup.show.equals(show))
	  {
		iterator.remove();
	  }
	}
	data.add(categoryGroups);
	data.add(servletDAO.getCategoryList(show));
	data.add(new LinkedList<Model>());

	response.setContentType("application/zip");
	final XMLEncoder e = new XMLEncoder(new GZIPOutputStream(response.getOutputStream()));
	e.writeObject(data);
	e.close();
  }

  public void process_importDataRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	final FileItemIterator iter = new ServletFileUpload().getItemIterator(request);
	while (iter.hasNext())
	{
	  final FileItemStream item = iter.next();
	  final String name = item.getFieldName();

	  final XMLDecoder e = new XMLDecoder(new GZIPInputStream(item.openStream()));
	  final List<List> data = (List<List>) e.readObject();
	  e.close();

	  final StringBuffer buff = new StringBuffer();

	  buff.append("Storing users");
	  servletDAO.deleteEntries("MAK_USERS");

	  final List<User> users = data.get(0);
	  for (final User user : users)
	  {
		buff.append(".");
		servletDAO.registerNewUser(user, getLanguage(getUser(request).language));
	  }

	  buff.append("<p>Storing CategoryGroups");
	  servletDAO.deleteEntries("MAK_CATEGORY_GROUP");

	  final List<CategoryGroup> categoryGroups = data.get(1);

	  if (!categoryGroups.isEmpty())
	  {
		for (final CategoryGroup categoryGroup : categoryGroups)
		{
		  buff.append(".");
		  servletDAO.saveCategoryGroup(categoryGroup);
		}

		buff.append("<p>Storing Categories");
		servletDAO.deleteEntries("MAK_CATEGORY");

		final List<Category> categories = data.get(2);
		for (final Category category : categories)
		{
		  buff.append(".");
		  servletDAO.saveCategory(category);
		}

		buff.append("<p>Storing Models");
		servletDAO.deleteEntries("MAK_MODEL");

		final List<Model> models = data.get(3);
		for (final Model model : models)
		{
		  buff.append(".");
		  servletDAO.saveModel(model);
		}
	  }

	  buff.append("<p>DONE.....");

	  writeResponse(response, buff);
	}
  }

  public void process_deleteDataForShowRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final String show = (String) request.getSession().getAttribute("show");

	for (final AwardedModel awardedModel : servletDAO.getAwardedModels())
	{
	  if (servletDAO.getCategory(awardedModel.model.categoryID).group.show.equals(show))
	  {
		servletDAO.deleteAwardedModel(awardedModel.model.modelID);
	  }
	}

	for (final Model model : servletDAO.getModels(servletDAO.INVALID_USERID))
	{
	  if (servletDAO.getCategory(model.categoryID).group.show.equals(show))
	  {
		servletDAO.deleteModel(model.modelID);
	  }
	}

	for (final Category category : servletDAO.getCategoryList(show))
	{
	  servletDAO.deleteCategory(category.categoryID);
	}

	for (final CategoryGroup categoryGroup : servletDAO.getCategoryGroups())
	{
	  if (categoryGroup.show.equals(show))
	  {
		servletDAO.deleteCategoryGroup(categoryGroup.categoryGroupID);
	  }
	}

	response.sendRedirect("jsp/main.jsp");
  }

  private User directRegisterUser(final HttpServletRequest request, final ResourceBundle language,
	  final String httpParameterPostTag) throws Exception
  {
	final String password = "-";
	final String userName = getRequestAttribute(request, "lastname" + httpParameterPostTag)
	    + getRequestAttribute(request, "firstname" + httpParameterPostTag) + DIRECT_USER + System.currentTimeMillis();

	servletDAO.registerNewUser(createUser(request, userName, password, httpParameterPostTag), language);

	final User user = servletDAO.getUser(userName);
	return user;
  }

  public void process_registerRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	String email = getRequestAttribute(request, "email");
	final String languageCode = getRequestAttribute(request, "language");
	final ResourceBundle language = getLanguage(languageCode);

	if (email.trim().length() == 0 || email.equals("-") || email.indexOf("@") == -1)
	{
	  writeErrorResponse(response, language.getString("authentication.failed") + " " + language.getString("email") + ": ["
		  + email + "]");
	  return;
	}

	if (servletDAO.userExists(email))
	{
	  RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/userExists.jsp");
	  dispatcher.forward(request, response);
	  return;
	}

	final String password = getRequestAttribute(request, "password");

	if (!password.equals(getRequestAttribute(request, "password2")))
	{
	  writeErrorResponse(response, language.getString("passwords.not.same"));
	}

	User user = createUser(request, email);
	servletDAO.registerNewUser(user, language);

	sendEmail(user, true);

	final StringBuffer buff = new StringBuffer();
	buff.append("<html><body>");

	buff.append(language.getString("email.was.sent"));
	buff.append("<p>");
	buff.append("<a href='index.html'>" + language.getString("proceed.to.login") + "</a></body></html>");

	writeResponse(response, buff);

  }

  public void process_modifyUserRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	final User oldUser = getUser(request);
	final ResourceBundle language = getLanguage(oldUser.language);

	final User newUser = createUser(request, getRequestAttribute(request, "email"));
	newUser.userID = oldUser.userID;
	if (newUser.email.trim().length() == 0 || newUser.email.equals("-") || newUser.email.indexOf("@") == -1)
	{
	  writeErrorResponse(response, language.getString("authentication.failed") + " " + language.getString("email") + ": ["
		  + newUser.email + "]");
	  return;
	}

	servletDAO.modifyUser(newUser, oldUser, language);

	request.getSession(false).setAttribute("userID", servletDAO.getUser(oldUser.userID));

	response.sendRedirect("jsp/main.jsp");
  }

  public void process_newUserIDsRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	final User user = getUser(request);
	final ResourceBundle language = getLanguage(user.language);

	servletDAO.newUserIDs();

	final StringBuffer buff = new StringBuffer();
	buff.append("<html><body>");

	buff.append("Randomize...<p>");
	buff.append("<a href='index.html'>" + language.getString("proceed.to.login") + "</a></body></html>");

	writeResponse(response, buff);
  }

  public void process_newUserIDsFromOneRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final User user = getUser(request);
	final ResourceBundle language = getLanguage(user.language);

	servletDAO.newUserIDsFromOne();

	final StringBuffer buff = new StringBuffer();
	buff.append("<html><body>");

	buff.append("Randomize...<p>");
	buff.append("<a href='index.html'>" + language.getString("proceed.to.login") + "</a></body></html>");

	writeResponse(response, buff);
  }

  public void process_deleteUserRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	final User user = getUser(request);
	final ResourceBundle language = getLanguage(user.language);

	servletDAO.deleteUser(user.userID);

	final StringBuffer buff = new StringBuffer();
	buff.append("<html><body>");

	buff.append("<a href='index.html'>" + language.getString("proceed.to.login") + "</a></body></html>");

	writeResponse(response, buff);

  }

  public void sendEmail(final User user, final boolean insertUserDetails) throws Exception
  {
	if (user.email.trim().length() == 0 || user.email.equals("-") || user.email.indexOf("@") == -1)
	{
	  return;
	}

	final StringBuffer message = new StringBuffer();
	final ResourceBundle language = getLanguage(user.language);

	message.append("<html><body>");

	String paramNames[] = new String[] { language.getString("last.name"), language.getString("first.name"),
	    language.getString("year.of.birth"), language.getString("city"), language.getString("country") };

	String paramValues[] = new String[] { user.lastName, user.firstName, String.valueOf(user.yearOfBirth), user.city,
	    user.country

	};

	for (int i = 0; i < paramValues.length; i++)
	{
	  message.append("<p>");
	  message.append("<b>");
	  message.append(paramNames[i]);
	  message.append(":</b> ");
	  message.append(paramValues[i]);
	  message.append("<p>");
	}

	if (insertUserDetails)
	{
	  paramNames = new String[] { language.getString("email"), language.getString("password"),

	  language.getString("language"), language.getString("address"), language.getString("telephone") };

	  paramValues = new String[] { user.email, user.password, user.language, user.address, user.telephone

	  };

	  for (int i = 0; i < paramValues.length; i++)
	  {
		message.append("<p>");
		message.append("<b>");
		message.append(paramNames[i]);
		message.append(":</b> ");
		message.append(paramValues[i]);
		message.append("<p>");
	  }

	}

	final List<Model> models = servletDAO.getModels(user.userID);

	if (!models.isEmpty())
	{
	  message.append("<hr>");
	  message.append("<p>");
	  message.append(language.getString("email.body1"));
	  message.append("<p>");
	  message.append(language.getString("email.body2"));
	  message.append("<p>");
	  message.append(language.getString("email.body3"));
	  message.append("<p>");
	  message.append("<hr>");

	  for (final Model model : models)
	  {
		message.append("<p>");
		paramNames = new String[] { language.getString("modelID"), language.getString("models.name"),
		    language.getString("scale"), language.getString("models.identification"), language.getString("models.markings"),
		    language.getString("models.producer"), language.getString("show"), language.getString("category.code"),
		    language.getString("category.description") };

		final Category category = servletDAO.getCategory(model.categoryID);

		paramValues = new String[] { String.valueOf(model.modelID), model.name, model.scale, model.identification,
		    model.markings, model.producer, category.group.show, category.categoryCode, category.categoryDescription };

		for (int i = 0; i < paramValues.length; i++)
		{
		  message.append("<b>");
		  message.append(paramNames[i]);
		  message.append(":</b> ");
		  message.append(paramValues[i]);
		  message.append("<br>");
		}
	  }
	}

	message.append("</body></html>");

	sendMessage(user.email, language.getString("email.subject"), message.toString());
  }

  public void process_addCategoryRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	final Category category = new Category(servletDAO.getNextID("CATEGORY", "CATEGORY_ID"), getRequestAttribute(request,
	    "categorycode"), getRequestAttribute(request, "categorydescription"), servletDAO.getCategoryGroup(
	    Integer.parseInt(getRequestAttribute(request, "categoryGroupID")), servletDAO.getCategoryGroups()));

	servletDAO.saveCategory(category);

	response.sendRedirect("jsp/main.jsp");
  }

  public void process_addCategoryGroupRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final CategoryGroup categoryGroup = new CategoryGroup(servletDAO.getNextID("CATEGORY_GROUP", "CATEGORY_group_ID"),
	    getRequestAttribute(request, "show"), getRequestAttribute(request, "group"));

	servletDAO.saveCategoryGroup(categoryGroup);

	response.sendRedirect("jsp/main.jsp");
  }

  public void process_listUsersRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	writeResponse(response, getUserTable(getUser(request).language));
  }

  public void process_listCategoriesRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final StringBuffer buff = new StringBuffer();

	getCategoryTable(buff, servletDAO.getCategoryList((String) request.getSession().getAttribute("show")),
	    getLanguage(getUser(request).language));

	writeResponse(response, buff);
  }

  private void getCategoryTable(final StringBuffer buff, final List<Category> categories, final ResourceBundle language)
	  throws SQLException
  {
	buff.append("<table border=1>");
	buff.append("<tr>");
	buff.append("<th>CategoryID</th>");

	buff.append("<th style='white-space: nowrap'>");
	buff.append(language.getString("show"));
	buff.append("</th>");

	buff.append("<th style='white-space: nowrap'>");
	buff.append(language.getString("group"));
	buff.append("</th>");

	buff.append("<th style='white-space: nowrap'>");
	buff.append(language.getString("category.code"));
	buff.append("</th>");

	buff.append("<th style='white-space: nowrap'>");
	buff.append(language.getString("category.description"));
	buff.append("</th>");

	buff.append("</tr>");

	for (final Category category : categories)
	{
	  buff.append("<tr>");
	  buff.append("<td align='center' >");
	  buff.append(category.categoryID);
	  buff.append("</td>");
	  buff.append("<td align='center' >");
	  buff.append(category.group.show);
	  buff.append("</td>");
	  buff.append("<td align='center' >");
	  buff.append(category.group.name);
	  buff.append("</td>");
	  buff.append("<td align='center' >");
	  buff.append(category.categoryCode);
	  buff.append("</td>");
	  buff.append("<td align='center' >");
	  buff.append(category.categoryDescription);
	  buff.append("</td>");
	  buff.append("</tr>");
	}

	buff.append("</table>");
  }

  public StringBuffer getUserTable(final String languageCode) throws Exception
  {
	final ResourceBundle language = getLanguage(languageCode);

	final StringBuffer buff = new StringBuffer();

	final List<User> users = servletDAO.getUsers();

	buff.append("<table border=1>");
	buff.append("<tr>");

	buff.append("<th style='white-space: nowrap'>");
	buff.append(language.getString("userID"));
	buff.append("</th>");

	buff.append("<th style='white-space: nowrap'>");
	buff.append(language.getString("email"));
	buff.append("</th>");

	if ("ADMIN".equals(languageCode))
	{
	  buff.append("<th style='white-space: nowrap'>");
	  buff.append(language.getString("password"));
	  buff.append("</th>");
	}

	buff.append("<th style='white-space: nowrap'>");
	buff.append(language.getString("last.name"));
	buff.append("</th>");

	buff.append("<th style='white-space: nowrap'>");
	buff.append(language.getString("first.name"));
	buff.append("</th>");

	buff.append("<th style='white-space: nowrap'>");
	buff.append(language.getString("year.of.birth"));
	buff.append("</th>");

	buff.append("<th style='white-space: nowrap'>");
	buff.append(language.getString("country"));
	buff.append("</th>");

	buff.append("<th style='white-space: nowrap'>");
	buff.append(language.getString("language"));
	buff.append("</th>");

	buff.append("<th style='white-space: nowrap'>");
	buff.append(language.getString("city"));
	buff.append("</th>");

	buff.append("<th style='white-space: nowrap'>");
	buff.append(language.getString("address"));
	buff.append("</th>");

	buff.append("<th style='white-space: nowrap'>");
	buff.append(language.getString("telephone"));
	buff.append("</th>");

	buff.append("</tr>");

	for (final User user : users)
	{
	  buff.append("<tr>");

	  buff.append("<td align='center' >");
	  buff.append(user.userID);
	  buff.append("</td>");

	  buff.append("<td align='center' >");
	  buff.append(user.email);
	  buff.append("</td>");

	  if ("ADMIN".equals(languageCode))
	  {
		buff.append("<td align='center' >");
		buff.append(user.password);
		buff.append("</td>");
	  }

	  buff.append("<td align='center' >");
	  buff.append(user.lastName);
	  buff.append("</td>");

	  buff.append("<td align='center' >");
	  buff.append(user.firstName);
	  buff.append("</td>");

	  buff.append("<td align='center' >");
	  buff.append(user.yearOfBirth);
	  buff.append("</td>");

	  buff.append("<td align='center' >");
	  buff.append(user.country);
	  buff.append("</td>");

	  buff.append("<td align='center' >");
	  buff.append(user.language);
	  buff.append("</td>");

	  buff.append("<td align='center' >");
	  buff.append(user.city);
	  buff.append("</td>");

	  buff.append("<td align='center' >");
	  buff.append(user.address);
	  buff.append("</td>");

	  buff.append("<td align='center' >");
	  buff.append(user.telephone);
	  buff.append("</td>");

	  buff.append("</tr>");
	}

	buff.append("</table>");

	return buff;
  }

  public void process_inputForAddCategoryRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final ResourceBundle language = getLanguage(getUser(request).language);

	final StringBuffer buff = new StringBuffer();

	buff.append("<html>");
	buff.append("<head>");
	buff.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
	buff.append("</head>");
	buff.append("<body>");

	buff.append("<form accept-charset=\"UTF-8\" name='input' action='RegistrationServlet' method='put'>");
	buff.append("<input type='hidden' name='command' value='addCategory'>");
	buff.append("<table border='0'>");

	buff.append("<tr>");
	buff.append("<td>");
	buff.append(language.getString("group"));
	buff.append(": ");
	buff.append("</td>");

	buff.append("<td>");

	final String show = (String) request.getSession().getAttribute("show");
	for (final CategoryGroup group : servletDAO.getCategoryGroups())
	{
	  if (!group.show.equals(show))
	  {
		continue;
	  }

	  buff.append("<input type='radio' name='categoryGroupID' value='" + group.categoryGroupID + "'/>");
	  buff.append(group.show + " - " + group.name + "<br>");
	}

	buff.append("<font color='#FF0000' size='+3'>&#8226;</font> </td>");

	buff.append("</tr>");

	buff.append("<tr>");
	buff.append("<td>");
	buff.append(language.getString("category.code"));
	buff.append(": ");
	buff.append("</td>");
	buff.append("<td><input type='text' name='categorycode'> <font color='#FF0000' size='+3'>&#8226;</font> </td>");
	buff.append("</tr>");

	buff.append("<tr>");
	buff.append("<td>");
	buff.append(language.getString("category.description"));
	buff.append(": ");
	buff.append("</td>");
	buff.append("<td><input type='text' name='categorydescription'> <font color='#FF0000' size='+3'>&#8226;</font> </td>");
	buff.append("</tr>");

	buff.append("<tr>");
	buff.append("<td></td>");
	buff.append("<td><input name='addCategory' type='submit' value='" + language.getString("save") + "'></td>");
	buff.append("</tr>");

	buff.append("</table>");

	buff.append("<p><font color='#FF0000' size='+3'>&#8226;</font>" + language.getString("mandatory.fields") + "</p>");
	buff.append("</form></body></html>");
	writeResponse(response, buff);
  }

  public void process_inputForModifyModelRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final int modelID = Integer.parseInt(getRequestAttribute(request, "modelID"));

	getModelForm(request, response, "modifyModel", "modify", modelID);
  }

  public void process_inputForAddModelRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final User user = getUser(request);

	if (preRegistrationAllowed || onSiteUse)
	{
	  getModelForm(request, response, "addModel", "add", null);
	}
	else
	{
	  response.sendRedirect("jsp/main.jsp");
	}
  }

  public void process_modifyModelRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	final int modelID = Integer.valueOf(getRequestAttribute(request, "modelID"));

	final Model model = createModel(modelID, servletDAO.getModel(modelID).userID, request);

	servletDAO.deleteModel(request);
	servletDAO.saveModel(model);

	response.sendRedirect("jsp/main.jsp");
  }

  public void process_addModelRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	final Model model = createModel(servletDAO.getNextID("MODEL", "MODEL_ID"), getUser(request).userID, request);

	servletDAO.saveModel(model);

	response.sendRedirect("jsp/main.jsp");
  }

  private Model createModel(final int modelID, final int userID, final HttpServletRequest request) throws Exception
  {
	return createModel(modelID, userID, request, "");
  }

  private Model createModel(final int modelID, final int userID, final HttpServletRequest request,
	  final String httpParameterPostTag) throws Exception
  {
	return new Model(modelID, userID, Integer.parseInt(getRequestAttribute(request, "categoryID" + httpParameterPostTag)),
	    getRequestAttribute(request, "modelscale" + httpParameterPostTag), getRequestAttribute(request, "modelname"
	        + httpParameterPostTag), getRequestAttribute(request, "modelproducer" + httpParameterPostTag),
	    getOptionalRequestAttribute(request, "modelcomment" + httpParameterPostTag), getOptionalRequestAttribute(request,
	        "identification" + httpParameterPostTag), getOptionalRequestAttribute(request, "markings" + httpParameterPostTag),
	    isCheckedIn(request, "gluedToBase" + httpParameterPostTag), getDetailing(request));
  }

  boolean isCheckedIn(final HttpServletRequest request, final String parameter) throws Exception
  {
	try
	{
	  return "on".equalsIgnoreCase(getRequestAttribute(request, parameter));
	}
	catch (final Exception e)
	{
	  return false;
	}
  }

  private Detailing[] getDetailing(final HttpServletRequest request) throws Exception
  {
	final Detailing[] detailing = new Detailing[Detailing.DETAILING_GROUPS.length];

	for (int i = 0; i < detailing.length; i++)
	{
	  final String group = Detailing.DETAILING_GROUPS[i];

	  detailing[i] = new Detailing(group, new boolean[] { isCheckedIn(request, "detailing." + group + ".externalSurface"),
		  isCheckedIn(request, "detailing." + group + ".cockpit"), isCheckedIn(request, "detailing." + group + ".engine"),
		  isCheckedIn(request, "detailing." + group + ".undercarriage"), isCheckedIn(request, "detailing." + group + ".gearBay"),
		  isCheckedIn(request, "detailing." + group + ".armament"), isCheckedIn(request, "detailing." + group + ".conversion") });
	}

	return detailing;
  }

  public static final User getUser(final HttpServletRequest request) throws Exception
  {
	final HttpSession session = request.getSession(false);

	if (session == null)
	{
	  throw new Exception("User is not logged in! <a href='index.html'>Please go to login page...</a>");
	}

	return (User) session.getAttribute("userID");
  }

  public void process_inputForDeleteModelRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final StringBuffer buff = new StringBuffer();

	buff.append("<html><body>");

	buff.append(inputForSelectModel(getUser(request), "deleteModel", "delete"));
	buff.append("</body></html>");

	writeResponse(response, buff);
  }

  public void process_inputForSelectModelForModifyRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final User user = getUser(request);

	if (preRegistrationAllowed || onSiteUse)
	{
	  writeResponse(response, inputForSelectModel(user, "inputForModifyModel", "modify"));
	}
	else
	{
	  response.sendRedirect("jsp/main.jsp");
	}
  }

  public StringBuffer inputForSelectModel(final User user, final String action, final String submitLabel) throws Exception
  {
	final StringBuffer buff = new StringBuffer();
	final ResourceBundle language = getLanguage(user.language);

	buff.append("<form name='input' action='RegistrationServlet' method='put'>");
	buff.append("<input type='hidden' name='command' value='");
	buff.append(action);
	buff.append("'>");

	for (final Model model : servletDAO.getModels(user.userID))
	{
	  buff.append("<input type='radio' name='modelID' value='" + model.modelID + "'/>");
	  buff.append(model.scale + " - " + model.producer + " - " + model.name + "<br>");
	}

	buff.append("<p><input name='");
	buff.append(action);
	buff.append("' type='submit' value='" + language.getString(submitLabel) + "'>");
	buff.append("</form>");

	return buff;
  }

  public void process_inputForDeleteUsersRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final String language = getUser(request).language;

	selectUser(request, response, "deleteUsers", getLanguage(language).getString("delete"), language);
  }

  public void process_inputForLoginUserRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final String language = getRequestAttribute(request, "language");
	selectUser(request, response, "directLogin", getLanguage(language).getString("login"), language);
  }

  public void process_inputForPrintRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	final String language = getRequestAttribute(request, "language");
	selectUser(request, response, "directPrintModels", getLanguage(language).getString("print.models"), language);
  }

  private void selectUser(final HttpServletRequest request, final HttpServletResponse response, final String command,
	  final String submitLabel, final String language) throws Exception, IOException
  {
	final StringBuffer buff = new StringBuffer();

	final String show = (String) request.getSession().getAttribute("show");

	buff.append("<html><body>");

	buff.append("<form name='input' action='RegistrationServlet' method='put'>");
	buff.append("<input type='hidden' name='command' value='" + command + "'>");
	if (show != null)
	{
	  buff.append("<input type='hidden' name='show' value='" + show + "'>");
	}
	buff.append("<input type='hidden' name='language' value='" + language + "'>");

	final List<User> users = servletDAO.getUsers();
	buff.append("<input type='hidden' name='rows' value='" + users.size() + "'>");

	// buff.append("<input name='deleteUsers' type='submit' value='"
	// + submitLabel + "'><p>");
	for (int i = 0; i < users.size(); i++)
	{
	  final User user = users.get(i);

	  buff.append("<input type='checkbox' name='userID" + i + "' value='" + user.userID + "' onClick='document.input.submit()'/>");
	  buff.append(user.lastName + " " + user.firstName + " (" + user.userID + " - " + user.email + " - " + user.yearOfBirth
		  + " - " + user.country + " - " + user.city + " - " + user.address + " - " + user.telephone + ")<br>");
	}

	// buff.append("<p><input name='deleteUsers' type='submit' value='"
	// + submitLabel + "'>");
	buff.append("</form></body></html>");

	writeResponse(response, buff);
  }

  public void process_deleteUsersRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	final Enumeration parameters = request.getParameterNames();
	while (parameters.hasMoreElements())
	{
	  final String param = (String) parameters.nextElement();

	  if (param.startsWith("userID"))
	  {
		final int userID = Integer.parseInt(getRequestAttribute(request, param));

		servletDAO.deleteUser(userID);
	  }
	}

	response.sendRedirect("jsp/main.jsp");

  }

  public void process_deletedirectUsersRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	for (final User user : servletDAO.getUsers())
	{
	  if (user.email.indexOf(DIRECT_USER) > -1)
	  {
		servletDAO.deleteUser(user.userID);
	  }
	}

	response.sendRedirect("jsp/main.jsp");

  }

  public void process_setSystemParameterRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	servletDAO.setSystemParameter(getRequestAttribute(request, "paramName"), getRequestAttribute(request, "paramValue"));

	response.sendRedirect("jsp/main.jsp");

  }

  public void process_deleteModelRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	servletDAO.deleteModel(request);

	response.sendRedirect("jsp/main.jsp");

  }

  public void process_deleteAwardedModelRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	servletDAO.deleteAwardedModel(request);

	response.sendRedirect("jsp/main.jsp");

  }

  public void process_inputForDeleteCategoryRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final StringBuffer buff = new StringBuffer();
	final ResourceBundle language = getLanguage(getUser(request).language);

	buff.append("<html><body>");

	buff.append("<form name='input' action='RegistrationServlet' method='put'>");
	buff.append("<input type='hidden' name='command' value='deleteCategory'>");

	getHTMLCodeForCategorySelect(buff, language.getString("select"), "", false, language, request);

	buff.append("<p><input name='deleteCategory' type='submit' value='" + language.getString("delete") + "'>");
	buff.append("</form></body></html>");

	writeResponse(response, buff);
  }

  public void process_inputForDeleteCategoryGroupRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final StringBuffer buff = new StringBuffer();
	final ResourceBundle language = getLanguage(getUser(request).language);

	buff.append("<html><body>");

	buff.append("<form name='input' action='RegistrationServlet' method='put'>");
	buff.append("<input type='hidden' name='command' value='deleteCategoryGroup'>");

	final String show = (String) request.getSession().getAttribute("show");

	for (final CategoryGroup group : servletDAO.getCategoryGroups())
	{
	  if (!group.show.equals(show))
	  {
		continue;
	  }

	  buff.append("<input type='radio' name='categoryGroupID' value='" + group.categoryGroupID + "'/>");
	  buff.append(group.show + " - " + group.name + "<br>");
	}

	buff.append("<p><input name='deleteCategoryGroup' type='submit' value='" + language.getString("delete") + "'>");
	buff.append("</form></body></html>");

	writeResponse(response, buff);
  }

  public void process_deleteCategoryGroupRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	servletDAO.deleteCategoryGroup(request);

	response.sendRedirect("jsp/main.jsp");

  }

  public void process_deleteCategoryRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	servletDAO.deleteCategory(request);

	response.sendRedirect("jsp/main.jsp");

  }

  public void process_listAllModelsRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	final StringBuffer buff = new StringBuffer();
	final List<Model> models = servletDAO.getModels(ServletDAO.INVALID_USERID);

	final String show = (String) request.getSession().getAttribute("show");
	final Iterator<Model> it = models.iterator();
	while (it.hasNext())
	{
	  final Model model = it.next();

	  if (!servletDAO.getCategory(model.categoryID).group.show.equals(show))
	  {
		it.remove();
	  }
	}

	getModelTable(buff, models, getLanguage(getUser(request).language),
	    Boolean.parseBoolean(getRequestAttribute(request, "withDetailing")));

	writeResponse(response, buff);
  }

  public void process_printAllModelsRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final ResourceBundle language = getLanguage(getUser(request).language);

	// boolean printPreRegisteredModels =
	// Boolean.parseBoolean(getRequestAttribute(request,
	// "printPreRegisteredModels"));

	for (final User user : servletDAO.getUsers())
	{
	  // if ((printPreRegisteredModels && user.userName.indexOf(DIRECT_USER) ==
	  // -1)
	  // || (!printPreRegisteredModels && user.userName.indexOf(DIRECT_USER) >
	  // -1))
	  printModels(request, response, language, user.userID);
	}

	showPrintDialog(response);
  }

  public void process_printCardsForAllModelsRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final User user = getUser(request);
	final ResourceBundle language = getLanguage(user.language);

	final int cols = 2;
	final int rows = 4;

	final List<Model> allModels = new LinkedList<Model>();
	final List<Category> categories = servletDAO.getCategoryList((String) request.getSession().getAttribute("show"));
	for (final Category category : categories)
	{
	  final List<Model> models = servletDAO.getModelsInCategory(category.categoryID);
	  for (final Model model : models)
	  {
		model.identification = category.group.show;
	  }

	  allModels.addAll(models);
	}

	do
	{
	  final List<Model> sublist = allModels.subList(0, Math.min(cols * rows, allModels.size()));

	  writeResponse(response, printModels(language, user, sublist, printCardBuffer, rows, cols, true));
	  sublist.clear();
	}
	while (!allModels.isEmpty());

	showPrintDialog(response);
  }

  public void process_printMyModelsRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	final User user = getUser(request);

	final String modelID = getOptionalRequestAttribute(request, "modelID");

	if ("-".equals(modelID))
	{
	  printModels(request, response, getLanguage(user.language), user.userID);
	}
	else
	{
	  final StringBuffer buff = new StringBuffer();

	  final List<Model> models = servletDAO.getModels(user.userID);
	  for (final Model model : models)
	  {
		if (model.modelID == Integer.parseInt(modelID))
		{
		  final List<Model> subList = new LinkedList<Model>();
		  subList.add(model);

		  buff.append(printModels(getLanguage(user.language), servletDAO.getUser(user.userID), subList, printBuffer, 1, 2, true));
		  break;
		}
	  }

	  writeResponse(response, buff);
	}

	showPrintDialog(response);
  }

  private void printModels(final HttpServletRequest request, final HttpServletResponse response, final ResourceBundle language,
	  final int userID) throws Exception, IOException
  {
	writeResponse(response, printModels(language, userID, request));
  }

  public StringBuffer printModels(final ResourceBundle language, final int userID, final HttpServletRequest request)
	  throws Exception, IOException
  {
	final List<Model> models = servletDAO.getModels(userID);

	final String show = (String) request.getSession().getAttribute("show");
	final Iterator<Model> it = models.iterator();
	while (it.hasNext())
	{
	  final Model model = it.next();

	  if (show != null && !servletDAO.getCategory(model.categoryID).group.show.equals(show))
	  {
		it.remove();
	  }
	}

	if (models.isEmpty())
	{
	  return new StringBuffer();
	}

	final StringBuffer buff = new StringBuffer();

	User user = servletDAO.getUser(userID);
	while (!models.isEmpty())
	{
	  final List<Model> subList = new LinkedList<Model>();
	  subList.add(models.remove(0));
	  if (!models.isEmpty())
	  {
		subList.add(models.remove(0));
	  }

	  buff.append(printModels(language, user, subList, printBuffer, 1, 2, !models.isEmpty()));
	}

	return buff;

  }

  StringBuffer printModels(final ResourceBundle language, final User user, final List<Model> models,
	  final StringBuffer printBuffer, final int rows, final int cols, boolean pageBreak) throws Exception, IOException
  {
	// System.out.println(models.size() + " " + rows + " " + cols);

	final int width = 100 / cols;
	final int height = 100 / rows;

	final StringBuffer buff = new StringBuffer();

	buff.append("<table cellpadding='0' cellspacing='10' width='100%' height='100%' "
	    + (pageBreak ? "style='page-break-after: always;' " : "") + "border='0' >");

	int ModelCount = 0;
	for (int row = 0; row < rows; row++)
	{
	  buff.append("<tr>");
	  for (int col = 0; col < cols; col++)
	  {
		buff.append("<td width='" + width + "%' height='" + height + "%'>");

		final Model model = ModelCount < models.size() ? models.get(ModelCount) : null;
		ModelCount++;

		if (model != null)
		{
		  String print = printBuffer.toString().replaceAll("__LASTNAME__", String.valueOf(user.lastName))
			  .replaceAll("__FIRSTNAME__", String.valueOf(user.firstName))
			  .replaceAll("__YEAROFBIRTH__", String.valueOf(user.yearOfBirth))

			  .replaceAll("__CITY__", String.valueOf(user.city)).replaceAll("__COUNTRY__", String.valueOf(user.country))

			  .replaceAll("__USER_ID__", String.valueOf(model.userID)).replaceAll("__MODEL_ID__", String.valueOf(model.modelID))
			  .replaceAll("__YEAR_OF_BIRTH__", String.valueOf(servletDAO.getUser(model.userID).yearOfBirth))
			  .replaceAll("__MODEL_SCALE__", model.scale)
			  .replaceAll("__CATEGORY_CODE__", servletDAO.getCategory(model.categoryID).categoryCode)
			  .replaceAll("__MODEL_NAME__", model.name).replaceAll("__MODEL_NATIONALITY__", model.markings)
			  .replaceAll("__MODEL_IDENTIFICATION__", model.identification).replaceAll("__MODEL_PRODUCER__", model.producer)
			  .replaceAll("__MODEL_COMMENT__", model.comment).replaceAll("__GLUED_TO_BASE__", model.gluedToBase ?

			  "<img src='icons/glued.jpg'> " : "<img src='icons/notglued.jpg'> "

			  // "<font color='#006600'>Alapra ragasztva</font>"
			  // :
			  // "<font color='#FF0000'><strong>Nincs leragasztva!!! </strong></font> "

			  );

		  final String[] detailingGroups = new String[] { "SCRATCH", "PE", "RESIN", "DOC" };

		  final String detailingCriterias[] = new String[] { "externalSurface", "cockpit", "engine", "undercarriage", "gearBay",
			  "armament", "conversion" };

		  for (int i = 0; i < detailingGroups.length; i++)
		  {
			for (int j = 0; j < detailingCriterias.length; j++)
			{
			  print = print.replaceAll("__" + detailingGroups[i] + "_" + detailingCriterias[j] + "__",
				  model.detailing[i].criterias[j] ? "<font size='+3'>&#8226;</font>"
				      : "<font size='+3' color='#FFFFFF'>&nbsp</font>");
			}
		  }

		  buff.append(print);
		}

		buff.append("</td>");
	  }
	  buff.append("</tr>");
	}

	buff.append("</table>");

	return buff;
  }

  public void process_inputForModifyUserRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final HttpSession session = request.getSession(true);

	session.setAttribute("directRegister", false);
	session.setAttribute("action", "modifyUser");

	response.sendRedirect("jsp/user.jsp");
  }

  public void process_listMyModelsRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	final StringBuffer buff = new StringBuffer();
	getModelTable(buff, servletDAO.getModels(getUser(request).userID), getLanguage(getUser(request).language), true);

	writeResponse(response, buff);
  }

  public void process_selectModelRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	final StringBuffer buff = new StringBuffer();
	getModelTable(buff, servletDAO.selectModels(request), getLanguage(getUser(request).language), true);

	writeResponse(response, buff);
  }

  private StringBuffer getModelTable(final StringBuffer buff, final List<Model> models, final ResourceBundle language,
	  final boolean withDetailing) throws SQLException, Exception
  {
	return getModelTable(buff, models, language, withDetailing, false);
  }

  private StringBuffer getModelTable(final StringBuffer buff, final List<Model> models, final ResourceBundle language,
	  final boolean withDetailing, final boolean insertAwards) throws SQLException, Exception
  {

	buff.append("<table border=1>");
	buff.append("<tr>");

	if (insertAwards)
	{
	  buff.append("<th align='center' style='white-space: nowrap'>");
	  buff.append(language.getString("award"));
	  buff.append("</th>");
	}

	buff.append("<th align='center' style='white-space: nowrap'>");
	buff.append(language.getString("last.name"));
	buff.append("</th>");

	buff.append("<th align='center' style='white-space: nowrap'>");
	buff.append(language.getString("first.name"));
	buff.append("</th>");

	buff.append("<th align='center' style='white-space: nowrap'>");
	buff.append(language.getString("city"));
	buff.append("</th>");

	buff.append("<th align='center' style='white-space: nowrap'>");
	buff.append(language.getString("country"));
	buff.append("</th>");

	if (!insertAwards)
	{
	  buff.append("<th align='center' style='white-space: nowrap'>");
	  buff.append(language.getString("userID"));
	  buff.append("</th>");

	  // buff.append("<th align='center' style='white-space: nowrap'>");
	  // buff.append(language.getString("user.name"));
	  // buff.append("</th>");

	  buff.append("<th align='center' style='white-space: nowrap'>");
	  buff.append(language.getString("modelID"));
	  buff.append("</th>");
	}

	buff.append("<th align='center' style='white-space: nowrap'>");
	buff.append(language.getString("models.name"));
	buff.append("</th>");

	if (!insertAwards)
	{
	  buff.append("<th align='center' style='white-space: nowrap'>");
	  buff.append(language.getString("scale"));
	  buff.append("</th>");

	  buff.append("<th align='center' style='white-space: nowrap'>");
	  buff.append(language.getString("models.identification"));
	  buff.append("</th>");

	  buff.append("<th align='center' style='white-space: nowrap'>");
	  buff.append(language.getString("models.markings"));
	  buff.append("</th>");

	  buff.append("<th align='center' style='white-space: nowrap'>");
	  buff.append(language.getString("models.producer"));
	  buff.append("</th>");

	  // buff.append("<th>categoryID</th>");
	}

	buff.append("<th align='center' style='white-space: nowrap'>");
	buff.append(language.getString("show"));
	buff.append("</th>");

	buff.append("<th align='center' style='white-space: nowrap'>");
	buff.append(language.getString("category.code"));
	buff.append("</th>");

	buff.append("<th align='center' style='white-space: nowrap'>");
	buff.append(language.getString("category.description"));
	buff.append("</th>");

	if (!insertAwards)
	{
	  if (withDetailing)
	  {
		buff.append("<th align='center' style='white-space: nowrap'>");
		buff.append(language.getString("models.detailing"));
		buff.append("</th>");
	  }

	  buff.append("<th align='center' style='white-space: nowrap'>");
	  buff.append(language.getString("glued.to.base"));
	  buff.append("</th>");

	  buff.append("<th align='center' style='white-space: nowrap'>");
	  buff.append(language.getString("comment"));
	  buff.append("</th>");
	}

	buff.append("</tr>");

	for (final Model model : models)
	{
	  buff.append("<tr>");

	  final User modelsUser = servletDAO.getUser(model.userID);
	  // buff.append("<td align='center'>");
	  // buff.append(modelsUser.userName);
	  // buff.append("</td>");

	  if (insertAwards)
	  {
		buff.append("<td align='center'>");
		buff.append(model.award);
		buff.append("</td>");
	  }

	  buff.append("<td align='center'>");
	  buff.append(modelsUser.lastName);
	  buff.append("</td>");

	  buff.append("<td align='center'>");
	  buff.append(modelsUser.firstName);
	  buff.append("</td>");

	  buff.append("<td align='center'>");
	  buff.append(modelsUser.city);
	  buff.append("</td>");

	  buff.append("<td align='center'>");
	  buff.append(modelsUser.country);
	  buff.append("</td>");

	  if (!insertAwards)
	  {
		buff.append("<td align='center'>");
		buff.append(model.userID);
		buff.append("</td>");

		buff.append("<td align='center'>");
		buff.append(model.modelID);
		buff.append("</td>");
	  }

	  buff.append("<td align='center' style='white-space: nowrap'>");
	  buff.append(model.name);
	  buff.append("</td>");

	  if (!insertAwards)
	  {
		buff.append("<td align='center'>");
		buff.append(model.scale);
		buff.append("</td>");

		buff.append("<td align='center' style='white-space: nowrap'>");
		buff.append(model.identification);
		buff.append("</td>");

		buff.append("<td align='center' style='white-space: nowrap'>");
		buff.append(model.markings);
		buff.append("</td>");

		buff.append("<td align='center' style='white-space: nowrap'>");
		buff.append(model.producer);
		buff.append("</td>");

		// buff.append("<td>");
		// buff.append(model.categoryID);
		// buff.append("</td>");
	  }

	  final Category category = servletDAO.getCategory(model.categoryID);

	  buff.append("<td align='center' style='white-space: nowrap'>");
	  buff.append(category.group.show);
	  buff.append("</td>");

	  buff.append("<td align='center' style='white-space: nowrap'>");
	  buff.append(category.categoryCode);
	  buff.append("</td>");

	  buff.append("<td align='center' style='white-space: nowrap'>");
	  buff.append(category.categoryDescription);
	  buff.append("</td>");

	  if (!insertAwards)
	  {
		if (withDetailing)
		{
		  buff.append("<td><table cellpadding='5' border='1'>");
		  buff.append("<tr>");
		  buff.append("<td>&nbsp;</td>");

		  for (int i = 0; i < Detailing.DETAILING_GROUPS.length; i++)
		  {
			buff.append("<td>");
			buff.append(language.getString("detailing." + Detailing.DETAILING_GROUPS[i]));
			buff.append("</td>");
		  }
		  buff.append("</tr>");

		  for (int i = 0; i < Detailing.DETAILING_CRITERIAS.length; i++)
		  {
			buff.append("<tr>");
			buff.append("<td>");
			buff.append(language.getString("detailing." + Detailing.DETAILING_CRITERIAS[i]));
			buff.append("</td>");

			for (int j = 0; j < Detailing.DETAILING_GROUPS.length; j++)
			{
			  buff.append("<td><input  type='checkbox' " + (model.detailing[j].criterias[i] ? "checked" : "") + "></td>");
			}

			buff.append("</tr>");
		  }
		  buff.append("</table></td>");
		}

		buff.append("<td align='center'><input  type='checkbox' " + (model.gluedToBase ? "checked" : "") + "></td>");

		buff.append("<td align='center'>");
		buff.append(model.comment);
		buff.append("</td>");
	  }

	  buff.append("</tr>");
	}

	buff.append("</table>");
	return buff;
  }

  public void sendMessage(final String to, final String subject, final String message) throws Exception
  {

	// create some properties and get the default Session
	final Properties props = new Properties();
	props.put("mail.smtp.host", smtpServer);
	props.put("mail.debug", debugSMTP);

	if (emailFrom == null)
	{
	  throw new Exception("!!! SendMail.sendMessage: FROM address is null!");
	}

	if (emailFrom.indexOf("@") == -1)
	{
	  throw new Exception("!!! SendMail.sendMessage: invalid FROM e-mail address: " + emailFrom);
	}

	if (to == null)
	{
	  throw new Exception("!!! SendMail.sendMessage: TO address is null !");
	}

	final Session session = Session.getDefaultInstance(props, null);
	session.setDebug(debugSMTP);

	// create a message
	final Message msg = new MimeMessage(session);
	msg.setFrom(new InternetAddress(emailFrom));

	final InternetAddress[] address = new InternetAddress[] { new InternetAddress(to) };

	msg.setRecipients(Message.RecipientType.TO, address);
	msg.setSubject(subject);
	msg.setSentDate(new Date());
	// If the desired charset is known, you can use setText(text, charset)
	msg.setText(message);
	msg.setHeader("Content-Type", "text/html");

	Transport.send(msg);
  }

  public void process_sendEmailRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	final User user = getUser(request);

	sendEmail(user, false);

	response.sendRedirect("jsp/main.jsp");
  }

  public void process_logoutRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	updateSystemSettings();

	final User user = getUser(request);

	if (!onSiteUse)
	{
	  sendEmail(user, false);
	}

	final HttpSession session = request.getSession(false);

	if (session != null)
	{
	  session.invalidate();
	}

	if (onSiteUse)
	{
	  response.sendRedirect("helyi.html");
	}
	else
	{
	  final StringBuffer buff = new StringBuffer();
	  buff.append("<html><body>");

	  buff.append("<p>");
	  buff.append("<a href='index.html'>");
	  buff.append(getLanguage(user.language).getString("proceed.to.login"));
	  buff.append("</a></body></html>");

	  writeResponse(response, buff);
	}
  }

  private void updateSystemSettings() throws Exception
  {
	preRegistrationAllowed = servletDAO.getYesNoSystemParameter(ServletDAO.SYSTEMPARAMETER.REGISTRATION);
	onSiteUse = servletDAO.getYesNoSystemParameter(ServletDAO.SYSTEMPARAMETER.ONSITEUSE);
	systemMessage = servletDAO.getSystemParameter(ServletDAO.SYSTEMPARAMETER.SYSTEMMESSAGE);
  }

  public void process_deleteDataRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {
	servletDAO.deleteEntries("MAK_CATEGORY_GROUP");

	servletDAO.deleteEntries("MAK_CATEGORY");

	servletDAO.deleteEntries("MAK_MODEL");

	servletDAO.deleteEntries("MAK_AWARDEDMODELS");

	response.sendRedirect("jsp/main.jsp");

  }

  public void process_statisticsRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception
  {

	final StringBuffer buff = new StringBuffer();

	buff.append("<table border=1>");

	String show = getOptionalRequestAttribute(request, "show");
	if ("-".equals(show))
	{
	  show = (String) request.getSession().getAttribute("show");
	}

	for (final String[] stat : servletDAO.getStatistics(show))
	{
	  buff.append("<tr><td>");
	  buff.append(stat[0]);
	  buff.append("</td><td>");
	  buff.append(stat[1]);
	  buff.append("</td></tr>");
	}

	buff.append("</table>");

	writeResponse(response, buff);
  }

  public void process_exceptionHistoryRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final StringBuffer buff = new StringBuffer();

	for (final ExceptionData data : exceptionHistory)
	{
	  buff.append(data.toHTML());
	}

	writeResponse(response, buff);
  }

  public ResourceBundle getLanguage(final String language)
  {
	try
	{
	  if (!languages.containsKey(language))
	  {
		languages.put(language, ResourceBundle.getBundle("language", new Locale(language, language)));
	  }
	}
	catch (final MissingResourceException e)
	{
	  logger.error("getLanguage(): country: " + language, e);

	  // cache default language
	  languages.put(language, ResourceBundle.getBundle("language", new Locale("", "")));
	}

	logger.trace("getLanguage(): " + language
	// + " " + languages.get(language).getString("list.models")
	    );

	return languages.get(language);
  }

  public void process_getawardedModelsPageRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final StringBuffer buff = new StringBuffer();
	final String languageCode = getUser(request).language;
	final ResourceBundle language = getLanguage(languageCode);

	buff.append(awardedModelsBuffer.toString().replaceAll("__ADDNEWROW__", language.getString("add.new.row"))
	    .replaceAll("__SELECT__", language.getString("list.models")).replaceAll("__PRINT__", language.getString("print.models"))
	    .replaceAll("__PRESENTATION__", language.getString("presentation")).replaceAll("__SAVE__", language.getString("save"))

	    .replaceAll("__MODELID__", language.getString("modelID")).replaceAll("__AWARD__", language.getString("award"))
	    .replaceAll("__LANGUAGE__", languageCode));
	writeResponse(response, buff);
  }

  public void process_listAwardedModelsRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final String languageCode = getRequestAttribute(request, "language");
	final ResourceBundle language = getLanguage(languageCode);

	final int rows = Integer.parseInt(getRequestAttribute(request, "rows"));

	final List<Model> models = new LinkedList<Model>();
	for (int i = 1; i <= rows; i++)
	{
	  final String httpParameterPostTag = String.valueOf(i);

	  final String modelID = getOptionalRequestAttribute(request, "modelID" + httpParameterPostTag).trim();
	  if (modelID.length() == 0 || "-".endsWith(modelID))
	  {
		continue;
	  }

	  final Model model = servletDAO.getModel(Integer.parseInt(modelID));
	  model.award = getRequestAttribute(request, "award" + httpParameterPostTag).trim();
	  models.add(model);
	}
	writeResponse(response, getModelTable(new StringBuffer(), models, language, false, true));
  }

  public void process_printAwardedModelsRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	printAwardedModels(request, response, cerificateOfMeritBuffer);
  }

  public void process_getPresentationPageRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	printAwardedModels(request, response, presentationBuffer);
  }

  public void process_addAwardedModelsRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final int rows = Integer.parseInt(getRequestAttribute(request, "rows"));

	for (int i = 1; i <= rows; i++)
	{
	  final String httpParameterPostTag = String.valueOf(i);

	  final String modelID = getRequestAttribute(request, "modelID" + httpParameterPostTag).trim();
	  if (modelID.length() == 0)
	  {
		continue;
	  }

	  final Model model = servletDAO.getModel(Integer.parseInt(modelID));
	  final User user = servletDAO.getUser(model.userID);
	  // Category category = servletDAO.getCategory(model.categoryID);

	  final String award = getRequestAttribute(request, "award" + httpParameterPostTag).trim();

	  servletDAO.saveAwardedModel(new AwardedModel(award, model));
	}

	response.sendRedirect("jsp/main.jsp");
  }

  private void printAwardedModels(final HttpServletRequest request, final HttpServletResponse response, final StringBuffer buffer)
	  throws Exception, IOException
  {
	final String languageCode = getRequestAttribute(request, "language");
	final ResourceBundle language = getLanguage(languageCode);

	final int rows = Integer.parseInt(getRequestAttribute(request, "rows"));

	final StringBuffer buff = new StringBuffer();
	for (int i = 1; i <= rows; i++)
	{
	  final String httpParameterPostTag = String.valueOf(i);

	  final String modelID = getOptionalRequestAttribute(request, "modelID" + httpParameterPostTag).trim();
	  if (modelID.length() == 0 || "-".endsWith(modelID))
	  {
		continue;
	  }

	  final Model model = servletDAO.getModel(Integer.parseInt(modelID));
	  final User user = servletDAO.getUser(model.userID);
	  final Category category = servletDAO.getCategory(model.categoryID);

	  buff.append(buffer.toString().replaceAll("__LASTNAME__", String.valueOf(user.lastName))
		  .replaceAll("__FIRSTNAME__", String.valueOf(user.firstName))
		  .replaceAll("__CATEGORY_CODE__", String.valueOf(category.categoryCode))
		  .replaceAll("__CATEGORY_CODE__", String.valueOf(category.categoryCode))
		  .replaceAll("__MODEL_NAME__", String.valueOf(model.name)).replaceAll("__MODEL_ID__", String.valueOf(model.modelID))

		  .replaceAll("__AWARD__", getRequestAttribute(request, "award" + httpParameterPostTag).trim()));
	}

	writeResponse(response, buff);
  }

  public void process_getbatchAddModelPageRequest(final HttpServletRequest request, final HttpServletResponse response)
	  throws Exception
  {
	final StringBuffer buff = new StringBuffer();
	final String languageCode = getRequestAttribute(request, "language");
	final ResourceBundle language = getLanguage(languageCode);

	final StringBuffer categoriesBuff = new StringBuffer();
	//	getHTMLCodeForCategorySelect(categoriesBuff, language.getString("select"), "", true, language, request);
	//	final StringBuffer countryBuff = new StringBuffer();
	//	getHTMLCodeForCountrySelect(countryBuff, language, language.getString("select"), "HU", "country");

	buff.append(batchAddModelBuffer
	    .toString()
	    .replaceAll("__ADDNEWROW__", language.getString("add.new.row"))
	    .replaceAll("__ADD__", language.getString("add"))
	    .replaceAll("__CATEGORIES_LIST__", categoriesBuff.toString())
	    //	    .replaceAll("__YEAROFBIRTHS_LIST__",
	    //	        yearBuffer.toString().replaceAll("__SELECTVALUE__", "2009").replaceAll("__YEAROFBIRTH__", "2009"))

	    //	    .replaceAll("__MODELSCALES_LIST__",
	    //	        scalesBuffer.toString().replaceAll("__SELECTVALUE__", "").replaceAll("__FREQUENTLYUSED__", ""))

	    //	    .replaceAll("__MODELPRODUCERS_LIST__",
	    //	        modelproducersBuffer.toString().replaceAll("__SELECTVALUE__", "").replaceAll("__FREQUENTLYUSED__", ""))
	    .replaceAll("__MANDATORYFIELDS__", language.getString("mandatory.fields"))

	    //	    .replaceAll("__COUNTRIES_LIST__", countryBuff.toString())

	    .replaceAll("__LASTNAMELABEL__", language.getString("last.name"))
	    .replaceAll("__FIRSTNAMELABEL__", language.getString("first.name"))
	    .replaceAll("__YEAROFBIRTHLABEL__", language.getString("year.of.birth"))
	    .replaceAll("__MODEL_SCALE__", language.getString("scale"))
	    .replaceAll("__MODEL_NAME__", language.getString("models.name"))
	    .replaceAll("__MODEL_PRODUCER__", language.getString("models.producer"))
	    .replaceAll("__CATEGORY_CODE__", language.getString("category")).replaceAll("__LANGUAGE__", languageCode)
	    .replaceAll("__GLUED_TO_BASE__", language.getString("glued.to.base")).replaceAll("__YES__", language.getString("yes"))
	    .replaceAll("__NO__", language.getString("no")).replaceAll("__COUNTRYLABEL__", language.getString("country"))
	    .replaceAll("__EMAILLABEL__", language.getString("email")).replaceAll("__LOGOUT__", language.getString("logout"))

	);
	writeResponse(response, buff);
  }

  private User createUser(final HttpServletRequest request, final String email) throws Exception
  {
	return createUser(request, email, "");
  }

  private User createUser(final HttpServletRequest request, final String email, final String httpParameterPostTag)
	  throws Exception
  {
	return createUser(request, email, getRequestAttribute(request, "password" + httpParameterPostTag), httpParameterPostTag);
  }

  private User createUser(final HttpServletRequest request, final String email, final String password,
	  final String httpParameterPostTag) throws Exception
  {
	// check if all data is sent
	getRequestAttribute(request, "language");

	getRequestAttribute(request, "firstname" + httpParameterPostTag);
	getRequestAttribute(request, "lastname" + httpParameterPostTag);
	getRequestAttribute(request, "country" + httpParameterPostTag);
	getOptionalRequestAttribute(request, "city" + httpParameterPostTag);
	getOptionalRequestAttribute(request, "address" + httpParameterPostTag);
	getOptionalRequestAttribute(request, "telephone" + httpParameterPostTag);

	getRequestAttribute(request, "yearofbirth" + httpParameterPostTag);

	return new User(servletDAO.getNextID("USERS", "USER_ID"), password, getRequestAttribute(request, "firstname"
	    + httpParameterPostTag), getRequestAttribute(request, "lastname" + httpParameterPostTag), getRequestAttribute(request,
	    "language"), getOptionalRequestAttribute(request, "address" + httpParameterPostTag), getOptionalRequestAttribute(request,
	    "telephone" + httpParameterPostTag), email, true, getRequestAttribute(request, "country" + httpParameterPostTag),
	    Integer.parseInt(getRequestAttribute(request, "yearofbirth" + httpParameterPostTag)), getOptionalRequestAttribute(
	        request, "city" + httpParameterPostTag));
  }

  class ExceptionData
  {
	long timestamp;
	Exception exception;
	HashMap<String, String> parameters;

	ExceptionData(final long timestamp, final Exception exception, final HttpServletRequest request)
	{
	  this.timestamp = timestamp;
	  this.exception = exception;
	  parameters = new HashMap<String, String>();

	  final Enumeration<String> e = request.getParameterNames();
	  while (e.hasMoreElements())
	  {
		final String param = e.nextElement();
		parameters.put(param, request.getParameter(param));
	  }
	}

	String toHTML()
	{
	  final StringBuffer buff = new StringBuffer();
	  buff.append("<b>Timestamp:</b> " + new Date(timestamp));

	  buff.append("<p>");
	  buff.append("<b>Exception:</b> " + exception.getClass().getName() + " " + exception.getMessage());
	  buff.append("<br>");
	  for (final StackTraceElement stackTrace : exception.getStackTrace())
	  {
		buff.append(stackTrace.toString());
		buff.append("<br>");
	  }

	  buff.append("<p>");
	  buff.append("<b>HTTP request:</b> ");
	  buff.append("<br>");

	  for (final String param : parameters.keySet())
	  {
		buff.append("<b>parameter:</b> " + param + " <b>value:</b> " + parameters.get(param));
		buff.append("<br>");
	  }
	  buff.append("<hr>");
	  buff.append("<p>");

	  return buff.toString();
	}
  }

  void addExceptionToHistory(final long timestamp, final Exception exception, final HttpServletRequest request)
  {
	if (exceptionHistory.size() == 10)
	{
	  exceptionHistory.remove(0);
	}

	exceptionHistory.add(new ExceptionData(timestamp, exception, request));
  }

  public String getSystemMessage()
  {
	return systemMessage;
  }

  public boolean isOnSiteUse()
  {
	return onSiteUse;
  }

  public static ServletDAO getServletDAO()
  {
	return servletDAO;
  }

  public void getModelForm(final HttpServletRequest request, final HttpServletResponse response, final String action,
	  final String submitLabel, final Integer modelID) throws Exception
  {
	final HttpSession session = request.getSession(true);

	session.setAttribute("action", action);
	session.setAttribute("submitLabel", submitLabel);
	if (modelID != null)
	{
	  session.setAttribute("modelID", modelID);
	}

	response.sendRedirect("jsp/modelForm.jsp");
  }

  private void getHTMLCodeForCategorySelect(final StringBuffer buff, final String selectedLabel, final String selectedValue,
	  final boolean mandatory, final ResourceBundle language, final HttpServletRequest request) throws Exception
  {
	final String show = (String) request.getSession().getAttribute("show");

	buff.append("<div id='categories'>");
	buff.append("<select name='categoryID'>");

	buff.append("<option value='" + selectedValue + "'>" + selectedLabel + "</option>");

	for (final CategoryGroup group : servletDAO.getCategoryGroups())
	{
	  if (show != null && !group.show.equals(show))
	  {
		// System.out.println(group.show + " " + show);
		continue;
	  }

	  buff.append("<optgroup label='" + group.show + " - " + group.name + "'>");

	  for (final Category category : servletDAO.getCategoryList(show))
	  {
		if (category.group.categoryGroupID != group.categoryGroupID)
		{
		  continue;
		}

		buff.append("<option value='" + category.categoryID + "'>");
		buff.append(category.categoryCode + " - " + category.categoryDescription);
		buff.append("</option>");

	  }

	  buff.append("</optgroup>");
	}
	buff.append("</select>");

	if (mandatory)
	{
	  buff.append("<font color='#FF0000' size='+3'>&#8226;</font> ");
	}
	buff.append("</div>");
  }

  public String getVersion()
  {
	return VERSION;
  }

  public static boolean isPreRegistrationAllowed()
  {
	return preRegistrationAllowed;
  }
}