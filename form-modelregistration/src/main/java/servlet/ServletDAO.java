package servlet;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import datatype.AgeGroup;
import datatype.AwardedModel;
import datatype.Category;
import datatype.CategoryGroup;
import datatype.Detailing;
import datatype.Model;
import datatype.ModelClass;
import datatype.User;

public class ServletDAO
{
  public static Logger logger = Logger.getLogger(ServletDAO.class);

  private final RegistrationServlet registrationServlet;
  private final Map<Integer, String> charEncodeMap;

  private final String dbURL;
  private final String dbUserName;
  private final String dbPassword;
  private Connection dBConnection;

  public final static int INVALID_USERID = -1;

  public enum SYSTEMPARAMETER
  {
	REGISTRATION, ONSITEUSE, SYSTEMMESSAGE
  };

  private Connection getDBConnection() throws SQLException
  {
	if (this.dBConnection == null || this.dBConnection.isClosed() || !this.dBConnection.isValid(0))
	{
	  logger.debug("ServletDAO.getDBConnection(): Acquiring new DB connnection");
	  this.dBConnection = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
	  this.dBConnection.setAutoCommit(true);
	}

	return this.dBConnection;
  }

  public ServletDAO(RegistrationServlet registrationServlet, String dbURL, String dbUserName, String dbPassword)
      throws SQLException
  {
	this.dbURL = dbURL;
	this.dbUserName = dbUserName;
	this.dbPassword = dbPassword;
	this.registrationServlet = registrationServlet;

	charEncodeMap = new HashMap<Integer, String>();
	charEncodeMap.put(192, "&Agrave;");
	charEncodeMap.put(193, "&Aacute;");
	charEncodeMap.put(194, "&Acirc;");
	charEncodeMap.put(195, "&Atilde;");
	charEncodeMap.put(196, "&Auml;");
	charEncodeMap.put(197, "&Aring;");
	charEncodeMap.put(198, "&AElig;");
	charEncodeMap.put(199, "&Ccedil;");
	charEncodeMap.put(200, "&Egrave;");
	charEncodeMap.put(201, "&Eacute;");
	charEncodeMap.put(202, "&Ecirc;");
	charEncodeMap.put(203, "&Euml;");
	charEncodeMap.put(204, "&Igrave;");
	charEncodeMap.put(205, "&Iacute;");
	charEncodeMap.put(206, "&Icirc;");
	charEncodeMap.put(207, "&Iuml;");
	charEncodeMap.put(208, "&ETH;");
	charEncodeMap.put(209, "&Ntilde;");
	charEncodeMap.put(210, "&Ograve;");
	charEncodeMap.put(211, "&Oacute;");
	charEncodeMap.put(212, "&Ocirc;");
	charEncodeMap.put(213, "&Otilde;");
	charEncodeMap.put(214, "&Ouml;");
	charEncodeMap.put(215, "&times;");
	charEncodeMap.put(216, "&Oslash;");
	charEncodeMap.put(217, "&Ugrave;");
	charEncodeMap.put(218, "&Uacute;");
	charEncodeMap.put(219, "&Ucirc;");
	charEncodeMap.put(220, "&Uuml;");
	charEncodeMap.put(221, "&Yacute;");
	charEncodeMap.put(222, "&THORN;");
	charEncodeMap.put(223, "&szlig;");
	charEncodeMap.put(224, "&agrave;");
	charEncodeMap.put(225, "&aacute;");
	charEncodeMap.put(226, "&acirc;");
	charEncodeMap.put(227, "&atilde;");
	charEncodeMap.put(228, "&auml;");
	charEncodeMap.put(229, "&aring;");
	charEncodeMap.put(230, "&aelig;");
	charEncodeMap.put(231, "&ccedil;");
	charEncodeMap.put(232, "&egrave;");
	charEncodeMap.put(233, "&eacute;");
	charEncodeMap.put(234, "&ecirc;");
	charEncodeMap.put(235, "&euml;");
	charEncodeMap.put(236, "&igrave;");
	charEncodeMap.put(237, "&iacute;");
	charEncodeMap.put(238, "&icirc;");
	charEncodeMap.put(239, "&iuml;");
	charEncodeMap.put(240, "&eth;");
	charEncodeMap.put(241, "&ntilde;");
	charEncodeMap.put(242, "&ograve;");
	charEncodeMap.put(243, "&oacute;");
	charEncodeMap.put(244, "&ocirc;");
	charEncodeMap.put(245, "&otilde;");
	charEncodeMap.put(246, "&ouml;");
	charEncodeMap.put(247, "&divide;");
	charEncodeMap.put(248, "&oslash;");
	charEncodeMap.put(249, "&ugrave;");
	charEncodeMap.put(250, "&uacute;");
	charEncodeMap.put(251, "&ucirc;");
	charEncodeMap.put(252, "&uuml;");
	charEncodeMap.put(253, "&yacute;");
	charEncodeMap.put(254, "&thorn;");
	charEncodeMap.put(255, "&yuml;");

  }

  private void encodeStringForDB(final PreparedStatement queryStatement, final int column, final String value) throws SQLException
  {
	queryStatement.setString(column, encodeString(value));
  }

  String encodeString(String value)
  {
	if (value == null)
	{
	  return "";
	}

	value = value.replaceAll("\"", "'");

	//	value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
	final StringBuffer buff = new StringBuffer();

	char ch;
	for (int i = 0; i < value.length(); i++)
	{
	  ch = value.charAt(i);
	  if (!charEncodeMap.containsKey((int) ch))
	  {
		if (ch > 255)
		{
		  buff.append("&#" + (int) ch + ";");
		}
		else
		{
		  buff.append(ch);
		}

	  }
	  else
	  {
		buff.append(charEncodeMap.get((int) ch));
	  }
	}

	return buff.toString();
  }

