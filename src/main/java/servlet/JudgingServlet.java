package servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import datatype.JudgingCriteria;
import datatype.JudgingResult;
import datatype.JudgingScore;
import datatype.Record;

public final class JudgingServlet extends HttpServlet
{
  private static final String JUDGING_FILENAME = "judging";

  public enum RequestType
  {
	GetJudgingRules, GetJudgingForuma, SaveJudging, ListJudgings, DeleteJudgings, ListJudgingSummary
  };

  public enum SessionAttribute
  {
	JudgingRulesForCategory, Category, Judgings
  }

  public enum RequestParameter
  {
	Category, ModelID, ModellerID, Judge, Score, JudgingCriterias, Comment
  }

  private HibernateDAO dao;

  public JudgingServlet()
  {
	super();
  }

  @Override
  public void init(ServletConfig config) throws ServletException
  {
	try
	{
	  dao = new HibernateDAO(config.getServletContext().getResource("/WEB-INF/conf/hibernate.cfg.xml"));
	}
	catch (MalformedURLException e)
	{
	  throw new ServletException(e);
	}
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
	doPost(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
	try
	{
	  String pathInfo = request.getPathInfo();
	  if (pathInfo != null)
	  {
		pathInfo = pathInfo.substring(1);

		switch (RequestType.valueOf(pathInfo))
		{
		case GetJudgingRules:
		  showJudgingRules(request, response);
		  break;

		case GetJudgingForuma:
		  showJudgingForm(request, response);
		  break;

		case SaveJudging:
		  saveJudging(request, response);
		  break;
		case ListJudgings:
		  listJudgings(request, response);
		  break;
		case DeleteJudgings:
		  deleteJudgings(request, response);
		  break;
		case ListJudgingSummary:
		  listJudgingSummary(request, response);
		  break;

		default:
		  throw new IllegalArgumentException("Unhandled pathInfo: " + pathInfo);
		}
	  }
	  else
	  {
		redirectRequest(request, response, "/jsp/judging.jsp");
	  }
	}
	catch (Exception ex)
	{
	  ex.printStackTrace(response.getWriter());
	}
  }

  private void listJudgingSummary(HttpServletRequest request, HttpServletResponse response) throws Exception
  {

	Map<String, JudgingResult> scoresByCategory = new HashMap<String, JudgingResult>();

	List<Record> allScores = new LinkedList<Record>(dao.getAll(JudgingScore.class));

	Collections.sort(allScores, new Comparator<Record>()
		{
	  @Override
	  public int compare(Record o1, Record o2)
	  {
		JudgingScore result1 = (JudgingScore) o1;
		JudgingScore result2 = (JudgingScore) o2;

		return result1.getCategory().compareTo(result2.getCategory());
	  }
		});

	for (Record record : allScores)
	{
	  JudgingScore score = (JudgingScore) record;

	  JudgingResult judgingResult = scoresByCategory.get(score.getJudge() + score.getModelID());
	  int maxScore = getMaxScore(score.getCategory(), score.getCriteriaID());
	  if (judgingResult == null)
	  {
		judgingResult = new JudgingResult(score, maxScore);
		scoresByCategory.put(score.getJudge() + score.getModelID(), judgingResult);
	  }

	  AtomicInteger count = judgingResult.getScores().get(score.getScore());
	  if (count == null)
	  {
		count = new AtomicInteger(0);
		judgingResult.getScores().put(score.getScore(), count);
	  }

	  if (judgingResult.getMaxScore() < maxScore)
	  {
		judgingResult.setMaxScore(maxScore);
	  }

	  count.incrementAndGet();
	}

	setSessionAttribute(request, SessionAttribute.Judgings.name(), scoresByCategory.values());

	redirectRequest(request, response, "/jsp/listJudgingSummary.jsp");
  }

  private int getMaxScore(String category, int criteriaID) throws IOException
  {
	for (JudgingCriteria criteria : getCriteriaList(category))
	{
	  if (criteria.getId() == criteriaID)
	  {
		return criteria.getMaxScore();
	  }
	}

	throw new IllegalArgumentException(String.format("criteriaID: %1$d not found in category: %2$d", criteriaID, category));
  }

  private void setSessionAttribute(HttpServletRequest request, String name, Object value)
  {
	HttpSession session = request.getSession(true);
	session.setAttribute(name, value);
  }

  private void deleteJudgings(HttpServletRequest request, HttpServletResponse response) throws Exception
  {
	for (Record score : dao.getAll(JudgingScore.class))
	{
	  dao.delete(score.id, JudgingScore.class);
	}

	redirectRequest(request, response, "/" + getClass().getSimpleName());
  }

  private void listJudgings(HttpServletRequest request, HttpServletResponse response) throws Exception
  {
	List<Record> allScores = new LinkedList<Record>(dao.getAll(JudgingScore.class));

	Collections.sort(allScores, new Comparator<Record>()
		{
	  @Override
	  public int compare(Record o1, Record o2)
	  {
		JudgingScore result1 = (JudgingScore) o1;
		JudgingScore result2 = (JudgingScore) o2;

		int compareTo = result1.getCategory().compareTo(result2.getCategory());
		if (compareTo == 0)
		{
		  compareTo = result1.getJudge().compareTo(result2.getJudge());
		  if (compareTo == 0)
		  {
			compareTo = new Integer(result1.getModelID()).compareTo(new Integer(result2.getModelID()));
		  }
		}

		return compareTo;
	  }
		});

	setSessionAttribute(request, SessionAttribute.Judgings.name(), allScores);

	redirectRequest(request, response, "/jsp/listJudgings.jsp");
  }

  private void saveJudging(HttpServletRequest request, HttpServletResponse response) throws Exception
  {
	String category = ServletUtil.getRequestAttribute(request, RequestParameter.Category.name());
	String judge = ServletUtil.getRequestAttribute(request, RequestParameter.Judge.name());
	int modelID = Integer.parseInt(ServletUtil.getRequestAttribute(request, RequestParameter.ModelID.name()));
	int modellerID = Integer.parseInt(ServletUtil.getRequestAttribute(request, RequestParameter.ModellerID.name()));

	int judgingCriterias = Integer.parseInt(ServletUtil.getRequestAttribute(request, RequestParameter.JudgingCriterias.name()));
	String comment = ServletUtil.getOptionalRequestAttribute(request, RequestParameter.Comment.name());

	for (int i = 1; i <= judgingCriterias; i++)
	{
	  String optionalRequestAttribute = ServletUtil.getOptionalRequestAttribute(request, RequestParameter.Score.name() + i);
	  if ("-".equals(optionalRequestAttribute))
	  {
		continue;
	  }

	  int score = Integer.parseInt(optionalRequestAttribute);

	  dao.save(new JudgingScore(dao.getNextID(JudgingScore.class), category, judge, modelID, modellerID, i, score, comment));
	}

	redirectRequest(request, response, "/" + getClass().getSimpleName());
  }

  private void showJudgingForm(HttpServletRequest request, HttpServletResponse response) throws Exception
  {

	String category = ServletUtil.getRequestAttribute(request, RequestParameter.Category.name());

	setSessionAttribute(request, SessionAttribute.JudgingRulesForCategory.name(), getCriteriaList(category));
	setSessionAttribute(request, SessionAttribute.Category.name(), category);

	redirectRequest(request, response, "/jsp/showJudgingForm.jsp");
  }

  private List<JudgingCriteria> getCriteriaList(String category) throws IOException
  {
	Map<String, String> judgingRules = loadFile(JUDGING_FILENAME);

	Map<String, String> judgingRulesForCategory = loadFile(judgingRules.get(category));
	List<JudgingCriteria> criteriaList = new LinkedList<JudgingCriteria>();

	for (Entry<String, String> entry : judgingRulesForCategory.entrySet())
	{
	  String values = entry.getValue();
	  String[] splitValues = values.split(";");
	  String description = splitValues[0];
	  int maxScore = Integer.parseInt(splitValues[1]);

	  JudgingCriteria criteria = new JudgingCriteria(Integer.parseInt(entry.getKey()), description, maxScore);
	  criteriaList.add(criteria);
	}

	return criteriaList;
  }

  private void showJudgingRules(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
	Map<String, String> judging = loadFile(JUDGING_FILENAME);

	setSessionAttribute(request, RequestType.GetJudgingRules.name(), judging);
	redirectRequest(request, response, "/jsp/showJudgingRules.jsp");
  }

  private void redirectRequest(HttpServletRequest request, HttpServletResponse response, String path) throws IOException
  {
	String requestURL = request.getRequestURL().toString();
	while (requestURL.indexOf(getClass().getSimpleName()) > -1)
	{
	  requestURL = requestURL.substring(0, requestURL.lastIndexOf("/"));
	}

	requestURL += path;

	response.sendRedirect(requestURL);
  }

  private File getFile(String fileName)
  {
	String basePath = "." + File.separator;
	final String extension = ".txt";

	File file = null;
	do
	{
	  file = new File(basePath + fileName + extension).getAbsoluteFile();
	  basePath += ".." + File.separator;
	}
	while (!file.exists());

	return file;
  }

  private Map<String, String> loadFile(String fileName) throws IOException
  {
	return loadFile(getFile(fileName));
  }

  private Map<String, String> loadFile(File file) throws IOException
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
  }
}
