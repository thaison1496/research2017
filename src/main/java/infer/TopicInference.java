package infer;

import java.io.File;
import java.nio.DoubleBuffer;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import util.FileHelper;


// one class needs to have a main() method
public class TopicInference
{
  private Map<String,Double[]> mp = new HashMap<String,Double[]>();
  private String[] topicName;
  private String[] words;

  private int numTopic;
  private int numWord;

  TopicInference(String filename){
    String[] lines = FileHelper.readFileAsLines(new File(filename), Charset.forName("utf-8"));
    numTopic = lines.length - 1;
    topicName = new String[numTopic];
    String[] tmp = lines[0].split("[ ,]");
    numWord = tmp.length-1;
    System.out.println("word: " + numWord + " topics: " + numTopic);
    words = new String[numWord];
    for(int i=0; i<numWord; i++){
      words[i]=tmp[i+1];
      mp.put(words[i], new Double[numTopic]);
      //System.out.println(words[i]);
    }

    for(int i=1; i<=numTopic; i++){
      tmp = lines[i].split("[ ,]");
      topicName[i-1]=tmp[0];
      for(int j=1; j<=numWord; j++){
        mp.get(words[j-1])[i-1] = Double.parseDouble(tmp[j]);
      }
    }

    //System.out.println(mp.get("490k")[0]);

  }
  List<String> getTopic(String sentence,double cutoff){
    String[] tokens = sentence.toLowerCase().split(" ");
    double[] pTopics = new double[numTopic];
    Double[] pTmp;
    String token;
    
    int cword=0;
    double sum=0;
    for(int j=0; j<numTopic; j++) pTopics[j]=0;    
    
    for(int i=0; i<tokens.length; i++){
      token=tokens[i];
      if (mp.containsKey(token)){
        cword++;
        pTmp = mp.get(token);
    	for(int j=0; j<numTopic; j++) {pTopics[j]+=pTmp[j]; sum+=pTmp[j];}
      }
    }
    List<String> r = new ArrayList<String>();
    NumberFormat formatter = new DecimalFormat("#0.00000");

    for(int j=0; j<numTopic; j++) {
      pTopics[j] = pTopics[j]/sum;
      if (pTopics[j]>=cutoff){
        //System.out.println(topicName[j]+" "+pTopics[j]);
        r.add(topicName[j]+" "+formatter.format(pTopics[j]));
      }
    }
    Collections.sort(r,new CustomComparator());
    return r;
  }
  public class CustomComparator implements Comparator<String> {
    public int compare(String s1, String s2) {
      //System.out.println(Double.parseDouble(s1.split(" ")[1]));
      Double f = Double.parseDouble(s1.split(" ")[1]) - Double.parseDouble(s2.split(" ")[1]);
      return (f>0?-1:1);
      //return -1;
    }
  }
}

