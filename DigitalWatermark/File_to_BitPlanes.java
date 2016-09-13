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
 * This plugin convert file to binary image(s) (bit plane(s)).
 * An image are requested, when you run this.
 * The new image will be made of the same size as the specified image.
 * In the case of 0 painted in white, the case of 1 painted in black.
 * Surplus portions of the image are painted in white.
 * This is the inverse process of "BitPlane to File".
 * @author <a href="https://github.com/Y-Ichioka">Y-Ichioka</a>
 * @version 0.1
 * @see <a href="https://en.wikipedia.org/wiki/Bit_plane">Bit plane</a>
 * @see <a href="https://imagej.nih.gov/ij/developer/api/ij/plugin/PlugIn.html">PlugIn (ImageJ API)</a>
 */
public class File_to_BitPlanes implements PlugIn {

  private ByteProcessor[] getBitPlanes(byte[] bytes, int width, int height) {
    int w = width;
    int h = height;

    // number of images
    int num = (int)Math.ceil((double)(bytes.length*8)/(w*h));

    // BufferedImage[] bitPlaneImages = new BufferedImage[num];
    ByteProcessor[] bitPlaneImages = new ByteProcessor[num];

    for (int i=0; i<num; i++) {
      bitPlaneImages[i] = new ByteProcessor(w, h);
    }

    // last one (fill white)
    for (int y=0; y<h; y++) {
      for (int x=0; x<w; x++) {
        bitPlaneImages[num-1].set(x, y, 255);
      }
    }

    for (int i=0; i<bytes.length*8; i++) {
      int n = (int)Math.floor(i/(w*h));
      int x = i%w;
      int y = (int)(i/w) - n*h;
      int value = ( (bytes[(int)(i/8)] >> (7-(i%8)) & 0x00000001) == 1 ) ? 0:255;

      // IJ.log("" + n + ": " + x + ", " + y);
      bitPlaneImages[n].set(x, y, value);
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
    // check
    if (WindowManager.getIDList() == null) {
      IJ.error("There is no image open.");
      return;
    }

    ImagePlus imp = IJ.getImage();

    int w = imp.getWidth();
    int h = imp.getHeight();

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

    // convert and output
    ByteProcessor[] images = getBitPlanes(readBinary, w, h);
    for (int i=0; i<images.length; i++) {
      new ImagePlus(openDialog.getFileName() + " (no. " + i + ")", images[i]).show();
    }
  }

}
