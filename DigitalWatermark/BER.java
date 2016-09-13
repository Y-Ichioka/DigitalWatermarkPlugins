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

/**
 * This plugin calculate and display BER (bit error rate) index between two files.
 * Two files are requested, when you run this.
 * They must be the same size.
 * @author <a href="https://github.com/Y-Ichioka">Y-Ichioka</a>
 * @version 0.1
 * @see <a href="https://en.wikipedia.org/wiki/Bit_error_rate">Bit error rate</a>
 * @see <a href="https://imagej.nih.gov/ij/developer/api/ij/plugin/PlugIn.html">PlugIn (ImageJ API)</a>
 */
public class BER implements PlugIn {

  private double getBER(byte[] data1, byte[] data2) {
    int count = 0;
    int numOfBits = data1.length * 8;

    for (int i=0; i<data1.length; i++) {
      byte base = 0x01;
      for (int j=0; j<8; j++) {
        if ( ( (base << j) & data1[i] ) != ( (base << j) & data2[i] ) ) {
          count++;
        }
      }     
    }

    IJ.log("Bits: " + numOfBits);
    IJ.log("Error Bits: " + count);

    return (double)count/(double)numOfBits;
  }

  private byte[] openFile() throws FileNotFoundException, IOException {
    // file open
    OpenDialog openDialog = new OpenDialog("Open");
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
      throw e;
    } catch (IOException e) {
      IJ.error("" + e);
      throw e;
    }

    return readBinary; 
  }

  /**
   * This method is concrete method of PlugIn class.
   * It's executed at first.
   * @see <a href="https://imagej.nih.gov/ij/developer/api/ij/plugin/PlugIn.html">PlugIn (ImageJ API)</a>
   */
  @Override
  public void run(String arg) {
    byte[] data1;
    byte[] data2;

    try {
      data1 = openFile();
      data2 = openFile();
    } catch (FileNotFoundException e) {
      IJ.error("" + e);
      return;
    } catch (IOException e) {
      IJ.error("" + e);
      return;
    }
    
    if (data1.length != data2.length) {
      IJ.error("Both files must have the same file size");
      return;
    }

    IJ.log("BER: " + getBER(data1, data2));
  }

}