  public static String decodeStringFromDB(final ResultSet rs, final String column) throws SQLException
  {
	final String value = rs.getString(column);

	if (value == null)
	{
	  return "";
	}
	else
	{
	  return rs.getString(column);
	}

  }

  public void registerNewUser(final User user) throws Exception
  {
	registerNewUser(user, false);
  }

  public void registerNewUser(final User user, final boolean deleteUserFromDB) throws SQLException
  {
	PreparedStatement queryStatement = null;

	try
	{

	  if (deleteUserFromDB)
	  {
		deleteEntry("MAK_USERS", "user_id", user.userID);
	  }

	  queryStatement = getDBConnection().prepareStatement("insert into MAK_USERS "
	      + "(USER_ID, USER_PASSWORD, FIRST_NAME, LAST_NAME, USER_LANGUAGE, COUNTRY, ADDRESS, TELEPHONE, EMAIL, YEAR_OF_BIRTH, CITY, USER_ENABLED) values "
	      + "(?,?,?,?,?,?,?,?,?,?,?,1)");

	  queryStatement.setInt(1, user.userID);
	  encodeStringForDB(queryStatement, 2, user.password);
	  encodeStringForDB(queryStatement, 3, user.firstName);
	  encodeStringForDB(queryStatement, 4, user.lastName);
	  encodeStringForDB(queryStatement, 5, user.language);
	  encodeStringForDB(queryStatement, 6, user.country);
	  encodeStringForDB(queryStatement, 7, user.address);
	  encodeStringForDB(queryStatement, 8, user.telephone);
	  encodeStringForDB(queryStatement, 9, user.email);
	  encodeStringForDB(queryStatement, 10, String.valueOf(user.yearOfBirth));
	  encodeStringForDB(queryStatement, 11, user.city);

	  queryStatement.executeUpdate();
	}
	finally
	{
	  try
	  {
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("ServletDAO.registerNewUser(): ", ex);
	  }
	}
  }

