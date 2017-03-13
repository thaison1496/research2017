package crawler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import util.FileHelper;
import util.LanguageDetectorSingleton;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.Post;


public class getPostfromPage implements Runnable {
  private File file;
  private ConcurrentLinkedQueue<String> idQueue;
  private ConcurrentLinkedQueue<String> accessTokensqueue;
  LanguageDetectorSingleton languageDetector;

  public static void main(String args[]) throws Exception {
    List<String> pageIDs =
        FileHelper.readFileAsList(new File("data/2trieupage.txt"), Charset.forName("UTF-8"));
    ConcurrentLinkedQueue<String> idQueue = new ConcurrentLinkedQueue<String>();
    idQueue.addAll(pageIDs);
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    for (int i = 0; i < 21; i++) {
      try {
        getPostfromPage fetcher = new getPostfromPage(idQueue, i);
        executor.execute(fetcher);
      } catch (Exception e) {
      }
    }
  }

  public getPostfromPage(ConcurrentLinkedQueue<String> idQueue, int i) throws Exception {
    languageDetector = new LanguageDetectorSingleton();
    file = new File("data/PostsFromPagesVN1/posts_" + i + ".txt");
    this.idQueue = idQueue;
    List<String> accessTokenList =
        FileHelper.readFileAsList(new File("data/UserAT.txt"), Charset.defaultCharset());
    accessTokensqueue = new ConcurrentLinkedQueue<String>();
    accessTokensqueue.addAll(accessTokenList);
  }

  public void run() {
    FacebookClient client =
        new DefaultFacebookClient(accessTokensqueue.poll(), Version.VERSION_2_0);
    while (!idQueue.isEmpty()) {
      try {
        String line = idQueue.poll();
        String pageID = line.substring(0, line.indexOf('\t'));
        Connection<Post> posts = client.fetchConnection(pageID + "/feed", Post.class,
            Parameter.with("fields", "message"), Parameter.with("limit", "10"));
        System.out.println(pageID);
        for (Post post : posts.getData()) {
          try {
            String message = post.getMessage();
            if (languageDetector.detect(message).equals("vi")) {
              message = message.replaceAll("\r", " ").replaceAll("\n", " ");
              FileHelper.appendToFile(post.getId() + "\t" + message + "\n", file,
                  Charset.forName("UTF-8"));
              System.out.println(post.getId() + "\t\t\t" + message);
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
