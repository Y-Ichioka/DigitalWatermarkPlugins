import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;

/**
 * This plugin convert grayscale image to 8 bit planes.
 * A grayscale image is requested, when you run this.
 * This is the inverse process of "BitPlanes to GrayScale".
 * @author <a href="https://github.com/Y-Ichioka">Y-Ichioka</a>
 * @version 0.1
 * @see <a href="https://en.wikipedia.org/wiki/Bit_plane">Bit plane</a>
 * @see <a href="https://imagej.nih.gov/ij/developer/api/ij/plugin/PlugIn.html">PlugIn (ImageJ API)</a>
 */
public class GrayScale_to_BitPlanes implements PlugIn {

  private ByteProcessor getBitPlane(ImageProcessor imp, int layer) {
    int w = imp.getWidth();
    int h = imp.getHeight();

    ByteProcessor bitPlaneImage = new ByteProcessor(w, h);

    for (int y=0; y<h; y++) {
      for (int x=0; x<w; x++) {
        bitPlaneImage.set(x, y, ((imp.get(x, y) >> layer & 0x00000001) == 1) ? 0:255);
      }
    }

    return bitPlaneImage;
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

    ImagePlus image = IJ.getImage();
    ImageProcessor imagePro = image.getProcessor();

    if (image.getType() != image.GRAY8) {
      IJ.error("\"GrayScale to BitPlanes\" requires an image of type:\n8-bit grayscale");
      return;
    }

    // generate
    for (int i=0; i<8; i++) {
      ByteProcessor bitPlane = getBitPlane(imagePro, i);
      ImagePlus ipl = new ImagePlus(image.getTitle() + " (layer " + i + ")", bitPlane);
      ipl.show();
    }

  }

}
