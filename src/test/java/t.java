import servlet.StringEncoder;

public class t
{

  public static void main(String[] args)
  {

	System.out.println(StringEncoder.encode("G&ouml;d&ouml;ll&otilde; 2015"));
	System.out.println(StringEncoder.encode(StringEncoder.encode("G&ouml;d&ouml;ll&otilde; 2015")));
	System.out.println(StringEncoder.fromBase64("M21evFQ2U05h0W5j3fyjJw=="));
	System.out.println(StringEncoder.fromBase64("/jouIjcCc45f77R1qoZv3Q=="));
  }

}
