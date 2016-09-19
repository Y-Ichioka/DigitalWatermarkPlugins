import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;

import ij.io.SaveDialog;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * This plugin read the pixel values of the 32bit of the image, and save it to a file.
 * If you specify a mask image, only if the pixel values of the mask image is not 0, to get the pixel of 32bit image.
 * @author <a href="https://github.com/Y-Ichioka">Y-Ichioka</a>
 * @version 0.1
 * @see <a href="https://imagej.nih.gov/ij/developer/api/ij/plugin/PlugIn.html">PlugIn (ImageJ API)</a>
 */
public class Read_32bit_Image implements PlugIn {

  private double[] read32bitImage(ImageProcessor image, ImageProcessor mask) {
    FloatProcessor imageFlo = image.convertToFloatProcessor();

    ArrayList<Double> data = new ArrayList<Double>();

    int pointer = 0;
    for (int y=0; y<imageFlo.getHeight(); y++) {
      for (int x=0; x<imageFlo.getWidth(); x++) {
        if (mask.get(x, y) != 0) {
          data.add((double)imageFlo.getf(x, y));
        }
      }
    }

    double[] returnData = new double[data.size()];
    for (int i=0; i<data.size(); i++) {
      returnData[i] = data.get(i);
    }

    return returnData;
  }

  private double[] read32bitImage(ImageProcessor image) {
    FloatProcessor imageFlo = image.convertToFloatProcessor();

    double[] data = new double[imageFlo.getWidth() * imageFlo.getHeight()];

    int pointer = 0;
    for (int y=0; y<imageFlo.getHeight(); y++) {
      for (int x=0; x<imageFlo.getWidth(); x++) {
        data[pointer++] = (double)imageFlo.getf(x, y);
      }
    }

    return data;
  }

  private void saveText(String title, double[] data) throws IOException {
    SaveDialog saveDialog = new SaveDialog("Save", title, ".txt");

    File file = new File(saveDialog.getDirectory() + saveDialog.getFileName());
    FileWriter filewriter = new FileWriter(file);

    for (int i=0; i<data.length; i++) {
      filewriter.write(String.valueOf(data[i]) + "\n");
      if (i%10000 == 0) {
        IJ.log((double)i/data.length + "%");
      }
    }
    IJ.log("100%");

    // ProgressBar pb = new ProgressBar(400, 100);
    // for (int i=0; i<data.length; i++) {
    //   filewriter.write(String.valueOf(data[i]));
    //   pb.show((double)i/data.length);
    // }

    filewriter.close();
  }


  /**
   * This method is concrete method of PlugIn class.
   * It's executed at first.
   * @see <a href="https://imagej.nih.gov/ij/developer/api/ij/plugin/PlugIn.html">PlugIn (ImageJ API)</a>
   */
  @Override
  public void run(String arg) {
    // input
    String[] imageTitles = WindowManager.getImageTitles();
    if (imageTitles.length == 0) {
      IJ.error("There are no images open.");
      return;
    }

    GenericDialog gd = new GenericDialog("Set Option");
    gd.addStringField("title ", "image data");
    gd.addChoice("image ", imageTitles, imageTitles[0]);
    gd.addCheckbox("using mask ", true);
    gd.addChoice("mask ", imageTitles, imageTitles[0]);

    gd.showDialog();
    if (gd.wasCanceled()) {
      return;
    }

    // get & check
    String title = gd.getNextString();
    String selectImage = gd.getNextChoice();
    boolean isUsingMask = gd.getNextBoolean();
    String selectMask = gd.getNextChoice();

    ImagePlus image = WindowManager.getImage(selectImage);
    ImageProcessor imagePro = image.getProcessor();

    if (image.getType() != ImagePlus.GRAY32) {
      IJ.error("Mask image must be 32-bit gray scale image");
    }

    ImagePlus mask = null;
    ImageProcessor maskPro = null;

    if (isUsingMask) {
      mask = WindowManager.getImage(selectMask);
      maskPro = mask.getProcessor();

      if (!maskPro.isBinary()) {
        IJ.error("Mask image must be 8-bit binary image (0 and 255)");
        return;
      }

      if (mask.getWidth() != image.getWidth() || mask.getHeight() != image.getHeight()) {
        IJ.error("Both images must have the same size");
        return;
      }
    }

    // read
    IJ.log("reading image...");
    double[] outDouble;
    if (isUsingMask) {
      outDouble = read32bitImage(imagePro, maskPro);
    } else {
      outDouble = read32bitImage(imagePro);
    }

    // out
    IJ.log("saving image...");
    try {
      saveText("image data", outDouble);
    } catch (IOException e) {
      IJ.error("" + e);
      return;
    }


  }

}
