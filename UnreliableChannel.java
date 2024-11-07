import java.io.*;
import java.net.*;
import java.util.Random;

public class UnreliableChannel {
    private static final String STATE_FILE = "unreliable_channel_state.txt"; // File to store packet data
    private static final String DELAY_FILE = "delay_data.txt"; // File to store delay data

    public static void SendingThroughServer(int port, DatagramSocket ds, InetAddress ip, byte[] buf, String message,
                                            char server) throws IOException {
        int lost = 0;
        message += " passed through Unreliable Channel";

        for (int i = 0; i < Utilities.packetNumber; i++) {
            buf = message.getBytes();
            DatagramPacket dps = new DatagramPacket(buf, buf.length, ip, port);

            double r = Math.random();
            if (r < Utilities.lossFactor) {
                lost++;  // Increase lost count
            }
                ds.send(dps);  // Send the packet
        }

        Random rand = new Random();
        int d = rand.nextInt(0,2001);
        double delay = d/10.0;

        // Construct the log messages
        String result = "Packets received from user " + server + ": " + Utilities.packetNumber +
                " | Lost: " + lost +
                " | Delayed: " + (Utilities.packetNumber - lost);
        String destination = (server == 'A') ? "B" : "A";
        String delayMessage = "Average delay from " + server + " to " + destination + ": " + delay + " ms.";

        // Write the results to the files
        appendToStateFile(result, STATE_FILE);
        appendToStateFile(delayMessage, DELAY_FILE);

        // Print the file contents if the current server is 'B'
        if (server == 'B') {
            printAndClearStateFile(STATE_FILE);
            printAndClearStateFile(DELAY_FILE);
        }
    }

    private static synchronized void appendToStateFile(String text, String filePath) throws IOException {
        try (FileWriter fw = new FileWriter(filePath, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(text);
        } catch (IOException e) {
            System.err.println("Error writing to " + filePath + ": " + e.getMessage());
            throw e;
        }
    }

    private static void printAndClearStateFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                // Clear file content after printing
                new PrintWriter(filePath).close();  // Clear the file
            } catch (IOException e) {
                System.err.println("Error reading " + filePath + ": " + e.getMessage());
                throw e;
            }
        }
    }
}
