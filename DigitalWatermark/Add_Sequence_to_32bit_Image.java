import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
// import ij.plugin.frame.*;
import ij.plugin.filter.*;

import java.io.IOException;
import java.io.FileNotFoundException;
// import java.io.File;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.BufferedReader;
import ij.io.OpenDialog;

/**
 * This plugin add sequence to the pixel values of the 32bit of the image.
 * If you specify a mask image, only if the pixel values of the mask image is not 0, to add the pixel of 32bit image.
 * @author <a href="https://github.com/Y-Ichioka">Y-Ichioka</a>
 * @version 0.1
 * @see <a href="https://imagej.nih.gov/ij/developer/api/ij/plugin/filter/PlugInFilter.html">PlugInFilter (ImageJ API)</a>
 */
public class Add_Sequence_to_32bit_Image implements PlugInFilter {
  ImagePlus imp;

  private double[] readSeriesFromTextFile(String title) throws FileNotFoundException, IOException {
    OpenDialog openDialog = new OpenDialog(title);

    FileReader fr = new FileReader(openDialog.getPath());
    BufferedReader br = new BufferedReader(fr);

    ArrayList<Double> data = new ArrayList<Double>();

    int nLine = 0;
    while (true) {
      String line = br.readLine();
      nLine++;
      if (null == line || line.length() == 0) {
        break;
      }

      // IJ.log(line);
      data.add(Double.parseDouble(line));
    }
    br.close();

    double[] returnData = new double[data.size()];
    for (int i=0; i<data.size(); i++) {
      returnData[i] = data.get(i);
    }

    return returnData;
  }

  /**
   * This method is concrete method of PlugInFilter class.
   * @see <a href="https://imagej.nih.gov/ij/developer/api/ij/plugin/filter/PlugInFilter.html">PlugInFilter (ImageJ API)</a>
   */
  @Override
  public int setup(String arg, ImagePlus imp) {
    this.imp = imp;
    // return DOES_32+CONVERT_TO_FLOAT;
    return DOES_32;
  }

  /**
   * This method is concrete method of PlugInFilter class.
   * @see <a href="https://imagej.nih.gov/ij/developer/api/ij/plugin/filter/PlugInFilter.html">PlugInFilter (ImageJ API)</a>
   */
  @Override
  public void run(ImageProcessor ip) {
  	// dialog
    GenericDialog gd = new GenericDialog("Set Option");
    String[] imageTitles = WindowManager.getImageTitles();

    gd.addNumericField("alpha ", 10, 0);
    gd.addCheckbox("using mask ", true);
    gd.addChoice("mask ", imageTitles, imageTitles[0]);

    gd.showDialog();
    if (gd.wasCanceled()) {
      return;
    }

    // get & check
    int alpha = (int)gd.getNextNumber();
    boolean isUsingMask = gd.getNextBoolean();
    String selectMask = gd.getNextChoice();

    ImagePlus mask = null;
    ImageProcessor maskPro = null;

    if (isUsingMask) {
      mask = WindowManager.getImage(selectMask);
      maskPro = mask.getProcessor();

      if (!maskPro.isBinary()) {
        IJ.error("Mask image must be 8-bit binary image (0 and 255)");
        return;
      }

      if (mask.getWidth() != ip.getWidth() || mask.getHeight() != ip.getHeight()) {
        IJ.error("Both images must have the same size");
        return;
      }
    }

    // sequence load
    double[] sequence;
    try {
      sequence = readSeriesFromTextFile("Open Sequence");
    } catch (FileNotFoundException e) {
      IJ.error("" + e);
      return;
    } catch (IOException e) {
      IJ.error("" + e);
      return;
    }

    // for (int i=0; i<sequence.length; i++) {
    //   IJ.log(i + ": " + sequence[i]);
    // }

    // write
    int pointer = 0;
    if (isUsingMask) {
      for (int y=0; y<ip.getHeight(); y++) {
        for (int x=0; x<ip.getWidth(); x++) {
          if (maskPro.get(x, y) != 0) {
            ip.setf(x, y, ip.getf(x, y) + alpha*(float)sequence[pointer++]);
            if (pointer >= sequence.length) {
              return;
            }
          }
        }
      }
    } else {
      for (int y=0; y<ip.getHeight(); y++) {
        for (int x=0; x<ip.getWidth(); x++) {
          ip.setf(x, y, ip.getf(x, y) + alpha*(float)sequence[pointer++]);
          if (pointer >= sequence.length) {
            return;
          }
        }
      }
    }
  }
}
