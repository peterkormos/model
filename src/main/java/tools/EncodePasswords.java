package tools;

import java.sql.Driver;
import java.sql.DriverManager;

import datatype.User;
import servlet.StringEncoder;
import servlet.ServletDAO;

public class EncodePasswords
{
  public EncodePasswords(String DB_Driver, String DB_URL, String DB_Username, String DB_Password) throws Exception
  {
	DriverManager.registerDriver((Driver) Class.forName(DB_Driver).newInstance());

	ServletDAO servletDAO = new ServletDAO(null, DB_URL, DB_Username, DB_Password);

	for (User user : servletDAO.getUsers())
	{
	  String newPassword = StringEncoder.encode(user.password);

	  System.out.printf("Converting old password %1$s to %2$s\n", user.password, newPassword);

	  user.setPassword(newPassword);

	  servletDAO.modifyUser(user, user);
	}

	System.out.println("\nDONE.....");
  }

  public EncodePasswords(String[] args) throws Exception
  {
	this(args[0], args[1], args[2], args[3]);
  }

  public static void main(String[] args) throws Exception
  {
	new EncodePasswords(args);
  }
}
