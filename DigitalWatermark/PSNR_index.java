import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;

/**
 * This plugin calculate and display PSNR (Peak signal-to-noise ratio) index between two images.
 * Two grayscale images are requested, when you run this.
 * They must be the same size.
 * @author <a href="https://github.com/Y-Ichioka">Y-Ichioka</a>
 * @version 0.1
 * @see <a href="https://en.wikipedia.org/wiki/Peak_signal-to-noise_ratio">Peak signal-to-noise ratio</a>
 * @see <a href="https://imagej.nih.gov/ij/developer/api/ij/plugin/PlugIn.html">PlugIn (ImageJ API)</a>
 */
public class PSNR_index implements PlugIn {

  private double getPSNR(ImagePlus image_1, ImagePlus image_2) {
    int h = image_1.getHeight();
    int w = image_1.getWidth();

    double mse = 0.0;

    for (int y=0; y<h; y++) {
      for (int x=0; x<w; x++) {
        mse += Math.pow(image_1.getPixel(x, y)[0] - image_2.getPixel(x, y)[0], 2);
      }
    }
    mse /= (double)(w*h);

    return 20 * Math.log10(255/Math.sqrt(mse));
  }

  /**
   * This method is concrete method of PlugIn class.
   * It's executed at first.
   * @see <a href="https://imagej.nih.gov/ij/developer/api/ij/plugin/PlugIn.html">PlugIn (ImageJ API)</a>
   */
  @Override
  public void run(String arg) {
    ImagePlus imp = IJ.getImage();

    int[] wList = WindowManager.getIDList();

    if (WindowManager.getImageCount() != 2) {
      IJ.error("There must be two images open to calculate PSNR index");
      return;
    }

    ImagePlus image_1 = WindowManager.getImage(wList[0]);
    ImagePlus image_2 = WindowManager.getImage(wList[1]);

    if (image_1.getHeight() != image_2.getHeight() && image_1.getWidth() != image_2.getWidth()) {
      IJ.error("Both images must have the same size");
      return;
    }
    if (image_1.getType() != image_1.GRAY8 || image_2.getType() != image_2.GRAY8){
      IJ.error("Both images must have the same image of type:\n8-bit grayscale");
      return;
    }

    IJ.log("PSNR index: " + getPSNR(image_1, image_2));  
  }

}
