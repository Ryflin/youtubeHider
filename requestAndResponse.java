import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class requestAndResponse {

    private static int maxVids;
    private static String search;

    private static void makeHtml(String request) {
        String fileInput = "<!DOCTYPE html>\n" + //
                "<head>\n" + //
                "  <title>\n" + //
                "    FREE YOUTUBE\n" + //
                "  </title>\n" + //
                "  <meta charset=\"utf-8\">\n" + //
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
                + //
                "  <link rel=\"stylesheet\" type=\"text/css\" href=\"../css/main.css\">\n"
                + //
                "  <script src=\"../javascript/youtubeSearchEngine.js\"></script>\n"
                + //
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
                "</head>\n" + //
                "<body>\n" + //
                "  <h1>" + request + "</h1>\n";
        System.out.println("Fetching request: " + request);
        String responses = serverRequest2(request);
        // System.out.println(responses);
        String[] ytId = getYoutubeIds(responses);
        // System.out.println();
        String[] newytId = removeDuplicates(ytId);
        for (int i = 0; i < newytId.length && i < maxVids; i++) {
            //System.out.println(newytId[i]);
            if (newytId[i] != null) {
                fileInput += "<iframe src=\"" + newytId[i]
                        + "\" class=\"youtube-player\" allowfullscreen=\"\" scrolling=\"no\" allow=\"encrypted-media\"></iframe>\n";
            }
        }
        fileInput += "</body>";
        fileMan.out("index.html", fileInput);
        File file = new File("index.html");
        try {
            Desktop.getDesktop().browse(file.toURI());
        } catch (IOException e) {
            System.out.println(
                    "So... kinda awkward but I can't seemm to open the index.html file that I just wrote to...");
            StringBuilder sb = new StringBuilder(file.getAbsolutePath());
            sb.deleteCharAt(2);
            String newFilePath = sb.toString();
            System.out.println(
                    "You can try openning it this way: file://" + newFilePath);
        }
    }

    private static String[] getYoutubeIds(String responses) {
        String[] ytId = new String[maxVids * 10];
        // Regular expression pattern to match YouTube video IDs
        String pattern = "\"videoId\":\"([a-zA-Z0-9_-]{11})\"";
        Pattern compiledPattern = Pattern.compile(pattern);

        // Use Matcher to find all matches in the HTML content
        Matcher matcher = compiledPattern.matcher(responses);
        int j = 0;
        while (j < maxVids * 10 && matcher.find()) {
            ytId[j] = "https://www.youtube.com/embed/" + matcher.group(1);
            // System.out.print(ytId[j] + " ");
            // System.out.println(responses[j]);
            j++;
        }
        return ytId;
    }

    private static String serverRequest2(String Request) {
        System.out.println("Request: ");
        String newRequest = Request.replace(' ', '+');
        // Create a neat value object to hold the URL
        String htmlContent = "";
        try {
            // System.out.println("https://www.youtube.com/results?search_query=" +
            // Request);
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    "https://www.youtube.com/" + search + newRequest)
                            .openConnection();
            InputStream inputStream = connection.getInputStream();
            try (Scanner scanner = new Scanner(inputStream,
                    StandardCharsets.UTF_8).useDelimiter("\\A")) {
                htmlContent = scanner.hasNext() ? scanner.next() : "";
            }
        } catch (IOException e) {
            System.out.println(
                    "Yeah, the server hates me some times. Ignore or try again.");
            e.printStackTrace();
        }

        // Manually converting the response body InputStream to APOD using Jackson
        // Finally we have the response
        return htmlContent;
    }

    static String[] removeDuplicates(String str[]) {
        //System.out.println(str.length);
        String[] str2 = new String[str.length];
        int k = 0;
        String uniVal = "";
        for (int i = 0; i < str.length; i++) {
            if (uniVal != null && !uniVal.equals(str[i])) {
                uniVal = str[i];
                str2[k] = str[i];
                k++;
            }
        }
        return str2;
    }

    public static void main(String[] args) {
        search = "results?search_query=";
        String vidString = fileMan.in("maxVids.txt");
        maxVids = Integer.parseInt(vidString);
        Scanner in = new Scanner(System.in);
        String input;

        do {
            System.out.print("What would you like to search? ");
            input = in.nextLine();
            if (input != null && !input.isEmpty()) {
                if (input.charAt(0) != '/') {
                    makeHtml(input);
                } else {
                    if (input.equals("/vidNum")) {
                        System.out.print(
                                "How many videos would you like to see per page? ");
                        int num = in.nextInt();
                        fileMan.out("maxVids.txt", String.valueOf(num));
                        input = "";
                    } else if (input.equals("/channel")) {
                        System.out.print(
                                "What channel would you like to search? ");
                        search = "@";
                        String channel = in.nextLine();
                        channel.toLowerCase();
                        channel += "/videos";
                        makeHtml(channel);
                    } else {
                        System.out.println(
                                "Sorry, I don't know that command yet");
                    }
                }
            }
        } while (input != null && input != "");
        in.close();
    }
}