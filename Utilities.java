import java.io.*;
import java.util.*;

public class Utilities {
    final static long t0 = System.nanoTime();
    final static double lossFactor = 0.3;
    final static int packetNumber = 1000;
    final static int clientNumber = 3;
    final static int channelPort = 9999;
    final static int BUFFER_SIZE = 1024;

    static File f = new File("Report.txt");
    @SuppressWarnings("unchecked")
    static ArrayList<Character>[] pairs = new ArrayList[clientNumber];

    static {
        for (int i = 0; i < clientNumber; i++) {
            pairs[i] = new ArrayList<>();
        }

        addPair('A', 'B');
        addPair('B', 'C');
        addPair('C', 'A');

    }

    public static void addPair(char from, char to) {
        from = Character.toUpperCase(from);
        to = Character.toUpperCase(to);

        if (from - 'A' >= clientNumber || to - 'A' >= clientNumber) {
            System.out.println("End systems available from A to " + (char) ('A' + clientNumber - 1) + ".");
        } else if (pairs[from - 'A'].contains(to) || pairs[to - 'A'].contains(from)) {
            System.out.println("Pair already exists.");
        } else {
            pairs[from - 'A'].add(to);
        }
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

    public static int countPairs() {
        int count = 0;
        for (int i = 0; i < clientNumber; i++)
            count += pairs[i].size();
        return count;
    }

}