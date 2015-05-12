package datatype;

import java.io.Serializable;

public class AwardedModel implements Serializable
{
  public Model model;
  public String award;

  public AwardedModel(String award, Model model)
  {
	this.award = award;
	this.model = model;
  }

  public AwardedModel()
  {

  }

  @Override
  public String toString()
  {
	return " award: " + award + " model: " + model;
  }

}
