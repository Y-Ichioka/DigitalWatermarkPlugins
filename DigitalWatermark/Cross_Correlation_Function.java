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
 * This plugin generate file is written Cross-correlation function of the input data.
 * When calculating the cross-correlation function, by circulating the first to the given data, and performs the computation.
 * @author <a href="https://github.com/Y-Ichioka">Y-Ichioka</a>
 * @version 0.1
 * @see <a href="https://imagej.nih.gov/ij/developer/api/ij/plugin/PlugIn.html">PlugIn (ImageJ API)</a>
 */
public class Cross_Correlation_Function implements PlugIn {

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

  private Double[] getCrossCorrelationFunction(Double[] series1, Double[] series2) {
    Double[] func = new Double[series1.length];

    int min = Math.min(series1.length, series2.length);

    for (int i=0; i<func.length; i++) {

      Double sum = 0.0;
      for (int j=0; j<min; j++) {
        sum += series1[(j+i)%series1.length] * series2[j];
      }

      func[i] = sum;
    }

    return func;
  }

  /**
   * This method is concrete method of PlugIn class.
   * It's executed at first.
   * @see <a href="https://imagej.nih.gov/ij/developer/api/ij/plugin/PlugIn.html">PlugIn (ImageJ API)</a>
   */
  @Override
  public void run(String arg) {
    // input
    Double[] series1;
    Double[] series2;
    try {
      series1 = readSeriesFromTextFile("Open Series1");
      series2 = readSeriesFromTextFile("Open Series2");
    } catch (FileNotFoundException e) {
      IJ.error("" + e);
      return;
    } catch (IOException e) {
      IJ.error("" + e);
      return;
    }

    // calc
    Double[] outDouble = getCrossCorrelationFunction(series1, series2);
    String outString = "";
    for (int i=0; i<outDouble.length; i++) {
      outString += String.valueOf(outDouble[i]) + "\n";
    }

    // output
    try {
      saveText("cross_correlation function", outString);
    } catch (IOException e) {
      IJ.error("" + e);
      return;
    }


  }

}
