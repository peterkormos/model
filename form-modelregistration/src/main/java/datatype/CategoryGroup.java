package datatype;

import java.io.Serializable;

public class CategoryGroup implements Serializable
{
  public String show;
  public String name;
  public int categoryGroupID;

  public int getCategoryGroupID()
  {
	return categoryGroupID;
  }

  public void setCategoryGroupID(final int categoryGroupID)
  {
	this.categoryGroupID = categoryGroupID;
  }

  public String getShow()
  {
	return show;
  }

  public void setShow(final String show)
  {
	this.show = show;
  }

  public String getGroup()
  {
	return name;
  }

  public void setGroup(final String group)
  {
	this.name = group;
  }

  public CategoryGroup()
  {

  }

  public CategoryGroup(final int categoryGroupID, final String show, final String group)
  {
	this.categoryGroupID = categoryGroupID;
	this.show = show;
	this.name = group;
  }

  @Override
  public String toString()
  {
	return "CategoryGroup [show=" + show + ", categoryGroupID=" + categoryGroupID + ", group=" + name + "]";
  }

}
