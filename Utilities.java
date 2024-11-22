import java.io.*;
import java.util.*;

public class Utilities {
    final static long t0 = System.nanoTime();
    final static double lossFactor = 0.3;
    final static int packetNumber = 1000;
    final static int port = 9999;
    final static int clientNumber = 2;
    static File f = new File("Report.txt");
    static ArrayList<Character>[] pairs = new ArrayList[clientNumber];

    public static void addPair(char from, char to) {
        from = Character.toUpperCase(from);
        to = Character.toUpperCase(to);

        if (from - 'A' > clientNumber || to - 'A' > clientNumber)
            System.out.println("End systems available from A to " + (char) (clientNumber + 'A' - 1) + ".");
        else if (pairs[from - 'A'].contains(to) || pairs[to - 'A'].contains(from))
            System.out.println("Pair already exists.");
        else
            pairs[from - 'A'].add(to);

    }

    public static void printSendTime(char server, char destination, long t0, long t1) {
        System.out.println("\nSending packets from " + server + " to " + destination + " at elapsed:\t"
                + (t1 - t0) / 1_000_000_000.0 + "s\n");
    }

    public static void printReceiveTime(char server, char destination, long t0, long t1) {
        System.out
                .println("\n" + destination + " recieved packets from " + server + " at elapsed:\t"
                        + (t1 - t0) / 1_000_000_000.0 + "s\n");
    }

    public static void resetFile() throws IOException {
        FileWriter out = new FileWriter(f, false);
        out.write("");
        out.close();
    }

    public static void writeToFile(String s) throws IOException {
        FileWriter out = new FileWriter(f, true);
        out.append(s);
        out.close();
    }

    public static void printFile(File f) throws FileNotFoundException {
        Scanner read = new Scanner(f);
        while (read.hasNextLine())
            System.out.println(read.nextLine());
        read.close();
    }

}