package datatype;

public class Category
{
  public int categoryID;

  public CategoryGroup group;
  public String categoryCode;
  public String categoryDescription;

  public int getCategoryID()
  {
	return categoryID;
  }

  public void setCategoryID(final int categoryID)
  {
	this.categoryID = categoryID;
  }

  public CategoryGroup getGroup()
  {
	return group;
  }

  public void setGroup(final CategoryGroup group)
  {
	this.group = group;
  }

  public String getCategoryCode()
  {
	return categoryCode;
  }

  public void setCategoryCode(final String categoryCode)
  {
	this.categoryCode = categoryCode;
  }

  public String getCategoryDescription()
  {
	return categoryDescription;
  }

  public void setCategoryDescription(final String categoryDescription)
  {
	this.categoryDescription = categoryDescription;
  }

  public Category()
  {

  }

  public Category(final int categoryID, final String categoryCode, final String categoryDescription, final CategoryGroup group)
  {
	this.categoryID = categoryID;
	this.categoryCode = categoryCode;
	this.categoryDescription = categoryDescription;
	this.group = group;
  }

  @Override
  public String toString()
  {
	return "Category [categoryID=" + categoryID + ", group=" + group.categoryGroupID + ", categoryCode=" + categoryCode
	    + ", categoryDescription=" + categoryDescription + "]";
  }
}