package servlet;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class StringEncoder
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

  public static String fromBase64(String base64Text)
  {
	if (base64Text == null)
	{
	  return "-";
	}
	else
	{
	  return new String(DatatypeConverter.parseBase64Binary(base64Text));
	}
  }

  public static byte[] hash(String rawText)
  {
	return MESSAGE_DIGEST.digest(rawText.getBytes());
  }

}
