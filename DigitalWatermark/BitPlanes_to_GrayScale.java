import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;

/**
 * This plugin convert 8 bit planes to grayscale image.
 * 8 images (binary images) are requested, when you run this.
 * They must be the same size.
 * This is the inverse process of "GrayScale to BitPlanes".
 * @author <a href="https://github.com/Y-Ichioka">Y-Ichioka</a>
 * @version 0.1
 * @see <a href="https://en.wikipedia.org/wiki/Bit_plane">Bit plane</a>
 * @see <a href="https://imagej.nih.gov/ij/developer/api/ij/plugin/PlugIn.html">PlugIn (ImageJ API)</a>
 */
public class BitPlanes_to_GrayScale implements PlugIn {

  private ByteProcessor getImageFromBitPlanes(ImageProcessor[] planes) {
    int h = planes[0].getWidth();
    int w = planes[0].getHeight();

    ByteProcessor newImage = new ByteProcessor(w, h);

    for (int y=0; y<h; y++) {
      for (int x=0; x<w; x++) {
        int gray = 0x00000000;

        for (int i=0; i<8; i++) {
          if (planes[i].get(x, y) < 128) {
            gray |= 1 << i;
          }
        }

        newImage.set(x, y, gray<<16 | gray<<8 | gray);
      }
    }

    return newImage;
  }


  /**
   * This method is concrete method of PlugIn class.
   * It's executed at first.
   * @see <a href="https://imagej.nih.gov/ij/developer/api/ij/plugin/PlugIn.html">PlugIn (ImageJ API)</a>
   */
  @Override
  public void run(String arg) {
    String[] imageTitles = WindowManager.getImageTitles();
    if (imageTitles.length == 0) {
      IJ.error("There are no images open.");
      return;
    }

    // input
    GenericDialog gd = new GenericDialog("Select BitPlanes");

    gd.addStringField("New image name", imageTitles[0]);
    for (int i=0; i<Math.min(imageTitles.length, 8); i++) {
      gd.addChoice("layer " + i, imageTitles, imageTitles[i]);
    }
    IJ.log("" + imageTitles.length);
    if (imageTitles.length < 8) {
      for (int i=imageTitles.length; i<8; i++) {
        gd.addChoice("layer " + i, imageTitles, imageTitles[0]);
      }
    }

    gd.showDialog();

    if (gd.wasCanceled()) {
      return;
    }

    // load
    String newImageName = gd.getNextString();
    ImagePlus[] bitPlanes = new ImagePlus[8];
    ImageProcessor[] bitPlanesPro = new ImageProcessor[8];
    for (int i=0; i<8; i++) {
      bitPlanes[i] = WindowManager.getImage(gd.getNextChoice());
      bitPlanesPro[i] = bitPlanes[i].getProcessor();
    }

    // check
    int tmpWidth = bitPlanes[0].getWidth();
    int tmpHeight = bitPlanes[0].getHeight();
    for (int i=1; i<8; i++) {
      if (tmpWidth != bitPlanes[i].getWidth() || tmpHeight != bitPlanes[i].getHeight()) {
        IJ.error("All images must have the same size");
        return;
      }
    }

    boolean isAllImageBinary = true;
    for (int i=0; i<8; i++) {
      if (!bitPlanesPro[i].isBinary()) {
        isAllImageBinary = false;
        break;
      }
    }

    if (!isAllImageBinary) {
      IJ.error("All images must be 8-bit binary image (0 and 255)");
      return;
    }

    // create image
    new ImagePlus(newImageName, getImageFromBitPlanes(bitPlanesPro)).show();
  }

}
