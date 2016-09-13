import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;

import ij.io.OpenDialog;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.image.BufferedImage;

/**
 * This plugin convert file to image.
 * A file is requested, when you run this.
 * This is the inverse process of "Image to File".
 * @author <a href="https://github.com/Y-Ichioka">Y-Ichioka</a>
 * @version 0.1
 * @see <a href="https://imagej.nih.gov/ij/developer/api/ij/plugin/PlugIn.html">PlugIn (ImageJ API)</a>
 */
public class File_to_Image implements PlugIn {

  // This method can be improved (by using ImageProcessor)
  private BufferedImage[] getBitPlanes(byte[] bytes, int w, int h) {
    // number of images
    int num = (int)Math.ceil((double)(bytes.length)/(w*h*3));
    BufferedImage[] bitPlaneImages = new BufferedImage[num];

    for (int i=0; i<num; i++) {
      bitPlaneImages[i] = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    }

    // last one (fill white)
    for (int y=0; y<h; y++) {
      for (int x=0; x<w; x++) {
        bitPlaneImages[num-1].setRGB(x, y, 0xFFFFFFFF);
      }
    }

    // 3 bytes each
    int colors[] = new int[(int)Math.ceil((double)bytes.length/3)];

    int pointer = 0;
    for (int i=0; i<colors.length-1; i++) {
      colors[i] = 0x00000000;
      for (int j=0; j<3; j++) {
        colors[i] |= (0x000000FF & bytes[pointer++]) << 8*(2-j);
      }
    }

    for (int j=0; j<3; j++) {
      if (pointer < bytes.length) {
        colors[colors.length-1] |= (0x000000FF & bytes[pointer++]) << 8*(2-j);
      } else {
        colors[colors.length-1] |= (0x000000FF & 0x00000000) << 8*(2-j);
      }
    }

    // for (int i=0; i<colors.length; i++) {
    //   IJ.log(Integer.toHexString(colors[i]));
    // }

    for (int i=0; i<colors.length; i++) {
      int n = (int)(i/(w*h));
      int x = i%w;
      int y = (int)(i/w) - n*h;
      bitPlaneImages[n].setRGB(x, y, colors[i]);
    }

    return bitPlaneImages;
  }

  /**
   * This method is concrete method of PlugIn class.
   * It's executed at first.
   * @see <a href="https://imagej.nih.gov/ij/developer/api/ij/plugin/PlugIn.html">PlugIn (ImageJ API)</a>
   */
  @Override
  public void run(String arg) {
    OpenDialog openDialog = new OpenDialog("Open");

    // file open
    File file = new File(openDialog.getPath());
    byte[] readBinary = new byte[(int)file.length()];

    // binary read
    try {
      FileInputStream fis = new FileInputStream(file);
      BufferedInputStream bis = new BufferedInputStream(fis);
      bis.read(readBinary);
      bis.close();
      fis.close();
    } catch (FileNotFoundException e) {
      IJ.error("" + e);
      return;
    } catch (IOException e) {
      IJ.error("" + e);
      return;
    }

    // dialog
    GenericDialog gd = new GenericDialog("New Image");
    gd.addStringField("Title: ", openDialog.getFileName());
    gd.addNumericField("Width: ", 512, 0);
    gd.addNumericField("Height: ", 512, 0);
    gd.showDialog();
    if (gd.wasCanceled()) {
      return;
    }
    String name = gd.getNextString();
    int width = (int)gd.getNextNumber();
    int height = (int)gd.getNextNumber();

    // convert and output
    BufferedImage[] images = getBitPlanes(readBinary, width, height);

    if (images.length == 1) {
      new ImagePlus(name, images[0]).show();
    } else {
      for (int i=0; i<images.length; i++) {
        new ImagePlus(name + "." + i, images[i]).show();
      }
    }


  }

}
