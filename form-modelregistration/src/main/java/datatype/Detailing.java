package datatype;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Detailing implements Serializable
{
  public final static String DETAILING_GROUPS[] = new String[] { "scratch", "photoEtched", "resin", "documentation" };

  public final static String DETAILING_CRITERIAS[] = new String[] { "externalSurface", "cockpit", "engine", "undercarriage",
	"gearBay", "armament", "conversion" };

  public String group;

  public List<Boolean> criterias = new LinkedList<Boolean>();

  public String getGroup()
  {
	return group;
  }

  public void setGroup(String group)
  {
	this.group = group;
  }

  public List<Boolean> getCriterias()
  {
	return criterias;
  }

  public void setCriterias(List<Boolean> criterias)
  {
	this.criterias = criterias;
  }

  public Detailing()
  {

  }

  public Detailing(String group, List<Boolean> criterias)
  {
	this.group = group;
	this.criterias = criterias;
  }

  @Override
  public String toString()
  {
	return " group: " + group + " criterias: " + Arrays.asList(criterias);
  }
}
