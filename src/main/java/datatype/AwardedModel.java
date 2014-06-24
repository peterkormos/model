package datatype;

public class AwardedModel
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

  public String toString()
  {
    return " award: " + award + " model: " + model;
  }

}
