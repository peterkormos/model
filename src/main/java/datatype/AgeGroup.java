package datatype;

public enum AgeGroup
{
  ADULT(19, 999), JUNIOR(16, 18), YOUNGSTER(13, 15), CHILD(0, 12), ALL(0, 999);

  private int from;
  private int to;

  AgeGroup(int from, int to)
  {
	this.from = from;
	this.to = to;
  }

  private boolean isIn(int age)
  {
	return age >= from && age <= to;
  }

  public boolean contains(AgeGroup userAge)
  {
	return from <= userAge.from && to >= userAge.to;
  }

  public static AgeGroup get(int age)
  {
	for (AgeGroup group : AgeGroup.values())
	{
	  if (group.isIn(age))
	  {
		return group;
	  }
	}

	throw new IllegalArgumentException("Wrong age: " + age);
  }
}