import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

public class t
{
  public static void main(String[] args) throws Exception
  {
	final int port = 8080;
	final String warFile = "c:\\MR2\\makett.war";

	//	final Server server = new Server(port);
	//
	//	final org.eclipse.jetty.webapp.Configuration.ClassList classlist = org.eclipse.jetty.webapp.Configuration.ClassList
	//	    .setServerDefault(server);
	//	classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
	//	    "org.eclipse.jetty.annotations.AnnotationConfiguration");
	//
	//	final WebAppContext webapp = new WebAppContext();
	//	webapp.setWar(warFile);
	//
	//	webapp.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
	//	    ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$");
	//
	//	server.setHandler(webapp);
	//
	//	server.start();
	//	server.join();
	final Server server = new Server();
	final ServerConnector connector = new ServerConnector(server);
	connector.setPort(port);
	server.addConnector(connector);

	// Establish Scratch directory for the servlet context (used by JSP compilation)
	final File tempDir = new File(System.getProperty("java.io.tmpdir"));
	final File scratchDir = new File(tempDir.toString(), "embedded-jetty-jsp");

	if (!scratchDir.exists())
	{
	  if (!scratchDir.mkdirs())
	  {
		throw new IOException("Unable to create scratch directory: " + scratchDir);
	  }
	}

	// Set JSP to use Standard JavaC always
	System.setProperty("org.apache.jasper.compiler.disablejsr199", "false");

	// Setup the basic application "context" for this application at "/"
	// This is also known as the handler tree (in jetty speak)
	final WebAppContext context = new WebAppContext();
	context.setContextPath("/");
	context.setAttribute("javax.servlet.context.tempdir", scratchDir);
	context.setWar(warFile);
	server.setHandler(context);

	// Add Application Servlets
	//Ensure the jsp engine is initialized correctly
	final JettyJasperInitializer sci = new JettyJasperInitializer();
	final ServletContainerInitializersStarter sciStarter = new ServletContainerInitializersStarter(context);
	final ContainerInitializer initializer = new ContainerInitializer(sci, null);
	final List<ContainerInitializer> initializers = new ArrayList<ContainerInitializer>();
	initializers.add(initializer);

	context.setAttribute("org.eclipse.jetty.containerInitializers", initializers);
	context.addBean(sciStarter, true);

	// Set Classloader of Context to be sane (needed for JSTL)
	// JSP requires a non-System classloader, this simply wraps the
	// embedded System classloader in a way that makes it suitable
	// for JSP to use
	final ClassLoader jspClassLoader = new URLClassLoader(new URL[0], t.class.getClassLoader());
	context.setClassLoader(jspClassLoader);

	// Add JSP Servlet (must be named "jsp")
	final ServletHolder holderJsp = new ServletHolder("jsp", JspServlet.class);
	holderJsp.setInitOrder(0);
	holderJsp.setInitParameter("logVerbosityLevel", "DEBUG");
	holderJsp.setInitParameter("fork", "false");
	holderJsp.setInitParameter("xpoweredBy", "false");
	holderJsp.setInitParameter("compilerTargetVM", "1.7");
	holderJsp.setInitParameter("compilerSourceVM", "1.7");
	holderJsp.setInitParameter("keepgenerated", "true");
	context.addServlet(holderJsp, "*.jsp");
	//context.addServlet(holderJsp,"*.jspf");
	//context.addServlet(holderJsp,"*.jspx");

	// Add Example of mapping jsp to path spec
	final ServletHolder holderAltMapping = new ServletHolder("foo.jsp", JspServlet.class);
	holderAltMapping.setForcedPath("/test/foo/foo.jsp");
	context.addServlet(holderAltMapping, "/test/foo/");

	// Add Default Servlet (must be named "default")
	final ServletHolder holderDefault = new ServletHolder("default", DefaultServlet.class);
	holderDefault.setInitParameter("resourceBase", "/resourceBase");
	holderDefault.setInitParameter("dirAllowed", "true");
	context.addServlet(holderDefault, "/");

	// Start Server
	server.start();

	// Establish the Server URI
	String scheme = "http";
	for (final ConnectionFactory connectFactory : connector.getConnectionFactories())
	{
	  if (connectFactory.getProtocol().equals("SSL-http"))
	  {
		scheme = "https";
	  }
	}
	String host = connector.getHost();
	if (host == null)
	{
	  host = "localhost";
	}
  }
}
