import java.io.File;
import java.io.IOException;

public class testingStuff {
  public static void main(String[] args) {
    try {
      File file = new File("html/index.html");
      file.createNewFile();
    } catch (IOException e) {
      System.err.println("Yeah, I don't know why that happened. The file failed to write properly.");
      e.printStackTrace();
    }
  }
}
