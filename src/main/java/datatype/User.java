package datatype;

import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class User implements Serializable
{
  public int userID;
  public String password;
  public String firstName;
  public String lastName;
  public int yearOfBirth;
  public String city;
  public String address;
  public String telephone;
  public String email;
  public boolean enabled;
  public String language;
  public String country;
  private List<ModelClass> modelClass = new LinkedList<ModelClass>();

  public static final String LOCAL_USER = "_LOCAL_";

  public String getFullName()
  {
	return lastName + " " + firstName;
  }

  public int getUserID()
  {
	return userID;
  }

  public void setUserID(int userID)
  {
	this.userID = userID;
  }

  public String getPassword()
  {
	return password;
  }

  public void setPassword(String password)
  {
	this.password = password;
  }

  public String getFirstName()
  {
	return firstName;
  }

  public void setFirstName(String firstName)
  {
	this.firstName = firstName;
  }

  public String getLastName()
  {
	return lastName;
  }

  public void setLastName(String lastName)
  {
	this.lastName = lastName;
  }

  public int getYearOfBirth()
  {
	return yearOfBirth;
  }

  public void setYearOfBirth(int yearOfBirth)
  {
	this.yearOfBirth = yearOfBirth;
  }

  public String getCity()
  {
	return city;
  }

  public void setCity(String city)
  {
	this.city = city;
  }

  public String getAddress()
  {
	return address;
  }

  public void setAddress(String address)
  {
	this.address = address;
  }

  public String getTelephone()
  {
	return telephone;
  }

  public void setTelephone(String telephone)
  {
	this.telephone = telephone;
  }

  public String getEmail()
  {
	return email;
  }

  public void setEmail(String email)
  {
	this.email = email;
  }

  public boolean isEnabled()
  {
	return enabled;
  }

  public void setEnabled(boolean enabled)
  {
	this.enabled = enabled;
  }

  public String getLanguage()
  {
	return language;
  }

  public void setLanguage(String language)
  {
	this.language = language;
  }

  public String getCountry()
  {
	return country;
  }

  public void setCountry(String country)
  {
	this.country = country;
  }

  public User()
  {

  }

  public User(String language)
  {
	this(0, null, null, null, language, null, null, null, false, null, Calendar.getInstance().get(Calendar.YEAR), null);
  }

  public User(int userID, String password, String firstname, String lastname, String language, String address, String telephone,
      String email, boolean enabled, String country, int yearOfBirth, String city)
  {
	this.userID = userID;
	this.password = password;
	this.firstName = firstname;
	this.lastName = lastname;
	this.language = language;
	this.address = address;
	this.telephone = telephone;
	this.email = email;
	this.enabled = enabled;
	this.country = country;
	this.yearOfBirth = yearOfBirth;
	this.city = city;
  }

  public List<ModelClass> getModelClass()
  {
	return modelClass;
  }

  public void setModelClass(List<ModelClass> modelClass)
  {
	this.modelClass = modelClass;
  }

  @Override
  public String toString()
  {
	return "User [userID=" + userID + ", password=" + password + ", firstName=" + firstName + ", lastName=" + lastName
	    + ", yearOfBirth=" + yearOfBirth + ", city=" + city + ", address=" + address + ", telephone=" + telephone + ", email="
	    + email + ", enabled=" + enabled + ", language=" + language + ", country=" + country + ", modelClass=" + modelClass + "]";
  }

  public int getAge()
  {
	return Calendar.getInstance().get(Calendar.YEAR) - yearOfBirth;
  }

  public boolean isLocalUser()
  {
	return email.indexOf(LOCAL_USER) > -1;
  }
}