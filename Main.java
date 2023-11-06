import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Desktop;

public final class Main {
  public static final int MaxVids = 10;
  private static void makeHtml(String request) {
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
        "  <style>\n" + //
        "    .youtube-player{\n" + //
        "      border: 1px solid #1D740C;\n" + //
        "      height: 390px;\n" + //
        "      width: 480px;\n" + //
        "      padding: 0px;\n" + //
        "      margin-left: 25px;\n" + //
        "      margin-bottom: 25px;\n" + //
        "      }\n" + //
        "  </style>\n" + //
        "  \n" + //
        "</head>";
    System.out.print(request);
    String responses = serverRequest2(request);
    // System.out.println(responses);
    String[] ytId = getYoutubeIds(responses);
    // System.out.println();
    String[] newytId = removeDuplicates(ytId);
    for (int i = 0; i < newytId.length && i < MaxVids; i++) {
      System.out.println(newytId[i]);
      fileInput += "<iframe src=\"" + newytId[i]
          + "\" class=\"youtube-player\" allowfullscreen=\"\" scrolling=\"no\" allow=\"encrypted-media\"></iframe>";
    }
    File file = new File("html/index.html");
    try {
      file.createNewFile();
      FileWriter writer = new FileWriter("html/index.html");
      writer.write(fileInput);
      Desktop.getDesktop().browse(file.toURI());
      writer.close();

    } catch (IOException e) {
      System.err.println("Yeah, I don't know why that happened. The file failed to write properly.");
      e.printStackTrace();
    }
    try {
      Desktop.getDesktop().browse(file.toURI());
    } catch (IOException e) {
      System.out.println("So... kinda awkward but I can't seemm to open the file I just wrote to...");
      e.printStackTrace();
    }
  }
  private static String[] getYoutubeIds(String responses) {
    String[] ytId = new String[100];
    // Regular expression pattern to match YouTube video IDs
    String pattern = "\"videoId\":\"([a-zA-Z0-9_-]{11})\"";
    Pattern compiledPattern = Pattern.compile(pattern);

    // Use Matcher to find all matches in the HTML content
    Matcher matcher = compiledPattern.matcher(responses);
    int j = 0;
    while (j < 100 && matcher.find()) {
      ytId[j] = "https://www.youtube.com/embed/" + matcher.group(1);
      // System.out.print(ytId[j] + " ");
      // System.out.println(responses[j]);
      j++;
    }
    return ytId;
  }

  /*
   * private static String serverRequest(String Request) {
   * try {
   * YouTube youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY,
   * null)
   * .setApplicationName("YouTubeSearch")
   * .build();
   * 
   * String apiKey = "YOUR_API_KEY"; // Replace with your API key
   * String query = "dogs";
   * long maxResults = 25;
   * 
   * YouTube.Search.List search = youtube.search().list("id,snippet");
   * search.setKey(apiKey);
   * search.setQ(query);
   * search.setMaxResults(maxResults);
   * 
   * SearchListResponse searchResponse = search.execute();
   * List<SearchResult> searchResults = searchResponse.getItems();
   * 
   * for (SearchResult searchResult : searchResults) {
   * System.out.println("Video ID: " + searchResult.getId().getVideoId());
   * System.out.println("Title: " + searchResult.getSnippet().getTitle());
   * }
   * } catch (IOException e) {
   * e.printStackTrace();
   * }
   * return "";
   * }
   */
  private static String serverRequest2(String Request) {
    System.out.print("Request: ");
    String newRequest = Request.replace(' ', '+');
    // Create a neat value object to hold the URL
    String htmlContent = "";
    try {
      // System.out.println("https://www.youtube.com/results?search_query=" +
      // Request);
      HttpURLConnection connection = (HttpURLConnection) new URL(
          "https://www.youtube.com/results?search_query=" + newRequest).openConnection();
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

  static String[] removeDuplicates(String str[]) {
    System.out.println(str.length);
    String[] str2 = new String[str.length];
    int k = 0;
    String uniVal = "";
    for (int i = 0; i < str.length; i++) {
      if (!uniVal.equals(str[i])) {
        uniVal = str[i];
        str2[k] = str[i];
        k++;
      }
    }
    return str2;
  }

  /*
   * private static String processRequest2 (String request) {
   * String response = "";
   * try {
   * HttpURLConnection connection = (HttpURLConnection) new
   * URL("https://www.youtube.com/").openConnection();
   * InputStream straem = connection.getInputStream();
   * try (Scanner scanner = new Scanner(straem,
   * StandardCharsets.UTF_8).useDelimiter("\\A")) {
   * response = scanner.hasNext() ? scanner.next() : "";
   * }
   * } catch (IOException e) {
   * System.out.println("Yeah not a lot going on here");
   * e.printStackTrace();
   * }
   * return response;
   * This one was done for my learning
   * }
   */
  public static void main(String[] args) {

    Scanner in = new Scanner(System.in);
    System.out.print("What would you like to search? ");
    String input = in.nextLine();

    if (input != null && input.charAt(0) == '/') {
      makeHtml(input);
    } else {

    }
  }
}