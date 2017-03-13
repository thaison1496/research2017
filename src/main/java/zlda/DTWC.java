package zlda;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.*;

import util.FileHelper;

public class DTWC {

  private List<File> documents;

  private List<List<Integer>> docVectors;
  private int[][] docVectorsAsInt;

  private Map<String, Integer> termIndex;
  private Map<Integer, String> indexTerm;
  private Integer vocabSize;
  private File seedFile;
  private Map<String, Integer> termCount;
  private List<Map<Integer, List<Integer>>> topicSeeds;
  private int[][][] topicSeedsAsInt;

  private Map<Integer, Integer> seedWords;

  //private List<String> stopList;
  private HashSet<String> stopList;

  public Integer getVocabSize() {
    return vocabSize;
  }

  public Map<Integer, String> getIndexTerm() {
    return indexTerm;
  }

  public File getSeedFile() {
    return seedFile;
  }

  public void setSeedFile(File seedFile) {
    this.seedFile = seedFile;
  }

  public List<Map<Integer, List<Integer>>> getTopicSeeds() {
    return topicSeeds;
  }

  public List<List<Integer>> getDocVectors() {
    return docVectors;
  }

  public Map<String, Integer> getTermIndex() {
    return termIndex;
  }

  public int[][] getDocVectorsAsInt() {
    return docVectorsAsInt;
  }

  public int[][][] getTopicSeedsAsInt() {
    return topicSeedsAsInt;
  }

