package servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serialization
{
  public static Object deserialize(byte[] a) throws IOException, ClassNotFoundException
  {
	final ByteArrayInputStream byteStream = new ByteArrayInputStream(a);
	final ObjectInputStream os = new ObjectInputStream(byteStream);
	final Object ret = os.readObject();
	byteStream.close();
	os.close();
	return ret;
  }

  public static byte[] serialize(Object object) throws Exception
  {
	final ByteArrayOutputStream bout = new ByteArrayOutputStream();
	final ObjectOutputStream oout = new ObjectOutputStream(bout);
	oout.writeObject(object);
	oout.flush();
	bout.flush();
	final byte[] bt = bout.toByteArray();
	bout.close();
	oout.close();
	return bt;
  }

  public static Object deserialize(InputStream inputStream) throws Exception
  {
	final ObjectInputStream oin = new ObjectInputStream(inputStream);
	final Object obj = oin.readObject();
	oin.close();
	return obj;
  }

}
