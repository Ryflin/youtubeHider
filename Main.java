import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import javax.naming.directory.SearchResult;


public final class Main {
  public static final int MaxVids = 10;

  private static void createHtmlPage() {
    String fileInput = "<!DOCTYPE html>\n" + //
        "\n" + //
        "<head>\n" + //
        "  <title>\n" + //
        "    MY YOUTUBE\n" + //
        "  </title>\n" + //
        "  <meta charset=\"utf-8\">\n" + //
        "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" + //
        "  <link rel=\"stylesheet\" type=\"text/css\" href=\"../css/main.css\">\n" + //
        "  <script src=\"../javascript/youtubeSearchEngine.js\"></script>\n" + //
        "  \n" + //
        "</head>\n" + //
        "\n" + //
        "<body>";
    Scanner in = new Scanner(System.in);
    System.out.print("What would you like to search? ");
    String input = in.nextLine();
    String responses = serverRequest(input);
    String[] ytId = getYoutubeIds(responses);
    System.out.println();
    ytId = removeDuplicates(ytId);
    for (int i = 0; i < ytId.length && i < MaxVids; i++){
        fileInput += "<iframe src=\"" + ytId[i] + "\"  style=\"margin: auto; position: flex;\" allowfullscreen=\"\" scrolling=\"no\" allow=\"encrypted-media\"></iframe>";
    }
    in.close();
    writeToPage(fileInput);
  }

  private static String[] getYoutubeIds(String responses) {
     String[] ytId = new String[100];
      //Regular expression pattern to match YouTube video IDs
      String pattern = "(v=|v/|vi=|vi/|youtu.be/)([a-zA-Z0-9_-]{11})";
      Pattern compiledPattern = Pattern.compile(pattern);

      // Use Matcher to find all matches in the HTML content
      Matcher matcher = compiledPattern.matcher(responses);
      int j = 0;
      while (j < 100 && matcher.find()) {
        
        ytId[j] = "https://www.youtube.com/embed/" + matcher.group(2);
        System.out.print(ytId[j] + " ");
        //System.out.println(responses[j]);
        j++;
        }
        return ytId;
      }

  private static String serverRequest(String Request) {
    try {
            YouTube youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, null)
                    .setApplicationName("YouTubeSearch")
                    .build();

            String apiKey = "YOUR_API_KEY"; // Replace with your API key
            String query = "dogs";
            long maxResults = 25;

            YouTube.Search.List search = youtube.search().list("id,snippet");
            search.setKey(apiKey);
            search.setQ(query);
            search.setMaxResults(maxResults);

            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResults = searchResponse.getItems();

            for (SearchResult searchResult : searchResults) {
                System.out.println("Video ID: " + searchResult.getId().getVideoId());
                System.out.println("Title: " + searchResult.getSnippet().getTitle());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
      }
  private static String serverRequest2(String Request) {
    System.out.print("Request: ");
    // Create a neat value object to hold the URL
    String htmlContent = "";
    try {
      System.out.println("https://www.youtube.com/results?search_query=" + Request);
      HttpURLConnection connection = (HttpURLConnection) new URL("https://www.youtube.com/results?search_query=" + Request).openConnection();
      InputStream inputStream = connection.getInputStream();
      try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A")) {
        htmlContent = scanner.hasNext() ? scanner.next() : "";
      }
    } catch (IOException e) {
      System.out.println("Yeah, the server hates me some times. Ignore or try again.");
      e.printStackTrace();
    }

    // Manually converting the response body InputStream to APOD using Jackson
    // Finally we have the response
    return htmlContent;
  }
  private static String[] removeDuplicates(String[] ids){
    String[] temp = new String[ids.length];
    int k = 0;
    for (int i = 0; i < ids.length - 1; i++) {
      boolean unique = true;
      for (int j = i + 1; j < ids.length - 1; j++) {
        if (ids[i].equals(ids[j])) {
          unique = false;
        }
      }
      if (unique) {
        temp[k] = ids[i];
        k++;
      }
    }
    for (int i = 0; i < ids.length; i++) {
      System.out.print(ids[i]);
    }
    return ids;
  }
  private static void writeToPage(String fileInput) {
    try {
      File file = new File("html/index.html");
      file.createNewFile();
    } catch (IOException e) {
      System.err.println("Yeah, I don't know why that happened. The file failed to write properly.");
      e.printStackTrace();
    }
    try {
      FileWriter writer = new FileWriter("index.html");
      writer.write(fileInput);
      writer.close();
    } catch (IOException e) {
      System.err.println("Yeah, I don't know why that happened. The file failed to write properly.");
      e.printStackTrace();
    }
  }
  public static void main(String[] args) {
    createHtmlPage();
  }
}