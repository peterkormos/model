package tools;

import java.beans.XMLDecoder;
import java.io.FileInputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

import datatype.Category;
import datatype.CategoryGroup;
import datatype.Model;
import datatype.User;
import servlet.RegistrationServlet;

public class ImportData
{

  public ImportData(String baseDir, String DB_Driver, String DB_URL, String DB_Username, String DB_Password, String dataFile)
      throws Exception
  {
	RegistrationServlet servlet = new RegistrationServlet();
	//    servlet.init(baseDir, DB_Driver, DB_URL, DB_Username, DB_Password, null,
	//        false, null);

	servlet.servletDAO.deleteEntries("MAK_CATEGORY_GROUP");

	servlet.servletDAO.deleteEntries("MAK_CATEGORY");

	servlet.servletDAO.deleteEntries("MAK_MODEL");

	servlet.servletDAO.deleteEntries("MAK_USERS");

	XMLDecoder d = new XMLDecoder(new GZIPInputStream(new FileInputStream(dataFile)));

	List data = (List) d.readObject();
	d.close();

	List<User> users = (List<User>) data.get(0);
	List<CategoryGroup> categoryGroups = (List<CategoryGroup>) data.get(1);
	List<Category> categories = (List<Category>) data.get(2);
	List<Model> models = (List<Model>) data.get(3);

	System.out.println("Storing users");
	for (User user : users)
	{
	  System.out.print(".");
	  servlet.servletDAO.registerNewUser(user);
	}

	System.out.println("\nStoring CategoryGroups");
	for (CategoryGroup categoryGroup : categoryGroups)
	{
	  System.out.print(".");
	  servlet.servletDAO.saveCategoryGroup(categoryGroup);
	}

	System.out.println("\nStoring Categories");
	for (Category category : categories)
	{
	  System.out.print(".");
	  servlet.servletDAO.saveCategory(category);
	}

	System.out.println("\nStoring Models");
	for (Model model : models)
	{
	  System.out.print(".");
	  servlet.servletDAO.saveModel(model);
	}

	System.out.println("\nDONE.....");
  }

  public static void main(String[] args) throws Exception
  {
	new ImportData(args[0], args[1], args[2], args[3], args[4], args[5]);
  }
}
