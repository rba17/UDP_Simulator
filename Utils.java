import java.io.*;
import java.util.*;

public class Utils {

    final static double lossFactor = 0.1;
    final static int packetNumber = 100;
    final static int channelPort = 9999;
    final static int destinationPort = 5000;
    final static int BUFFER_SIZE = 1024;
    final static int packetSize = 512;
    final static String destinationIP_string = "192.168.0.109";
    public static File f = new File("Report.txt");

    public static void printSendTime(char sender, char receiver, long t0, long t1) {
        System.out.println("\nSending packets from " + sender + " to " + receiver + " at elapsed:\t"
                + (t1 - t0) / 1_000_000_000.0 + "s\n");
    }

    public static void printReceiveTime(char sender, char receiver, long t0, long t1) {
        System.out.println("\n" + receiver + " received packets from " + sender + " at elapsed:\t"
                + (t1 - t0) / 1_000_000_000.0 + "s\n");
    }

    public static synchronized void resetFile() throws IOException {
        try (FileWriter out = new FileWriter(f, false)) {
            out.write("");
        }
    }

    public static synchronized void writeToFile(String s) throws IOException {
        try (FileWriter out = new FileWriter(f, true)) {
            out.append(s);
        }
    }

    public static void printFile(File f) throws FileNotFoundException {
        try (Scanner read = new Scanner(f)) {
            while (read.hasNextLine())
                System.out.println(read.nextLine());
        }
    }

    public static String generateRandomMessage(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(rand.nextInt(characters.length())));
        }
        return sb.toString();
    }

}