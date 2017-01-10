package datatype;

public class JudgingCriteria
{
  private int id;
  private String description;
  private int maxScore;

  public JudgingCriteria(int id, String description, int maxScore)
  {
	super();
	this.id = id;
	this.description = description;
	this.maxScore = maxScore;
  }

  public int getId()
  {
	return id;
  }

  public void setId(int id)
  {
	this.id = id;
  }

  public String getDescription()
  {
	return description;
  }

  public void setDescription(String description)
  {
	this.description = description;
  }

  public int getMaxScore()
  {
	return maxScore;
  }

  public void setMaxScore(int maxScore)
  {
	this.maxScore = maxScore;
  }

  @Override
  public String toString()
  {
	return "JudgingCriteria [id=" + id + ", description=" + description + ", maxScore=" + maxScore + "]";
  }

}
