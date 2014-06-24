package datatype;

import java.util.Arrays;

public class Detailing
{
  public final static String DETAILING_GROUPS[] = new String[]
  { "scratch", "photoEtched", "resin", "documentation" };

  public final static String DETAILING_CRITERIAS[] = new String[]
  { "externalSurface", "cockpit", "engine", "undercarriage", "gearBay",
      "armament", "conversion" };

  public String group;
  
  public boolean criterias[];

  public String getGroup()
  {
    return group;
  }

  public void setGroup(String group)
  {
    this.group = group;
  }

  public boolean[] getCriterias()
  {
    return criterias;
  }

  public void setCriterias(boolean[] criterias)
  {
    this.criterias = criterias;
  }

  public Detailing()
  {
    
  }
  
  public Detailing(String group, boolean criterias[])
  {
    this.group = group;
    this.criterias = criterias;
  }

  public String toString()
  {
    return " group: " + group + " criterias: " + Arrays.asList(criterias);
  }
}
