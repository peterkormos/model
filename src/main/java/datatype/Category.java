package datatype;

import java.io.Serializable;

public class Category implements Serializable
{
  public int categoryID;

  public CategoryGroup group;
  public String categoryCode;
  public String categoryDescription;
  private boolean master;
  private ModelClass modelClass;
  private AgeGroup ageGroup;

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

  public Category(final int categoryID, final String categoryCode, final String categoryDescription, final CategoryGroup group,
	  boolean master, ModelClass modelClass, AgeGroup ageGroup)
  {
	this.categoryID = categoryID;
	this.categoryCode = categoryCode;
	this.categoryDescription = categoryDescription;
	this.group = group;

	this.master = master;
	this.modelClass = modelClass;
	this.ageGroup = ageGroup;
  }

  public boolean isMaster()
  {
	return master;
  }

  public void setMaster(boolean master)
  {
	this.master = master;
  }

  public ModelClass getModelClass()
  {
	return modelClass;
  }

  public void setModelClass(ModelClass modelClass)
  {
	this.modelClass = modelClass;
  }

  public AgeGroup getAgeGroup()
  {
	return ageGroup;
  }

  public void setAgeGroup(AgeGroup ageGroup)
  {
	this.ageGroup = ageGroup;
  }

  @Override
  public String toString()
  {
	return "Category [categoryID=" + categoryID + ", group=" + group + ", categoryCode=" + categoryCode
	    + ", categoryDescription=" + categoryDescription + ", master=" + master + ", modelClass=" + modelClass + ", ageGroup="
	    + ageGroup + "]";
  }
}