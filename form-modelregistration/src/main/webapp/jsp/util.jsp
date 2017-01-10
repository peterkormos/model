<%!
  boolean highlightFlag;
  long highlightStart;

  public final String highlight()
  {
	try
	{
	  if (highlightFlag)
		return Long.toHexString(highlightStart);
	  else
	  {
		return Long.toHexString(Math.min(highlightStart + 0xF6F4F0, 0xffffff));
	  }
	}
	finally
	{
	  highlightFlag = !highlightFlag;
	}
  }

%>