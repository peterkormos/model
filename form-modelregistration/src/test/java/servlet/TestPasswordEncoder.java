package servlet;

import org.junit.Assert;
import org.junit.Test;

public class TestPasswordEncoder
{

  @Test
  public void test()
  {
	System.out.println(StringEncoder.encode("abc123"));
  }

  @Test

  public void base64Test()
  {
	Assert.assertEquals("abc123", StringEncoder.fromBase64(StringEncoder.toBase64("abc123".getBytes())));
  }

}
