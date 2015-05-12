package servlet;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;

public class ServletUtil
{

  public static String getOptionalRequestAttribute(final HttpServletRequest request, final String name)
  {
    try
    {
      final String value = ServletUtil.getRequestAttribute(request, name, false);
  
      return "".equals(value) ? "-" : value;
    }
    catch (final Exception e)
    {
      e.printStackTrace();
      return "-";
    }
  }

  public static String getRequestAttribute(final HttpServletRequest request, final String name, final boolean throwException)
      throws Exception
  {
    String value;
    value = request.getParameter(name);
    if (value == null || value.trim().length() == 0)
    {
      if (throwException)
      {
    	throw new Exception(name + " is not set!");
      }
      else
      {
    	return "-";
      }
    }
  
    if (RegistrationServlet.logger.isDebugEnabled())
    {
      RegistrationServlet.logger.debug("HTTP parameter: " + name + " value: " + value);
    }
  
    return value.trim();
  }

  public static void sendEmail(String smtpServer, final String from, String to, String subject, String htmlMessage,
      boolean debugSMTP, final String password) throws Exception
  {
    if (from == null)
    {
      throw new Exception("!!! Utils.sendMessage(): FROM address is null!");
    }
  
    if (from.indexOf("@") == -1)
    {
      throw new Exception("!!! Utils.sendMessage(): invalid FROM e-mail address: " + from);
    }
  
    if (to == null)
    {
      throw new Exception("!!! Utils.sendMessage(): TO address is null !");
    }
  
    if (to.indexOf("@") == -1)
    {
      throw new Exception("!!! Utils.sendMessage(): invalid TO e-mail address: " + to);
    }
  
    final Properties props = new Properties();
    props.put("mail.smtp.host", smtpServer);
    props.put("mail.debug", debugSMTP);
    props.put("mail.smtp.socketFactory.port", "465");
    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.port", "465");
  
    final Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator()
    {
      @Override
      protected PasswordAuthentication getPasswordAuthentication()
      {
    	return new PasswordAuthentication(from, password);
      }
    });
  
    final Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress(from));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
    message.setSubject(subject);
  
    // Create a multi-part to combine the parts
    final Multipart multipart = new MimeMultipart("alternative");
  
    // Create your text message part
    BodyPart messageBodyPart = new MimeBodyPart();
    messageBodyPart
    .setText("Ha ezt latod, akkor a levelezod nem jol jeleniti meg az emailt. Kerlek valaszolj a feladonak a hibaval kapcsolatban.");
  
    // Add the text part to the multipart
    multipart.addBodyPart(messageBodyPart);
  
    // Create the html part
    messageBodyPart = new MimeBodyPart();
    messageBodyPart.setContent(htmlMessage, "text/html");
  
    // Add html part to multi part
    multipart.addBodyPart(messageBodyPart);
  
    // Associate multi-part with message
    message.setContent(multipart);
  
    Transport.send(message);
  }

  public static String getRequestAttribute(final HttpServletRequest request, final String name) throws Exception
  {
    return getRequestAttribute(request, name, true);
  }

}
