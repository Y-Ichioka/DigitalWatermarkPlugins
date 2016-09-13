import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;

import ij.io.SaveDialog;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This plugin convert image to file.
 * An image is requested, when you run this.
 * This is the inverse process of "File to Image".
 * @author <a href="https://github.com/Y-Ichioka">Y-Ichioka</a>
 * @version 0.1
 * @see <a href="https://imagej.nih.gov/ij/developer/api/ij/plugin/PlugIn.html">PlugIn (ImageJ API)</a>
 */
public class Image_to_File implements PlugIn {

  private ArrayList<Byte> getInfo(ImagePlus imp) {
    int w = imp.getWidth();
    int h = imp.getHeight();

    int endPoint = w*h;
    boolean isEnd = false;

    // search end
    for (int y=h-1; y>=0; y--) {
      for (int x=w-1; x>=0; x--) {
        // IJ.log("x,y: " + x + "," + y);

        for (int i=0; i<3; i++) {
          if (imp.getPixel(x, y)[i] != 0xff) {
            isEnd = true;
          }
        }

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

    // get data
    ArrayList<Byte> data = new ArrayList<Byte>();
    for (int i=0; i<endPoint; i++) {
      int x = i%w;
      int y = (int)((double)i/h);

      for (int j=0; j<3; j++) {
        data.add((byte)imp.getPixel(x, y)[j]);
        // IJ.log("" + Integer.toHexString(imp.getPixel(x, y)[j]) );
      }
    }

    // remove last 0
    int size = data.size();
    for (int i=0; i<3; i++) {
      if (data.get(size-1-i) == 0x00) {
        data.remove(size-1-i);
      }
    }

    // check
    // for (int i=0; i<data.size(); i++) {
    //   IJ.log("" +  Integer.toHexString(data.get(i)) );
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

    ImagePlus imp = IJ.getImage();

    if (imp.getType() != imp.COLOR_RGB) {
      IJ.error("\"Divide BitPlane GrayScale\" requires an image of type:\n32-bit color");
      return;
    }

    // extract
    ArrayList<Byte> data = getInfo(imp);

    // save
    SaveDialog saveDialog = new SaveDialog("Save", imp.getTitle(), ".bin");
    // IJ.log(saveDialog.getDirectory() + saveDialog.getFileName());
    
    try {
      FileOutputStream output = new FileOutputStream(saveDialog.getDirectory() + saveDialog.getFileName());
      for (int i=0; i<data.size(); i++) {
        output.write(data.get(i));
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
