import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class StandaloneServer
{
  public static void main(String[] args) throws Exception
  {
	final int port = Integer.parseInt(args[0]);
	final String warFile = args[1];

	final Server server = new Server(port);

	final org.eclipse.jetty.webapp.Configuration.ClassList classlist = org.eclipse.jetty.webapp.Configuration.ClassList
	    .setServerDefault(server);
	classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
	    "org.eclipse.jetty.annotations.AnnotationConfiguration");

	final WebAppContext webapp = new WebAppContext();
	webapp.setContextPath("/makett/");
	webapp.setWar(warFile);
	server.setHandler(webapp);

	server.start();
	server.join();
  }

}
