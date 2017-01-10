package datatype;

import java.io.Serializable;

public class Model implements Serializable
{
  public int modelID;

  public int userID;
  public int categoryID;

  public String scale;
  public String name;
  public String producer;
  public String comment;

  public String identification;
  public String markings;
  public boolean gluedToBase;

  public Detailing[] detailing;

  public String award;

  public Model()
  {

  }

  public int getModelID()
  {
	return modelID;
  }

  public void setModelID(int modelID)
  {
	this.modelID = modelID;
  }

  public int getUserID()
  {
	return userID;
  }

  public void setUserID(int userID)
  {
	this.userID = userID;
  }

  public int getCategoryID()
  {
	return categoryID;
  }

  public void setCategoryID(int categoryID)
  {
	this.categoryID = categoryID;
  }

  public String getScale()
  {
	return scale;
  }

  public void setScale(String scale)
  {
	this.scale = scale;
  }

  public String getName()
  {
	return name;
  }

  public void setName(String name)
  {
	this.name = name;
  }

  public String getProducer()
  {
	return producer;
  }

  public void setProducer(String producer)
  {
	this.producer = producer;
  }

  public String getComment()
  {
	return comment;
  }

  public void setComment(String comment)
  {
	this.comment = comment;
  }

  public String getIdentification()
  {
	return identification;
  }

  public void setIdentification(String identification)
  {
	this.identification = identification;
  }

  public String getMarkings()
  {
	return markings;
  }

  public void setMarkings(String markings)
  {
	this.markings = markings;
  }

  public boolean isGluedToBase()
  {
	return gluedToBase;
  }

  public void setGluedToBase(boolean gluedToBase)
  {
	this.gluedToBase = gluedToBase;
  }

  public Detailing[] getDetailing()
  {
	return detailing;
  }

  public void setDetailing(Detailing[] detailing)
  {
	this.detailing = detailing;
  }

  public Model(int modelID, int userID, int categoryID, String scale, String name, String producer, String comment,
	  String identification, String markings, boolean gluedToBase, Detailing[] detailing)
  {
	this.modelID = modelID;
	this.userID = userID;
	this.categoryID = categoryID;
	this.scale = scale;
	this.name = name;
	this.producer = producer;
	this.comment = comment;

	this.identification = identification;
	this.markings = markings;
	this.gluedToBase = gluedToBase;

	this.detailing = detailing;
  }

  @Override
  public String toString()
  {
	String returned = " modelID: " + modelID + " userID: " + userID + " categoryID: " + categoryID + " scale: " + scale
	    + " name: " + name + " producer: " + producer + " comment: " + comment +

	    " identification: " + identification + " markings: " + markings + " gluedToBase: " + gluedToBase +

	    " detailing: ";

	for (int i = 0; i < detailing.length; i++)
	{
	  returned += "[" + detailing[i] + "] ";
	}

	return returned;
  }
}