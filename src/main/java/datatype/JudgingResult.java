package datatype;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class JudgingResult
{
  private String category;
  private String judge;
  private int modelID;
  private int modellerID;

  //key: 0 - (maxScore-1)
  //value:count
  private final Map<Integer, AtomicInteger> scores = new LinkedHashMap<Integer, AtomicInteger>();
  private int maxScore;

  public JudgingResult(JudgingScore result, int maxScore)
  {
	this.category = result.getCategory();
	this.judge = result.getJudge();
	this.modelID = result.getModelID();
	this.modellerID = result.getModellerID();
	this.maxScore = maxScore;
  }

  @Override
  public String toString()
  {
	return "JudgingResult [category=" + category + ", judge=" + judge + ", modelID=" + modelID + ", modellerID=" + modellerID
	    + ", maxScore=" + maxScore + ", scores=" + scores + "]";
  }

  public String getCategory()
  {
	return category;
  }

  public void setCategory(String category)
  {
	this.category = category;
  }

  public String getJudge()
  {
	return judge;
  }

  public void setJudge(String judge)
  {
	this.judge = judge;
  }

  public int getModelID()
  {
	return modelID;
  }

  public void setModelID(int modelID)
  {
	this.modelID = modelID;
  }

  public int getModellerID()
  {
	return modellerID;
  }

  public void setModellerID(int modellerID)
  {
	this.modellerID = modellerID;
  }

  public Map<Integer, AtomicInteger> getScores()
  {
	return scores;
  }

  public int getMaxScore()
  {
	return maxScore;
  }

  public void setMaxScore(int maxScore)
  {
	this.maxScore = maxScore;
  }

}