  private DTWC(List<File> docs) {
    documents = docs;
    vocabSize = 0;
    docVectors = new ArrayList<List<Integer>>();

    //stopList = new ArrayList<String>();
    stopList = new HashSet<String>();
    try {
      Scanner stopListSc = new Scanner(new File("data/stoplist.txt"));
      while (stopListSc.hasNextLine()) {
        String line = stopListSc.nextLine();
        stopList.add(line);
      }
    } catch (FileNotFoundException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    int cline=0;
    System.out.println("Start count line!");
    for (int i = 0; i < docs.size(); i++) {
      try {
        Scanner sc = new Scanner(docs.get(i));
        while (sc.hasNextLine()) {
          docVectors.add(new ArrayList<Integer>());
          //System.out.println(sc.nextLine());
          sc.nextLine();
          cline++; if (cline%10000==0) System.out.println(cline);
        }
//        for (int j =
//            0; j < FileHelper.readFileAsLines(docs.get(i), Charset.forName("UTF-8")).length; j++) {
//          docVectors.add(new ArrayList<Integer>());
//        }
//        System.out.println(FileHelper.readFileAsLines(docs.get(i), Charset.forName("UTF-8")).length);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    System.out.println("Done count line!");
    termIndex = new HashMap<String, Integer>();
    termCount = new HashMap<String, Integer>();
    initializeSeeds();

    indexTerm = new HashMap<Integer, String>();
  }

  public DTWC(List<File> docs, File seedFile) {
    this(docs);
    this.seedFile = seedFile;
  }

  private void initializeSeeds() {
    topicSeeds = new ArrayList<Map<Integer, List<Integer>>>();
    for (int i = 0; i < docVectors.size(); i++) {
      topicSeeds.add(new HashMap<Integer, List<Integer>>());
    }
    seedWords = new HashMap<Integer, Integer>();
  }

  private void calculateTermIndicesAndVectors() {
    String word;
    List<Integer> vector;
    for (int i = 0; i < documents.size(); i++) {

      vector = docVectors.get(i);
      try {
        String content = FileHelper.readFileAsString(documents.get(i), Charset.forName("UTF-8"));
        StringTokenizer tokenizer = new StringTokenizer(content);

        while (tokenizer.hasMoreTokens()) {
          // word = ptbtk.next().toString().toLowerCase();
          word = tokenizer.nextToken();
          word.replaceAll("[^a-zA-Z0-9-_]", " ");
          if (word.matches("^[a-zA-Z0-9]*$")) {
            if (termIndex.containsKey(word) == false) {
              termIndex.put(word, vocabSize);
              indexTerm.put(vocabSize, word);
              termCount.put(word, 1);
              vector.add(vocabSize);
              vocabSize++;
            } else {
              vector.add(termIndex.get(word));
              termCount.put(word, termCount.get(word) + 1);
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private boolean isStopWord(String word) {
    if (!word.matches(".*[^0-9].*") || word.length()==1 || !word.matches(".*[\\w].*"))
      return true;
    /*for (String stopWord : stopList) {
      if (word.equals(stopWord)) {
        return true;
      }
    }*/
    if (stopList.contains(word)) return true;
    return false;
  }

  private void calculateTermIndicesAndVectors1() {
    String word;
    
    int count = 0;
    int cline = 0;
    System.out.println("start reading documents!");
    for (int i = 0; i < documents.size(); i++) {
      try {
        String[] lines = FileHelper.readFileAsLines(documents.get(i), Charset.forName("UTF-8"));
        List<Integer> vector;
        for (String line : lines) {
          cline++; if (cline%10000==0) System.out.println(cline);
          line = line.toLowerCase();
          try{String line2 = line.substring(line.indexOf("facebook ")+9, line.length()); line = line2;}catch (Exception e){}
          //line = line.substring(line.indexOf("Facebook ")+9);
          //System.out.println(line);
          vector = docVectors.get(count);
          StringTokenizer tokenizer = new StringTokenizer(line);
          while (tokenizer.hasMoreTokens()) {
            // word = ptbtk.next().toString().toLowerCase();
            word = tokenizer.nextToken();
            //word.replaceAll("[^a-zA-Z0-9-_\\p{L}\\s]", " ");

            //if (word.matches("^[a-zA-Z0-9_^\\p{L}\\s]*$") && !isStopWord(word)) {
            if (word.matches("^[a-z_\\p{L}\\s]+$") && !isStopWord(word)) {
              //System.out.println(word);
              if (termIndex.containsKey(word) == false) {
                termIndex.put(word, vocabSize);
                indexTerm.put(vocabSize, word);
                termCount.put(word, 1);
                vector.add(vocabSize);
                vocabSize++;
              } else {
                vector.add(termIndex.get(word));
                termCount.put(word, termCount.get(word) + 1);
              }
            }
          }
          count++;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    System.out.println("done reading documents!");
  }

  public void computeDocumentVectors() {
    calculateTermIndicesAndVectors1();
    // calculateTermIndicesAndVectors();
    computeZSets();
    convertToPrimitiveDataTypes();
  }

  /* Computing z-sets for these documents */


  /* Read the seed words from the seed file */
  private void constructTopicList() {
    String line;
    String[] words;
    int topicNo = 0;
    try {

      BufferedReader br = new BufferedReader(new FileReader(seedFile));
      while ((line = br.readLine()) != null) {
        words = line.split(" ");
        for (int i = 0; i < words.length; i++) {
          if (termIndex.containsKey(words[i])) {
            seedWords.put(termIndex.get(words[i]), topicNo);
          }
        }
        topicNo++;
      }
      br.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void computeZSets() {
    constructTopicList();
    List<Integer> doc;
    Map<Integer, List<Integer>> docTopicSeeds;
    Integer word;
    List<Integer> wordTopicList;

    for (int i = 0; i < docVectors.size(); i++) {
      doc = docVectors.get(i);
      docTopicSeeds = topicSeeds.get(i);
      for (int j = 0; j < doc.size(); j++) {
        word = doc.get((int) j);
        if (termCount.get(indexTerm.get(word)) > 5) {
          wordTopicList = docTopicSeeds.get(j);
          if (seedWords.containsKey(word)) {
            if (wordTopicList == null) {
              wordTopicList = new ArrayList<Integer>();
              docTopicSeeds.put(j, wordTopicList);
            }
            wordTopicList.add(seedWords.get(word));
          }
        }
      }
    }
  }

  private void convertToPrimitiveDataTypes() {

    docVectorsAsInt = new int[docVectors.size()][];
    for (int i = 0; i < docVectors.size(); i++) {
      docVectorsAsInt[i] = new int[docVectors.get(i).size()];
      for (int j = 0; j < docVectors.get(i).size(); j++) {
        docVectorsAsInt[i][j] = docVectors.get(i).get(j);
      }
    }

    topicSeedsAsInt = new int[topicSeeds.size()][][];
    for (int i = 0; i < topicSeeds.size(); i++) {

      topicSeedsAsInt[i] = new int[docVectors.get(i).size()][];
      for (int j = 0; j < docVectors.get(i).size(); j++) {

        if (topicSeeds.get(i).get(j) != null) {
          topicSeedsAsInt[i][j] = new int[topicSeeds.get(i).get(j).size()];
          for (int k = 0; k < topicSeeds.get(i).get(j).size(); k++) {

            topicSeedsAsInt[i][j][k] = topicSeeds.get(i).get(j).get(k);
          }
        }
      }
    }
  }
}
