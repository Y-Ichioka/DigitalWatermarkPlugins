import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;

import ij.io.SaveDialog;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This plugin convert a binary image (bit plane) to file.
 * A binary image are requested, when you run this.
 * In the case of white pixel set to 0, the case of black pixel set to 1.
 * This is the inverse process of "File to BitPlanes".
 * @author <a href="https://github.com/Y-Ichioka">Y-Ichioka</a>
 * @version 0.1
 * @see <a href="https://en.wikipedia.org/wiki/Bit_plane">Bit plane</a>
 * @see <a href="https://imagej.nih.gov/ij/developer/api/ij/plugin/PlugIn.html">PlugIn (ImageJ API)</a>
 */
public class BitPlane_to_File implements PlugIn {

  private Byte[] getInfo(ImageProcessor imp) {
    int w = imp.getWidth();
    int h = imp.getHeight();

    int endPoint = w*h;
    boolean isEnd = false;

    // search end
    for (int y=h-1; y>=0; y--) {
      for (int x=w-1; x>=0; x--) {
        // IJ.log(x + "," + y + ": " + (imp.getPixel(x, y)[0] < 128) );
        isEnd = imp.get(x, y) == 0;
        // if (imp.getPixel(x, y)[0] < 128) {
        //   isEnd = true;
        // }

        if (isEnd) {
          break;
        } else {
          endPoint--;
        }
      }
      if (isEnd) {
        break;
      }
    }
    // IJ.log("" + endPoint);

    if (endPoint%8 != 0) {
      endPoint = endPoint + (8-endPoint%8);
    }

    Byte data[] = new Byte[(int)Math.ceil((double)endPoint/8)];
    // IJ.log("" + data.length);

    for (int i=0; i<data.length; i++) {
      byte tmp = 0x00;

      for (int j=0; j<8; j++) {
        int p = i*8+j;
        int x = p%w;
        int y = (int)Math.floor((double)p/w);

        if (imp.get(x, y) == 0) {
          tmp |= 0x01 << (7-j);
        }
      }

      data[i] = tmp;
    }

    // check
    // for (int i=0; i<data.length; i++) {
    //   IJ.log("" + Integer.toHexString(data[i]) );
    // }

    return data;

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

    ImagePlus impl =  IJ.getImage();
    ImageProcessor impr =  IJ.getProcessor();

    if (impl.getType() != ImagePlus.GRAY8) {
      IJ.error("8-bit binary image (0 and 255) required.");
      return;
    }

    if (!impr.isBinary()) {
      IJ.error("8-bit binary image (0 and 255) required.");
      return;
    }

    Byte[] data = getInfo(impr);

    SaveDialog saveDialog = new SaveDialog("Save", impl.getTitle(), ".bin");

    try {
      FileOutputStream output = new FileOutputStream(saveDialog.getDirectory() + saveDialog.getFileName());
      for (int i=0; i<data.length; i++) {
        output.write(data[i]);
      }
      output.close();
    } catch (FileNotFoundException e) {
      IJ.error("" + e);
      return;
    } catch (IOException e) {
      IJ.error("" + e);
      return;
    }

  }

}
