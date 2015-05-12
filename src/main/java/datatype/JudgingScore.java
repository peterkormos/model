package datatype;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "mak_judgingscore")
public class JudgingScore extends Record
{
  @Column
  private String category;
  @Column
  private String judge;
  @Column
  private int modelID;
  @Column
  private int modellerID;
  @Column
  private int criteriaID;
  @Column
  private int score;
  @Column(length = 1000, nullable = true)
  private String comment;

  public JudgingScore()
  {

  }

  public JudgingScore(int id, String category, String judge, int modelID, int modellerID, int criteriaID, int score,
	  String comment)
  {
	super(id);

	this.category = category;
	this.judge = judge;
	this.modelID = modelID;
	this.modellerID = modellerID;
	this.criteriaID = criteriaID;
	this.score = score;
	this.comment = comment;
  }

  @Override
  public int getId()
  {
	return id;
  }

  @Override
  public void setId(int id)
  {
	this.id = id;
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

  public int getCriteriaID()
  {
	return criteriaID;
  }

  public void setCriteriaID(int criteriaID)
  {
	this.criteriaID = criteriaID;
  }

  public int getScore()
  {
	return score;
  }

  public void setScore(int score)
  {
	this.score = score;
  }

  public String getComment()
  {
	return comment;
  }

  public void setComment(String comment)
  {
	this.comment = comment;
  }

  @Override
  public String toString()
  {
	return "JudgingScore [id=" + id + ", category=" + category + ", judge=" + judge + ", modelID=" + modelID + ", modellerID="
	    + modellerID + ", criteriaID=" + criteriaID + ", score=" + score + ", comment=" + comment + "]";
  }

}
