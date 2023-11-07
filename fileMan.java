import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public final class fileMan {
    /**
     * Reads from the file name and returns the string.
     *
     * @param filename
     * @return fileContents
     */
    public static String in(String filename) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
                out(filename, "10");
            }
            permission(file);
            FileInputStream fileInputStream = new FileInputStream(file);
            Scanner in = new Scanner(fileInputStream);

            if (in.hasNextLine()) {
                String input = in.nextLine();
                in.close();
                return input;
            } else {
                // Handle the case where the file is empty
                in.close();
                return "";
            }
        } catch (Exception e) {
            System.out.println("Sry, can't seem to read the file " + filename
                    + " if you wouldn't mind checkind that it exists that would be great.");
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Writes to a file.
     *
     * @param filename
     *            the name of the file you want to write to
     * @param input
     *            the string you want to write to the file
     */
    public static void out(String filename, String input) {
        File file = new File(filename);
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(filename);
            permission(file);
            writer.write(input);
            writer.close();

        } catch (IOException e) {
            System.err.println(
                    "Yeah, I don't know why that happened. The file failed to write properly.");
            e.printStackTrace();
        }
    }

    public static void permission(File myFile) {
        if (!myFile.canRead()) {
            if (!myFile.setReadable(true)) {
                System.out
                        .println("Your permissions is not letting me write to "
                                + myFile.getName());
            }
        }
        if (!myFile.canExecute()) {
            if (!myFile.setExecutable(true)) {
                System.out.print("Can't seem to execute " + myFile.getName());
            }
        }
        if (!myFile.canWrite()) {
            if (!myFile.setWritable(true)) {
                System.out.print("Can't seem to write " + myFile.getName());
            }
        }
    }
}