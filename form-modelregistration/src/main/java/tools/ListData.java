package tools;

import java.beans.XMLDecoder;
import java.io.FileInputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

import datatype.Category;
import datatype.CategoryGroup;
import datatype.Model;
import datatype.User;

public class ListData
{

  public ListData(final String dataFile) throws Exception
  {
	final XMLDecoder d = new XMLDecoder(new GZIPInputStream(new FileInputStream(dataFile)));

	final List data = (List) d.readObject();
	d.close();

	final List<User> users = (List<User>) data.get(0);
	final List<CategoryGroup> categoryGroups = (List<CategoryGroup>) data.get(1);
	final List<Category> categories = (List<Category>) data.get(2);
	final List<Model> models = (List<Model>) data.get(3);

	final boolean logEach = false;

	System.out.println("Users: " + users.size());
	for (final User user : users)
	{
	  if (logEach)
	  {
		System.out.println(user);
	  }
	}

	System.out.println("\nCategoryGroups: " + categoryGroups.size());
	for (final CategoryGroup categoryGroup : categoryGroups)
	{
	  if (logEach)
	  {
		System.out.println(categoryGroup);
	  }
	}

	System.out.println("\nCategories: " + categories.size());
	for (final Category category : categories)
	{
	  if (logEach)
	  {
		System.out.println(category);
	  }
	}

	System.out.println("\nModels: " + models.size());
	for (final Model model : models)
	{
	  if (logEach)
	  {
		System.out.println(model);
	  }
	}
  }

  public static void main(final String[] args) throws Exception
  {
	new ListData(args[0]);
  }
}
