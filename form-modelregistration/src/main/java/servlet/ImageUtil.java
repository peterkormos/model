package servlet;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ImageUtil
{
  public static void main(String[] args)
  {
	BufferedImage originalImage = null;
	try
	{
	  originalImage = ImageIO.read(new FileInputStream("k1.png"));

	  final BufferedImage resizedImage = resize(originalImage, 800);
	  if (resizedImage != originalImage)
	  {
		save(resizedImage, new FileOutputStream("saved2.jpg"));
	  }
	}
	catch (final IOException e)
	{
	}
  }

  public static BufferedImage load(InputStream stream) throws IOException
  {
	return ImageIO.read(stream);
  }

  public static void save(final BufferedImage resized, OutputStream stream) throws IOException
  {
	final Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
	final ImageWriter writer = iter.next();
	final ImageWriteParam iwp = writer.getDefaultWriteParam();
	iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	iwp.setCompressionQuality(1f);

	final ImageOutputStream ios = ImageIO.createImageOutputStream(stream);
	writer.setOutput(ios);
	writer.write(null, new IIOImage(resized, null, null), iwp);
	writer.dispose();
  }

  public static BufferedImage resize(BufferedImage originalImage, int scaledWidth)
  {
	if (scaledWidth >= originalImage.getWidth(null))
	{
	  return originalImage;
	}

	final int scaledHeight = scaledWidth * originalImage.getHeight(null) / originalImage.getWidth(null);

	final BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
	final Graphics2D g = scaledBI.createGraphics();
	g.setComposite(AlphaComposite.Src);
	g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
	g.dispose();
	return scaledBI;
  }

}
