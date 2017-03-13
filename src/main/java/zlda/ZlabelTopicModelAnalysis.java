package zlda;

/**
 * @author vutm
 *
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ZlabelTopicModelAnalysis {
  public ZlabelTopicModelAnalysis() {}

  private void runLDA(File dir, File preSeedFile, int numTopics, int noOfSamples, double alphaval,
      double betaval, double confidenceValue, String outputdir, Date dateObj) {

    DateFormat df = new SimpleDateFormat("yy-dd-MM_HH-mm-ss");
    File[] listOfFiles = dir.listFiles();
    List<File> inputFiles = new ArrayList<File>();
    for (File f : listOfFiles) {
      if (f.getAbsolutePath().contains("DS_Store"))
        continue;
      inputFiles.add(f);
    }

    // tinh toan khoi tao tham so tu input
    DTWC dtwc = new DTWC(inputFiles, preSeedFile);
    dtwc.computeDocumentVectors();

    int[][][] zlabels = dtwc.getTopicSeedsAsInt();
    int[][] docs = dtwc.getDocVectorsAsInt();

    int T = numTopics;
    int W = dtwc.getVocabSize();

    double[][] alpha = new double[1][T];
    for (int i = 0; i < T; i++) {
      alpha[0][i] = alphaval;
    }

    double[][] beta = new double[T][W];
    for (int i = 0; i < T; i++) {
      for (int j = 0; j < W; j++) {
        beta[i][j] = betaval;
      }
    }

    ZlabelLDA zelda = new ZlabelLDA(docs, zlabels, confidenceValue, alpha, beta, noOfSamples);
    boolean retVal = zelda.zLDA();
    if (!retVal) {
      System.out
          .println("Sorry, something is wrong with the input - please check format and try again");
      return;
    }
    double[][] theta, phi;

    theta = zelda.getTheta();
    phi = zelda.getPhi();

    Map<String, Integer> dictionary = dtwc.getTermIndex();
    Map<Integer, String> revDict = dtwc.getIndexTerm();
    List<List<Map.Entry<String, Double>>> topicWords =
        new ArrayList<List<Map.Entry<String, Double>>>();
    for (int i = 0; i < T; i++) {
      topicWords.add(new ArrayList<Map.Entry<String, Double>>());
    }

    for (int i = 0; i < T; i++) {
      for (int j = 0; j < W; j++) {
        if (phi[i][j] > Float.MIN_VALUE) {
          topicWords.get(i).add(
              new AbstractMap.SimpleEntry<String, Double>(revDict.get(j), new Double(phi[i][j])));
        }
      }
    }

    System.out.println("\nTopic and its corresponding words and phi values stored in " + outputdir
        + File.separator + df.format(dateObj) + "_topicwords" + ".csv");
    try {
//      FileOutputStream fos = new FileOutputStream(new File(generateTopicWordsFileName(outputdir, df.format(dateObj))));
//      OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
      FileWriter fw =
          new FileWriter(new File(generateTopicWordsFileName(outputdir, df.format(dateObj))));
      StringBuilder row = new StringBuilder();
      StringBuilder rowValues = new StringBuilder();
      for (int i = 0; i < T; i++) {
        row.append("Topic" + i + ", ,");
        rowValues.append("Word,Probability,");
      }
      // adding headers
//      osw.write(row.toString() + "\n");
//      osw.write(rowValues.toString() + "\n");
      fw.write(row.toString() + "\n");
      fw.write(rowValues.toString() + "\n");

      for (int i = 0; i < T; i++) {
        // fw.write("Topic" + i + ","+"");
        Collections.sort(topicWords.get(i), new Comparator<Map.Entry<String, Double>>() {
//          @Override
          public int compare(Entry<String, Double> arg0, Entry<String, Double> arg1) {
            return -(arg0.getValue()).compareTo(arg1.getValue());
          }
        });

      }
      for (int k = 0; k < 50; k++) {

        row = new StringBuilder();
        for (int i = 0; i < T; i++) {

          if (topicWords.get(i).get(k).getKey() != null) {
            row.append(topicWords.get(i).get(k).getKey() + "," + topicWords.get(i).get(k).getValue()
                + ",");
          } else {
            row.append(" , ,");
          }
        }
//        osw.write(row.toString() + "\n");
//        osw.flush();
        fw.write(row.toString() + "\n");
        fw.flush();
      }
      fw.flush();
      fw.close();
//      osw.flush();
//      osw.close();

      
      fw = new FileWriter(new File(generateWordsInTopicFileName(outputdir, df.format(dateObj))));
      fw.write("Topic/Words,");
      for (int j = 0; j < revDict.size(); j++)
        fw.write(revDict.get(j) + ",");
      fw.write("\n");
      for (int i = 0; i < T; i++) {

        fw.write("Topic" + i + ",");

        for (int j = 0; j < phi[i].length; j++) {

          if (phi[i][j] > Float.MIN_VALUE) {
            fw.write(phi[i][j] + ",");
          } else {
            fw.write("0,");
          }
        }
        fw.write("\n");
        fw.flush();
      }
      fw.flush();
      fw.close();

      fw = new FileWriter(
          new File(generateTopicsPerDocumentFileName(outputdir, df.format(dateObj))));
      fw.write("Document/Topic,");
      for (int i = 0; i < T; i++) {
        fw.write("Topic" + i + ",");
      }
      fw.write("\n");
      for (int i = 0; i < docs.length; i++) {

        fw.write("Document" + i + ",");
        for (int j = 0; j < theta[i].length; j++) {
          fw.write(theta[i][j] + ",");
        }
        fw.write("\n");
        fw.flush();
      }
      fw.flush();
      fw.close();
    } catch (Exception e) {
    }
  }

  protected String generateTopicWordsFileName(String outputdir, String date) {
    return outputdir + File.separator + date + "_topicwords" + ".csv";
  }

  protected String generateTopicsPerDocumentFileName(String outputdir, String date) {
    return outputdir + File.separator + date + "_topicsPerDocument(theta)" + ".csv";
  }

  protected String generateWordsInTopicFileName(String outputdir, String date) {
    return outputdir + File.separator + date + "_wordsinTopics(Phi)" + ".csv";
  }

  public void invokeLDA(String inputDir, String seedFileName, int numTopics, String outputDir,
      Date dateObj) {
    File dir = new File(inputDir);

    File seedFile = new File(seedFileName);

    double alphaval = 0.5;
    double betaval = 0.1;
    int noOfSamples = 1;
    double confidenceValue = 1;

    runLDA(dir, seedFile, numTopics, noOfSamples, alphaval, betaval, confidenceValue, outputDir,
        dateObj);

  }
}
