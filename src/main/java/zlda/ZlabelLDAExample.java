package zlda;
import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author vutm
 *
 */
public class ZlabelLDAExample {

  public static void main(String[] args) {
    /*List<String> linez=FileHelper.readFileAsList(new File("data/stoplist.txt"), Charset.forName("utf-8"));
    HashSet<String> hs = new HashSet<String>();
    for(String line: linez){
      hs.add(line.split(" ")[0]);
    }
    String[] lines=FileHelper.readFileAsLines(new File("data/stop.txt"), Charset.forName("utf-8"));
    List<String> r = new ArrayList<String>();
    int ct=0;
    for(int i=0; i<lines.length; i++){
      ct++; if (ct%1000==0) System.out.println(ct);
      lines[i]=lines[i].split(" ")[0];
      if (lines[i].length()>1 && !hs.contains(lines[i]) && lines[i].matches("^[a-zA-Z_\\p{L}\\s]*$")){
        lines[i]=lines[i].toLowerCase();
        r.add(lines[i]);
      } else lines[i]="";
    }

    FileHelper.writeListToFile(r,"stop1.txt");
*/
    ZlabelTopicModelAnalysis zlda = new ZlabelTopicModelAnalysis();
    try {
      Date dateObj = new Date();
      zlda.invokeLDA("data/input/", "data/seedlist2.txt", 45, "data/output/", dateObj);
      Date endTime = new Date();
      Long runTime = endTime.getTime() - dateObj.getTime();
      System.out.println(dateObj+"\t"+endTime+"\t"+runTime);
    } catch (Exception e) {
    }
  }
}