  boolean userExists(final String email) throws SQLException, Exception
  {
	ResultSet rs = null;
	try
	{
	  final PreparedStatement queryStatement = getDBConnection()
	      .prepareStatement("select count(*) from MAK_USERS where EMAIL = ?");
	  encodeStringForDB(queryStatement, 1, email);
	  rs = queryStatement.executeQuery();
	  rs.next();
	  return rs.getInt(1) != 0;
	}
	finally
	{
	  try
	  {
		if (rs != null)
		{
		  rs.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("ServletDAO.userExists(): ", ex);
	  }

	}

  }

  public void saveCategory(final Category category) throws SQLException, Exception, IOError
  {
	PreparedStatement queryStatement = null;
	final ResultSet rs = null;

	try
	{
	  queryStatement = getDBConnection().prepareStatement("insert into MAK_CATEGORY"
	      + " (CATEGORY_ID, CATEGORY_CODE, CATEGORY_DESCRIPTION, CATEGORY_GROUP_ID, MASTER, MODEL_CLASS,ageGroup) values "
	      + "(?,?,?,?,?,?,?)");

	  logger.debug("ServletDAO.saveCategory(): categoryID: " + category.categoryID);

	  queryStatement.setInt(1, category.categoryID);
	  encodeStringForDB(queryStatement, 2, category.categoryCode);
	  encodeStringForDB(queryStatement, 3, category.categoryDescription);
	  queryStatement.setInt(4, category.group.categoryGroupID);
	  queryStatement.setInt(5, category.isMaster() ? 1 : 0);
	  queryStatement.setString(6, category.getModelClass().name());
	  queryStatement.setString(7, category.getAgeGroup().name());

	  queryStatement.executeUpdate();

	}
	finally
	{
	  try
	  {
		if (rs != null)
		{
		  rs.close();
		}
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("!!! ServletDAO.saveCategory(): ", ex);
	  }
	}
  }

  public List<Category> getCategoryList(final String show) throws Exception
  {
	return getCategoryList(0, show);
  }

  public List<AwardedModel> getAwardedModels() throws Exception
  {
	PreparedStatement queryStatement = null;
	ResultSet rs = null;

	try
	{
	  queryStatement = getDBConnection().prepareStatement("SELECT * FROM MAK_AWARDEDMODELS");

	  rs = queryStatement.executeQuery();

	  final List<AwardedModel> returned = new LinkedList<AwardedModel>();
	  while (rs.next())
	  {
		returned.add(new AwardedModel(decodeStringFromDB(rs, "AWARD"), getModel(rs.getInt("MODEL_ID"))));
	  }

	  return returned;
	}
	finally
	{
	  try
	  {
		if (rs != null)
		{
		  rs.close();
		}
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("!!! ServletDAO.getAwardedModels(): ", ex);
	  }
	}
  }

  public List<Category> getCategoryList(final int CATEGORY_group_ID, final String show) throws Exception
  {
	PreparedStatement queryStatement = null;
	ResultSet rs = null;

	try
	{
	  queryStatement = getDBConnection().prepareStatement("SELECT * FROM MAK_CATEGORY"
	      + (CATEGORY_group_ID == 0 ? "" : " WHERE CATEGORY_group_ID = " + CATEGORY_group_ID) + " order by CATEGORY_ID");

	  rs = queryStatement.executeQuery();

	  final List<Category> returned = new LinkedList<Category>();
	  final List<CategoryGroup> groups = getCategoryGroups();
	  while (rs.next())
	  {
		final Category category = new Category(rs.getInt("CATEGORY_ID"), decodeStringFromDB(rs, "CATEGORY_CODE"),
		    decodeStringFromDB(rs, "CATEGORY_DESCRIPTION"), getCategoryGroup(rs.getInt("CATEGORY_GROUP_ID"), groups),
		    rs.getInt("MASTER") == 1, ModelClass.valueOf(rs.getString("MODEL_CLASS")),
		    AgeGroup.valueOf(rs.getString("ageGroup")));

		if (show == null || category.group.show.equals(show))
		{
		  returned.add(category);
		  // else
		  // System.out.println(category.group.show + " " + show);
		}
	  }

	  return returned;
	}
	finally
	{
	  try
	  {
		if (rs != null)
		{
		  rs.close();
		}
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("!!! ServletDAO.getCategoryList(): ", ex);
	  }
	}
  }

  public CategoryGroup getCategoryGroup(final int categoryGroupID, final List<CategoryGroup> groups)
      throws SQLException, Exception
  {
	for (final CategoryGroup group : groups)
	{
	  if (group.categoryGroupID == categoryGroupID)
	  {
		return group;
	  }
	}

	throw new Exception("Unknown categoryGroupID: " + categoryGroupID);

  }

  public List<CategoryGroup> getCategoryGroups() throws SQLException, Exception
  {
	final List<CategoryGroup> returned = new LinkedList<CategoryGroup>();

	PreparedStatement queryStatement = null;
	ResultSet rs = null;

	try
	{
	  queryStatement = getDBConnection().prepareStatement("SELECT * FROM MAK_CATEGORY_GROUP");

	  rs = queryStatement.executeQuery();

	  while (rs.next())
	  {
		returned.add(new CategoryGroup(rs.getInt("CATEGORY_group_ID"), decodeStringFromDB(rs, "MODEL_SHOW"),
		    decodeStringFromDB(rs, "CATEGORY_GROUP")));
	  }

	  return returned;
	}
	finally
	{
	  try
	  {
		if (rs != null)
		{
		  rs.close();
		}
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("!!! ServletDAO.getCategoryGroups(): ", ex);
	  }
	}
  }

  public List<String> getShows() throws SQLException, Exception
  {
	final List<String> returned = new LinkedList<String>();

	PreparedStatement queryStatement = null;
	ResultSet rs = null;

	try
	{
	  queryStatement = getDBConnection().prepareStatement("SELECT distinct MODEL_SHOW FROM MAK_CATEGORY_GROUP");

	  rs = queryStatement.executeQuery();

	  while (rs.next())
	  {
		returned.add(decodeStringFromDB(rs, "MODEL_SHOW"));
	  }

	  return returned;
	}
	finally
	{
	  try
	  {
		if (rs != null)
		{
		  rs.close();
		}
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("!!! getShows(): ", ex);
	  }
	}
  }

  public List<User> getUsers() throws SQLException
  {
	PreparedStatement queryStatement = null;
	ResultSet rs = null;

	try
	{
	  queryStatement = getDBConnection()
	      .prepareStatement("SELECT * FROM MAK_USERS where USER_enabled=1 order by LAST_NAME, FIRST_NAME");

	  rs = queryStatement.executeQuery();

	  final List<User> returned = new LinkedList<User>();

	  while (rs.next())
	  {
		returned.add(getUser(rs));
	  }
	  return returned;
	}
	finally
	{
	  try
	  {
		if (rs != null)
		{
		  rs.close();
		}
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("!!! ServletDAO.getUserList(): ", ex);
	  }
	}
  }

  public void saveAwardedModel(final AwardedModel model) throws SQLException, Exception, IOException
  {
	PreparedStatement queryStatement = null;
	final ResultSet rs = null;

	logger.trace("ServletDAO.saveAwardedModel(): " + model);

	try
	{
	  queryStatement = getDBConnection().prepareStatement("insert into MAK_AWARDEDMODELS" + " (MODEL_ID, AWARD) values (?,?)");

	  queryStatement.setInt(1, model.model.modelID);

	  queryStatement.setString(2, model.award);

	  queryStatement.executeUpdate();

	}
	finally
	{
	  try
	  {
		if (rs != null)
		{
		  rs.close();
		}
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("!!! ServletDAO.saveAwardedModel(): ", ex);
	  }
	}
  }

  public void saveModel(final Model model) throws SQLException, Exception, IOException
  {
	PreparedStatement queryStatement = null;
	final ResultSet rs = null;

	logger.trace("ServletDAO.saveModel(): " + model);

	try
	{
	  queryStatement = getDBConnection().prepareStatement(
	      "insert into MAK_MODEL" + " (MODEL_ID, USER_ID, CATEGORY_ID, MODEL_SCALE, MODEL_NAME, PRODUCER, COMMENTS, "
	          + "IDENTIFICATION, MARKINGS, GLUEDTOBASE, "
	          + "SCRATCH_EXTERNALSURFACE, SCRATCH_COCKPIT, SCRATCH_ENGINE, SCRATCH_UNDERCARRIAGE, SCRATCH_GEARBAY, SCRATCH_ARMAMENT, SCRATCH_CONVERSION, "
	          + "PHOTOETCHED_EXTERNALSURFACE, PHOTOETCHED_COCKPIT, PHOTOETCHED_ENGINE, PHOTOETCHED_UNDERCARRIAGE, PHOTOETCHED_GEARBAY, PHOTOETCHED_ARMAMENT, PHOTOETCHED_CONVERSION, "
	          + "RESIN_EXTERNALSURFACE, RESIN_COCKPIT, RESIN_ENGINE, RESIN_UNDERCARRIAGE, RESIN_GEARBAY, RESIN_ARMAMENT, RESIN_CONVERSION, "
	          + "DOCUMENTATION_EXTERNALSURFACE, DOCUMENTATION_COCKPIT, DOCUMENTATION_ENGINE, DOCUMENTATION_UNDERCARRIAGE, DOCUMENTATION_GEARBAY, DOCUMENTATION_ARMAMENT, DOCUMENTATION_CONVERSION)"
	          + " values " + "(?,?,?,?,?,?,?,?,?,?" + ",?,?,?,?,?,?,?" + ",?,?,?,?,?,?,?" + ",?,?,?,?,?,?,?" + ",?,?,?,?,?,?,?"
	          + ")");

	  queryStatement.setInt(1, model.modelID);

	  queryStatement.setInt(2, model.userID);
	  queryStatement.setInt(3, model.categoryID);
	  encodeStringForDB(queryStatement, 4, model.scale);
	  encodeStringForDB(queryStatement, 5, model.name);
	  encodeStringForDB(queryStatement, 6, model.producer);
	  encodeStringForDB(queryStatement, 7, model.comment);

	  encodeStringForDB(queryStatement, 8, model.identification);
	  encodeStringForDB(queryStatement, 9, model.markings);
	  queryStatement.setInt(10, model.gluedToBase ? 1 : 0);

	  int statementCounter = 11;
	  for (int i = 0; i < Detailing.DETAILING_GROUPS.length; i++)
	  {
		for (int j = 0; j < model.detailing[i].criterias.size(); j++)
		{
		  queryStatement.setInt(statementCounter++, model.detailing[i].criterias.get(j) ? 1 : 0);
		}
	  }

	  queryStatement.executeUpdate();

	}
	finally
	{
	  try
	  {
		if (rs != null)
		{
		  rs.close();
		}
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("!!! ServletDAO.saveModel(): ", ex);
	  }
	}
  }

  public synchronized int getNextID(final String table, final String column) throws SQLException
  {
	PreparedStatement queryStatement = null;
	ResultSet rs = null;

	try
	{
	  queryStatement = getDBConnection().prepareStatement("SELECT max(" + column + ") FROM MAK_" + table);

	  rs = queryStatement.executeQuery();
	  rs.next();

	  final int maxID = rs.getInt(1);
	  logger.trace("ServletDAO.getNextID() table: " + table + ", maxID in MAK_" + table + " : " + maxID);

	  if (maxID != 0)
	  {
		return maxID + 1;
	  }
	  else
	  {
		return 1;
	  }
	}
	finally
	{
	  try
	  {
		if (rs != null)
		{
		  rs.close();
		}
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("!!! ServletDAO.getNextID(): table: " + table, ex);
	  }
	}
  }

  public void saveModelClass(int userID, ModelClass modelClass) throws Exception
  {
	final User user = getUser(userID);
	user.getModelClass().add(modelClass);

	String modelClasses = "";
	for (final ModelClass currentModelClass : user.getModelClass())
	{
	  modelClasses += currentModelClass.name() + ",";
	}
	PreparedStatement queryStatement = null;

	try
	{
	  queryStatement = getDBConnection().prepareStatement("update MAK_USERS set MODEL_CLASS=? where USER_ID=?");

	  queryStatement.setString(1, modelClasses);
	  queryStatement.setInt(2, userID);

	  queryStatement.executeUpdate();
	}
	finally
	{
	  try
	  {
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("!!! ServletDAO.saveModelClass(): userId: " + userID + " modelClass: " + modelClass, ex);
	  }
	}
  }

  public User getUser(final ResultSet rs) throws SQLException
  {
	final List<ModelClass> modelClasses = new LinkedList<ModelClass>();
	if (rs.getString("MODEL_CLASS") != null)
	{
	  final StringTokenizer mc = new StringTokenizer(rs.getString("MODEL_CLASS"), ",");

	  while (mc.hasMoreTokens())
	  {
		modelClasses.add(ModelClass.valueOf(mc.nextToken()));
	  }
	}

	final User user = new User(rs.getInt("USER_ID"), decodeStringFromDB(rs, "USER_PASSWORD"),
	    decodeStringFromDB(rs, "FIRST_NAME"), decodeStringFromDB(rs, "LAST_NAME"), decodeStringFromDB(rs, "USER_LANGUAGE"),
	    decodeStringFromDB(rs, "ADDRESS"), decodeStringFromDB(rs, "TELEPHONE"), decodeStringFromDB(rs, "EMAIL"),
	    rs.getBoolean("USER_ENABLED"), decodeStringFromDB(rs, "COUNTRY"), rs.getInt("YEAR_OF_BIRTH"),
	    decodeStringFromDB(rs, "CITY"));

	if (!modelClasses.isEmpty())
	{
	  user.setModelClass(modelClasses);
	}

	return user;
  }

  public User getUser(String email) throws Exception
  {
	PreparedStatement queryStatement = null;
	ResultSet rs = null;

	try
	{
	  // for SQL inject
	  email = email.replace(';', ' ').replace('(', ' ').replace(')', ' ').replace('\'', ' ').replace(';', ' ');

	  queryStatement = getDBConnection().prepareStatement("SELECT * FROM MAK_USERS where EMAIL = ?");

	  encodeStringForDB(queryStatement, 1, email);

	  rs = queryStatement.executeQuery();

	  if (rs.next())
	  {
		return getUser(rs);
	  }

	  throw new Exception("email not found in DB. email: " + email);
	}
	finally
	{
	  try
	  {
		if (rs != null)
		{
		  rs.close();
		}
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("!!! ServletDAO.getUser(): ", ex);
	  }
	}
  }

  public User getUser(final int userID) throws Exception
  {
	PreparedStatement queryStatement = null;
	ResultSet rs = null;

	try
	{
	  queryStatement = getDBConnection().prepareStatement("SELECT * FROM MAK_USERS where USER_ID = ?");

	  queryStatement.setInt(1, userID);

	  rs = queryStatement.executeQuery();

	  if (rs.next())
	  {
		return getUser(rs);
	  }

	  throw new Exception("userID not found in DB. userID: " + userID);
	}
	finally
	{
	  try
	  {
		if (rs != null)
		{
		  rs.close();
		}
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("!!! ServletDAO.getUser(): ", ex);
	  }
	}
  }

  public boolean getYesNoSystemParameter(final SYSTEMPARAMETER parameter) throws Exception
  {
	final String value = getSystemParameter(parameter);
	if ("-".equals(value))
	{
	  return false;
	}
	else
	{
	  return Integer.parseInt(value) == 1;
	}
  }

  public String getSystemParameter(final SYSTEMPARAMETER parameter) throws Exception
  {
	PreparedStatement queryStatement = null;
	ResultSet rs = null;

	try
	{
	  queryStatement = getDBConnection().prepareStatement("SELECT * FROM MAK_SYSTEM where PARAM_NAME = '" + parameter + "'");

	  rs = queryStatement.executeQuery();

	  if (rs.next())
	  {
		return rs.getString("PARAM_VALUE");
	  }
	  else
	  {
		return "-";
	  }
	}
	finally
	{
	  try
	  {
		if (rs != null)
		{
		  rs.close();
		}
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("!!! ServletDAO.registrationAllowed(): ", ex);
	  }
	}
  }

  public void setSystemParameter(final String parameterName, final String parameterValue) throws Exception
  {
	PreparedStatement queryStatement = null;

	try
	{
	  queryStatement = getDBConnection().prepareStatement("update MAK_SYSTEM set PARAM_VALUE=? where PARAM_NAME=?");

	  queryStatement.setString(1, parameterValue);
	  queryStatement.setString(2, parameterName);

	  if (queryStatement.executeUpdate() == 0)
	  {
		queryStatement.close();

		queryStatement = getDBConnection().prepareStatement("insert into MAK_SYSTEM (PARAM_VALUE, PARAM_NAME) values (?,?)");
		queryStatement.setString(1, parameterValue);
		queryStatement.setString(2, parameterName);

		queryStatement.executeUpdate();
	  }
	}
	finally
	{
	  try
	  {
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal(
		    "!!! ServletDAO.setSystemParameter(): parameterName: " + parameterName + " parameterValue: " + parameterValue, ex);
	  }
	}
  }

  String createWhereStatement(final HttpServletRequest request, String where, final String sQLfield, final String httpParameter,
      final boolean stringParameter)
  {

	try
	{
	  if (ServletUtil.getRequestAttribute(request, httpParameter).length() != 0)
	  {
		if (where.length() != 0)
		{
		  where += " AND ";
		}

		if (stringParameter)
		{
		  where += sQLfield + " like '%" + ServletUtil.getRequestAttribute(request, httpParameter) + "%'";
		}
		else
		{
		  where += sQLfield + " = '" + ServletUtil.getRequestAttribute(request, httpParameter) + "'";
		}
	  }
	}
	catch (final Exception e)
	{
	}

	return where;
  }

  public List<Model> selectModels(final HttpServletRequest request) throws Exception
  {
	String where = "";

	where = createWhereStatement(request, where, "CATEGORY_ID", "categoryID", false);
	where = createWhereStatement(request, where, "USER_ID", "userID", false);
	where = createWhereStatement(request, where, "MODEL_ID", "modelID", false);
	where = createWhereStatement(request, where, "MODEL_name", "modelname", true);
	where = createWhereStatement(request, where, "markings", "markings", true);
	where = createWhereStatement(request, where, "producer", "modelproducer", true);

	return getModels(where);
  }

  public List<Model> getModels(final int userID) throws Exception
  {
	return getModels(userID == INVALID_USERID ? "" : "USER_ID = " + userID);
  }

  public List<Model> getModelsInCategory(final int categoryID) throws Exception
  {
	return getModels("CATEGORY_ID = " + categoryID);
  }

  public List<Model> getModels(final String where) throws Exception
  {
	PreparedStatement queryStatement = null;
	ResultSet rs = null;

	try
	{
	  logger.trace("ServletDAO.getModels(): where: " + where);

	  queryStatement = getDBConnection()
	      .prepareStatement("SELECT * FROM MAK_MODEL" + (where.length() == 0 ? "" : " where " + where) + " order by MODEL_ID");

	  rs = queryStatement.executeQuery();

	  final List<Model> returned = new LinkedList<Model>();

	  while (rs.next())
	  {
		returned.add(new Model(rs.getInt("model_ID"), rs.getInt("user_ID"), rs.getInt("category_ID"),
		    decodeStringFromDB(rs, "MODEL_scale"), decodeStringFromDB(rs, "MODEL_name"), decodeStringFromDB(rs, "producer"),
		    decodeStringFromDB(rs, "comments"), decodeStringFromDB(rs, "identification"), decodeStringFromDB(rs, "markings"),
		    rs.getInt("gluedToBase") == 1, getDetailing(rs)));
	  }
	  return returned;
	}
	finally
	{
	  try
	  {
		if (rs != null)
		{
		  rs.close();
		}
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("!!! ServletDAO.getModels(): ", ex);
	  }
	}
  }

  public Model getModel(final int modelID) throws Exception
  {
	PreparedStatement queryStatement = null;
	ResultSet rs = null;

	try
	{
	  logger.trace("ServletDAO.getModel(): modelID: " + modelID);

	  queryStatement = getDBConnection().prepareStatement("SELECT * FROM MAK_MODEL where MODEL_ID=" + modelID);

	  rs = queryStatement.executeQuery();
	  if (rs.next())
	  {
		return new Model(rs.getInt("model_ID"), rs.getInt("user_ID"), rs.getInt("category_ID"),
		    decodeStringFromDB(rs, "MODEL_scale"), decodeStringFromDB(rs, "MODEL_name"), decodeStringFromDB(rs, "producer"),
		    decodeStringFromDB(rs, "comments"), decodeStringFromDB(rs, "identification"), decodeStringFromDB(rs, "markings"),
		    rs.getInt("gluedToBase") == 1, getDetailing(rs));
	  }
	  else
	  {
		throw new Exception("No model found with modelID: " + modelID);
	  }
	}
	finally
	{
	  try
	  {
		if (rs != null)
		{
		  rs.close();
		}
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("!!! ServletDAO.getModel(): ", ex);
	  }
	}
  }

  private Detailing[] getDetailing(final ResultSet rs) throws SQLException
  {
	final Detailing[] detailing = new Detailing[Detailing.DETAILING_GROUPS.length];

	for (int i = 0; i < detailing.length; i++)
	{
	  final String group = Detailing.DETAILING_GROUPS[i];

	  final List<Boolean> criterias = new LinkedList<Boolean>();
	  criterias.add(rs.getInt(group + "_externalSurface") == 1);

	  criterias.add(rs.getInt(group + "_cockpit") == 1);
	  criterias.add(rs.getInt(group + "_engine") == 1);
	  criterias.add(rs.getInt(group + "_undercarriage") == 1);
	  criterias.add(rs.getInt(group + "_gearBay") == 1);
	  criterias.add(rs.getInt(group + "_armament") == 1);
	  criterias.add(rs.getInt(group + "_conversion") == 1);
	  detailing[i] = new Detailing(group, criterias);
	}

	return detailing;
  }

  public Category getCategory(final int categoryID) throws Exception
  {
	for (final Category category : getCategoryList(null))
	{
	  if (category.categoryID == categoryID)
	  {
		return category;
	  }
	}

	throw new Exception("Unknown categoryID: " + categoryID);
  }

  public Category getCategory(final String categoryCode) throws Exception
  {
	for (final Category category : getCategoryList(null))
	{
	  if (category.categoryCode.equals(categoryCode))
	  {
		return category;
	  }
	}

	throw new Exception("Unknown categoryCode: " + categoryCode);
  }

  public void deleteEntries(final String table) throws SQLException
  {
	deleteEntry(table, null, 0);
  }

  public void deleteEntry(final String table, final String idField, final int id) throws SQLException
  {
	PreparedStatement queryStatement = null;
	final ResultSet rs = null;

	try
	{
	  queryStatement = getDBConnection()
	      .prepareStatement("delete from " + table + (idField == null ? "" : " where " + idField + " = ?"));

	  if (idField != null)
	  {
		queryStatement.setInt(1, id);
	  }

	  queryStatement.executeUpdate();

	}
	finally
	{
	  try
	  {
		if (rs != null)
		{
		  rs.close();
		}
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("!!! ServletDAO.deleteEntry(): ", ex);
	  }
	}
  }

  public List<String[]> getStatistics(final String show) throws Exception
  {
	final List<String[]> returned = new LinkedList<String[]>();

	final PreparedStatement queryStatement = null;
	final ResultSet rs = null;

	try
	{
	  // aktiv kategoriak
	  final List<Category> categories = getCategoryList(show);

	  returned.add(new String[] { "<b>Verseny</b>", show });
	  returned.add(new String[] { "Kateg&oacute;ri&aacute;k sz&aacute;ma: ", String.valueOf(categories.size()) });

	  returned.add(new String[] { "|", "" });
	  final List<User> users = getUsers();
	  final Map<String, HashSet<Integer>> activeModelers = new HashMap<String, HashSet<Integer>>();

	  int allModels = 0;

	  returned.add(new String[] { "Makettek sz&aacute;ma kateg&oacute;ri&aacute;ban: ", "" });
	  returned.add(new String[] { "|", "" });

	  for (final Category category : categories)
	  {
		if (!category.group.show.equals(show))
		{
		  continue;
		}

		final List<Model> models = getModelsInCategory(category.categoryID);

		returned.add(new String[] { "<b>" + category.categoryCode + " - " + category.categoryDescription + "</b>",
		    String.valueOf(models.size()) });

		// benevezett makettek szama
		allModels += models.size();

		for (final Model model : models)
		{
		  // jelenleg nevezok
		  for (final User user : users)
		  {
			if (user.userID == model.userID)
			{
			  HashSet<Integer> userIDs = activeModelers.get(user.country);
			  if (userIDs == null)
			  {
				userIDs = new HashSet<Integer>();
				activeModelers.put(user.country, userIDs);
			  }
			  userIDs.add(user.userID);
			}
		  }

		}
	  }

	  returned.add(new String[] { "|", "" });

	  // benevezett makettek szama
	  returned.add(new String[] { "Benevezett makettek sz&aacute;ma: ", String.valueOf(allModels) });
	  returned.add(new String[] { "Felt&ouml;lt&ouml;tt k&eacute;pek sz&aacute;ma: ", simpleQuery("count(*)", "MAK_PICTURES") });

	  returned.add(new String[] { "|", "" });

	  // ossz regisztralt felhasznalo
	  int modelers = 0;
	  for (final String country : activeModelers.keySet())
	  {
		modelers += activeModelers.get(country).size();
	  }

	  returned.add(new String[] { "Jelenleg nevez&otilde;k sz&aacute;ma : ", String.valueOf(modelers) });

	  // jelenleg nevezok
	  for (final String country : activeModelers.keySet())
	  {
		returned.add(new String[] { "Jelenleg nevez&otilde;k ebb&otilde;l az orsz&aacute;gb&oacute;l: <b>" + country + "</b>",
		    String.valueOf(activeModelers.get(country).size()) });
	  }

	  // queryStatement = getDBConnection()
	  // .prepareStatement("SELECT count(*) FROM MAK_USERS");
	  // rs = queryStatement.executeQuery();
	  // rs.next();
	  // returned.add(new String[]
	  // { "&Ouml;sszes regisztr&aacute;lt felhaszn&aacute;l&oacute;k: ",
	  // String.valueOf(rs.getInt(1)) });
	  // rs.close();
	  // queryStatement.close();
	  //
	  // queryStatement = getDBConnection()
	  // .prepareStatement("select COUNTRY, count(*)  from MAK_USERS group by COUNTRY order by COUNTRY");
	  // rs = queryStatement.executeQuery();
	  // while (rs.next())
	  // returned.add(new String[]
	  // {
	  // "<b>Regisztr&aacute;lt felhaszn&aacute;l&oacute;k ebb&otilde;l az orsz&aacute;gb&oacute;l:</b> "
	  // + decodeStringFromDB(rs, "COUNTRY"),
	  // String.valueOf(rs.getInt(2)) });
	  // rs.close();
	  // queryStatement.close();

	}
	finally
	{
	  try
	  {
		if (rs != null)
		{
		  rs.close();
		}
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("!!! ServletDAO.getStatistics(): ", ex);
	  }
	}

	return returned;
  }

  public void deleteModel(final HttpServletRequest request) throws Exception
  {
	deleteModel(Integer.valueOf(ServletUtil.getRequestAttribute(request, "modelID")));
  }

  public void deleteModel(final int id) throws Exception
  {
	deleteEntry("MAK_MODEL", "model_id", id);
	deleteEntry("MAK_PICTURES", "model_id", id);
  }

  public void deleteAwardedModel(final HttpServletRequest request) throws Exception
  {
	deleteAwardedModel(Integer.valueOf(ServletUtil.getRequestAttribute(request, "modelID")));
  }

  public void deleteAwardedModel(final int id) throws Exception
  {
	deleteEntry("MAK_AWARDEDMODELS", "model_id", id);
  }

  public void deleteCategory(final HttpServletRequest request) throws Exception
  {
	deleteCategory(Integer.valueOf(ServletUtil.getRequestAttribute(request, "categoryID")));
  }

  public void deleteCategory(final int id) throws Exception
  {
	for (final Model model : getModelsInCategory(id))
	{
	  deleteModel(model.modelID);
	}

	deleteEntry("MAK_CATEGORY", "category_id", id);
  }

  public void deleteCategoryGroup(final HttpServletRequest request) throws Exception
  {
	deleteCategoryGroup(Integer.valueOf(ServletUtil.getRequestAttribute(request, "categoryGroupID")));
  }

  public void deleteCategoryGroup(final int id) throws Exception
  {
	for (final Category category : getCategoryList(id, null // show
	))
	{
	  deleteCategory(category.categoryID);
	}

	deleteEntry("MAK_CATEGORY_GROUP", "CATEGORY_GROUP_ID", id);
  }

  public void deleteUser(final int userID) throws SQLException
  {
	deleteEntry("MAK_MODEL", "user_id", userID);

	deleteEntry("MAK_USERS", "user_id", userID);
  }

  public void modifyUser(final User newUser, final User oldUser) throws SQLException
  {
	registerNewUser(newUser, true);
  }

  public void newUserIDs() throws Exception
  {
	final List<User> users = getUsers();

	for (final User user : users)
	{
	  final int newUserID = getNextID("USERS", "USER_ID");

	  changeUserIDForUser(user.email, user.userID, newUserID);
	  changeUserIDForModel(user.userID, newUserID);
	}
  }

  public void newUserIDsFromOne() throws Exception
  {
	final List<User> users = getUsers();

	int newUserID = 0;
	for (final User user : users)
	{
	  newUserID++;

	  changeUserIDForUser(user.email, user.userID, newUserID);
	  changeUserIDForModel(user.userID, newUserID);
	}
  }

  public void changeUserIDForUser(final String email, final int oldUserID, final int newUserID) throws Exception
  {
	logger.trace("ServletDAO.changeUserIDForUser(): email: " + email + " oldUserID: " + oldUserID + " newUserID: " + newUserID);

	PreparedStatement queryStatement = null;

	try
	{
	  queryStatement = getDBConnection().prepareStatement("update MAK_USERS set USER_ID=? where EMAIL=? AND USER_ID=?");

	  queryStatement.setInt(1, newUserID);
	  queryStatement.setString(2, email);
	  queryStatement.setInt(3, oldUserID);

	  queryStatement.executeUpdate();
	}
	finally
	{
	  try
	  {
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal(
		    "!!! ServletDAO.changeUserIDForUser(): email: " + email + " oldUserID: " + oldUserID + " newUserID: " + newUserID,
		    ex);
	  }
	}
  }

  public void changeUserIDForModel(final int oldUserID, final int newUserID) throws Exception
  {
	logger.trace("ServletDAO.changeUserIDForModel(): oldUserID: " + oldUserID + " newUserID: " + newUserID);

	PreparedStatement queryStatement = null;

	try
	{
	  queryStatement = getDBConnection().prepareStatement("update MAK_MODEL set USER_ID=? where USER_ID=?");

	  queryStatement.setInt(1, newUserID);
	  queryStatement.setInt(2, oldUserID);

	  queryStatement.executeUpdate();
	}
	finally
	{
	  try
	  {
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("!!! ServletDAO.changeUserIDForModel(): oldUserID: " + oldUserID + " newUserID: " + newUserID, ex);
	  }
	}
  }

  public void saveCategoryGroup(final CategoryGroup categoryGroup) throws Exception
  {
	PreparedStatement queryStatement = null;
	final ResultSet rs = null;

	try
	{
	  queryStatement = getDBConnection()
	      .prepareStatement("insert into MAK_CATEGORY_GROUP (CATEGORY_GROUP_ID, MODEL_SHOW, CATEGORY_GROUP) values " + "(?,?,?)");

	  logger.debug("ServletDAO.saveCategoryGroup(): categoryID: " + categoryGroup.categoryGroupID);

	  queryStatement.setInt(1, categoryGroup.categoryGroupID);
	  encodeStringForDB(queryStatement, 2, categoryGroup.show);
	  encodeStringForDB(queryStatement, 3, categoryGroup.name);

	  queryStatement.executeUpdate();

	}
	finally
	{
	  try
	  {
		if (rs != null)
		{
		  rs.close();
		}
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("!!! ServletDAO.saveCategoryGroup(): ", ex);
	  }
	}
  }

  public StringBuffer execute(final String sql) throws SQLException
  {
	PreparedStatement queryStatement = null;
	ResultSet rs = null;

	try
	{
	  logger.debug("ServletDAO.execute(): " + sql);
	  queryStatement = getDBConnection().prepareStatement(sql);
	  if (sql.toLowerCase().indexOf("select") > -1)
	  {
		rs = queryStatement.executeQuery(sql);

		final StringBuffer buff = new StringBuffer();

		final ResultSetMetaData rsm = rs.getMetaData();
		while (rs.next())
		{
		  for (int i = 1; i <= rsm.getColumnCount(); i++)
		  {
			final String columnName = rsm.getColumnName(i);
			final String columnValue = rs.getString(columnName);

			buff.append(columnName + ": " + columnValue);
			buff.append("<br>");
		  }

		  buff.append("<p>");
		  buff.append("-------------------------------------------");
		  buff.append("<p>");
		}

		return buff;
	  }
	  else
	  {
		queryStatement.executeUpdate();
		return null;
	  }
	}
	finally
	{
	  try
	  {
		if (rs != null)
		{
		  rs.close();
		}

		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("!!! ServletDAO.execute(): ", ex);
	  }
	}
  }

  public List<User> getSimilarLastNames(final String lastname) throws Exception
  {
	PreparedStatement queryStatement = null;
	ResultSet rs = null;

	final List<User> returned = new LinkedList<User>();
	try
	{
	  queryStatement = getDBConnection().prepareStatement("SELECT * FROM MAK_USERS where LAST_NAME like '%" + lastname + "%'");

	  rs = queryStatement.executeQuery();

	  while (rs.next())
	  {
		returned.add(getUser(rs));
	  }
	}
	finally
	{
	  try
	  {
		if (rs != null)
		{
		  rs.close();
		}
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("!!! ServletDAO.getSimilarLastNames(): ", ex);
	  }
	}
	return returned;
  }

  public void saveImage(int modelID, InputStream stream) throws Exception, IOException, SQLException
  {
	PreparedStatement ps = null;
	try
	{
	  ps = getDBConnection().prepareStatement("delete from MAK_PICTURES where MODEL_ID=?");
	  ps.setInt(1, modelID);
	  ps.executeUpdate();
	  ps.close();

	  ps = getDBConnection().prepareStatement("INSERT INTO MAK_PICTURES (MODEL_ID, PHOTO) VALUES (?, ?)");
	  ps.setInt(1, modelID);
	  ps.setBinaryStream(2, stream);
	  ps.executeUpdate();
	}
	finally
	{
	  ps.close();
	}
  }

  public byte[] loadImage(int modelID) throws SQLException
  {
	final PreparedStatement ps = getDBConnection().prepareStatement("select PHOTO from MAK_PICTURES where MODEL_ID=?");
	ps.setInt(1, modelID);
	final ResultSet resultSet = ps.executeQuery();

	if (!resultSet.next())
	{
	  resultSet.close();
	  ps.close();

	  throw new IllegalStateException("Image not found. modelID: " + modelID);
	}

	final Blob blob = resultSet.getBlob(1);
	final byte[] image = blob.getBytes(1, (int) blob.length());

	resultSet.close();
	ps.close();

	return image;
  }

  public Map<Integer, byte[]> getPhotos() throws Exception
  {
	final Map<Integer, byte[]> photos = new HashMap<Integer, byte[]>();

	for (final Model model : getModels(INVALID_USERID))
	{
	  try
	  {
		final byte[] loadedImage = loadImage(model.modelID);
		logger.debug("ServletDAO.loadImage(): model.modelID: " + model.modelID + " loadedImage.length: " + loadedImage.length);
		photos.put(model.modelID, loadedImage);
	  }
	  catch (final Exception e)
	  {
	  }
	}

	return photos;
  }

  public String simpleQuery(final String field, String table) throws Exception
  {
	PreparedStatement queryStatement = null;
	ResultSet rs = null;

	try
	{
	  queryStatement = getDBConnection().prepareStatement("SELECT " + field + " FROM " + table);

	  rs = queryStatement.executeQuery();

	  if (rs.next())
	  {
		return rs.getString(1);
	  }

	  return "";
	}
	finally
	{
	  try
	  {
		if (rs != null)
		{
		  rs.close();
		}
		if (queryStatement != null)
		{
		  queryStatement.close();
		}
	  }
	  catch (final Exception ex)
	  {
		logger.fatal("!!! ServletDAO.simpleQuery(): ", ex);
	  }
	}
  }
}
