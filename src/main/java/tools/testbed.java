package tools;

public class testbed
{

  testbed()
  {
	System.out.println(getClass().getSimpleName());
  }

  public static void main(String[] args) throws Exception
  {
	new testbed();
  }
}
