import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;

import java.security.SecureRandom;
import ij.io.SaveDialog;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import ij.io.OpenDialog;
import java.util.ArrayList;

/**
 * This plugin generate file is written direct sequence spread spectrum.
 * @author <a href="https://github.com/Y-Ichioka">Y-Ichioka</a>
 * @version 0.1
 * @see <a href="https://en.wikipedia.org/wiki/Direct-sequence_spread_spectrum">Direct-sequence spread spectrum</a>
 * @see <a href="https://imagej.nih.gov/ij/developer/api/ij/plugin/PlugIn.html">PlugIn (ImageJ API)</a>
 */
public class Spread_Spectrum_DS implements PlugIn {

  private Double[] readSeriesFromTextFile(String title) throws FileNotFoundException, IOException {
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

    Double[] returnData = new Double[data.size()];
    for (int i=0; i<data.size(); i++) {
      returnData[i] = data.get(i);
    }

    return returnData;
  }

  private void saveText(String title, String out) throws IOException {
    SaveDialog saveDialog = new SaveDialog("Save", title, ".txt");

    File file = new File(saveDialog.getDirectory() + saveDialog.getFileName());
    FileWriter filewriter = new FileWriter(file);

    filewriter.write(out);
    filewriter.close();
  }

  private Double[] getDirectSequence(Double[] data, Double[] series) {
    Double[] s = new Double[data.length*series.length];

    int pointer = 0;

    for (int i=0; i<data.length; i++) {
      for (int j=0; j<series.length; j++) {
        s[pointer++] = data[i] * series[j];
      }
    }

    return s;
  }

  /**
   * This method is concrete method of PlugIn class.
   * It's executed at first.
   * @see <a href="https://imagej.nih.gov/ij/developer/api/ij/plugin/PlugIn.html">PlugIn (ImageJ API)</a>
   */
  @Override
  public void run(String arg) {
    // input
    Double[] data;
    Double[] series;
    try {
      data = readSeriesFromTextFile("Open Data");
      series = readSeriesFromTextFile("Open Series");
    } catch (FileNotFoundException e) {
      IJ.error("" + e);
      return;
    } catch (IOException e) {
      IJ.error("" + e);
      return;
    }

    // calc
    Double[] outDouble = getDirectSequence(data, series);
    String outString = "";
    for (int i=0; i<outDouble.length; i++) {
      outString += String.valueOf(outDouble[i]) + "\n";
    }

    // output
    try {
      saveText("sequence", outString);
    } catch (IOException e) {
      IJ.error("" + e);
      return;
    }


  }

}
