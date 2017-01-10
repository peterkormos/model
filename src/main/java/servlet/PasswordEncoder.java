package servlet;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class PasswordEncoder
{
  private static final MessageDigest MESSAGE_DIGEST;

  static
  {
	try
	{
	  MESSAGE_DIGEST = MessageDigest.getInstance("MD5");
	}
	catch (NoSuchAlgorithmException e)
	{
	  throw new ExceptionInInitializerError(e);
	}
  }

  public static String encode(String rawText)
  {
	return toBase64(hash(rawText));
  }

  public static String toBase64(byte[] hash)
  {
	return DatatypeConverter.printBase64Binary(hash);
  }

  public static byte[] fromBase64(String base64Text)
  {
	return DatatypeConverter.parseBase64Binary(base64Text);
  }

  public static byte[] hash(String rawText)
  {
	return MESSAGE_DIGEST.digest(rawText.getBytes());
  }

}
