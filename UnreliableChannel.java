import java.io.*;
import java.net.*;
import java.util.Random;

public class UnreliableChannel {

    private static double totalDelay = 0;
    private static double successfulSends = 0;
    private static int lost = 0;

    public static void SendingThroughServer(int port, DatagramSocket ds, InetAddress ip, byte[] buf, String message,
            char server)
            throws IOException {

        Random rand = new Random();
        char destination = (server == 'A') ? 'B' : 'A';

        Utilities.printSendTime(server, destination, Utilities.t0, System.nanoTime());

        for (int i = 0; i < Utilities.packetNumber; i++) {
            buf = message.getBytes();
            DatagramPacket dps = new DatagramPacket(buf, buf.length, ip, port);
            double r = Math.random();

            if (r < Utilities.lossFactor)
                lost++;

            else {
                int d = rand.nextInt(0, 2001);
                double delay = d / 10.0;
                totalDelay += delay;
                successfulSends++;
            }
            ds.send(dps);
        }

        Utilities.printReceiveTime(server, destination, Utilities.t0, System.nanoTime());

        double averageDelay = successfulSends > 0 ? totalDelay / successfulSends : 0;

        String result = "Packets received from user " + server + ": " + Utilities.packetNumber +
                " | Lost: " + lost +
                " | Delayed: " + (Utilities.packetNumber - lost) + "\n";

        String delayMessage = "Average delay from " + server + " to " + destination + ": " + averageDelay + " ms.\n\n";

        Utilities.writeToFile(result);

        Utilities.writeToFile(delayMessage);
    }
}